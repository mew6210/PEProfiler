package org.example.Analysis;

import org.example.PE.EventTimestamp;
import org.example.PE.ReadEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReadDataAnalyzer {

    private final List<ReadEvent> data;
    private final List<ReadTargetEvent> targetData;
    private final List<Insight> insights = new ArrayList<>();
    public ReadDataAnalyzer(List<ReadEvent> data,int collectionIndex){
        this.data = data;
        this.targetData = getCollectionTargetData(collectionIndex);
    }

    private List<ReadTargetEvent> getCollectionTargetData(int collectionIndex){

        Path collection = Path.of("collections").resolve(Integer.toString(collectionIndex));
        if(!Files.isDirectory(collection)) throw new IllegalStateException("No such collection exists: "+collectionIndex);
        Path collectionsTxt = collection.resolve(Path.of("collection.txt"));

        File colTxt = new File(collectionsTxt.toUri());
        try(Scanner fileReader = new Scanner(colTxt)){
            List<ReadTargetEvent> targetEvents = new ArrayList<>();
            String fileName = "";
            List<String> items = new ArrayList<>();
            while(fileReader.hasNext()){
                String line = fileReader.nextLine();


                if(line.startsWith("file: ")){
                    fileName = line.substring(6)+".bmp";
                }

                if(line.startsWith("item")){
                    items.add(line.substring(7));
                }

                if(line.isBlank()) {
                    targetEvents.add(new ReadTargetEvent(fileName, new ArrayList<>(items)));
                    fileName = "";
                    items.clear();
                }

            }
            if(!fileName.isBlank() &&
                            !items.isEmpty())
                targetEvents.add(new ReadTargetEvent(fileName,new ArrayList<>(items)));

            return targetEvents;
        } catch (FileNotFoundException e) {
            System.out.println("Could not find collection.txt in collection: "+ collectionIndex);
            e.printStackTrace();
        }

        return List.of();
    }
    public List<Insight> analyze(){
        analyzeTimestamps();
        analyzeItems();
        return insights;
    }

    private void analyzeTimestamps(){
        Map<String,TimestampAggregate> timestampMap = new TreeMap<>();
        for(ReadEvent datum : data){
            for(EventTimestamp stamp: datum.timestamps()){

                var current = timestampMap.getOrDefault(stamp.eventName(),new TimestampAggregate(0,0));
                timestampMap.put( //could be timestampMap.merge(...)
                        stamp.eventName(),
                        new TimestampAggregate(current.aggregate() + stamp.ElapsedMs(),current.count()+1)
                );
            }
        }
        Map<String,Integer> avgTimestampMap = new TreeMap<>();
        for(var entry : timestampMap.entrySet()){
            var val = entry.getValue();
            avgTimestampMap.put(entry.getKey(), val.aggregate()/val.count());
        }
        insights.add(new AverageTimestampsInsight(avgTimestampMap));
    }
    private void analyzeItems(){

        if(data.size() != targetData.size()){
            System.out.printf("Incorrect sizes of data to targetData - data.size(): %d, targetData.size(): %d\n",
                    data.size(),
                    targetData.size()
            );
        }
        List<EventMatch> matches = getMatches();

        for(EventMatch match : matches){
            if(match.read().getItemCount() != match.target().items().size()){
                System.out.printf("Incorrect sizes of items read and items targeted - read.items.size(): %d, target.items.size(): %d, file affected: %s\n",
                        match.read().getItemCount(),
                        match.target().items().size(),
                        match.target().fileName()
                );
                continue;
            }
            List<String> unfoundItems = new ArrayList<>();
            List<String> readItems = new ArrayList<>(match.read().getParsedItemsSatisfyingItemCount());

            for(String item : match.target().items()){
                int indexMatch = -1;
                for(int i = 0;i<readItems.size();i++){
                    if(item.equalsIgnoreCase(readItems.get(i))){
                        indexMatch = i;
                        break;
                    }
                }
                if(indexMatch != -1){
                    readItems.remove(indexMatch);
                }
                else {
                    unfoundItems.add(item);
                }
            }

            for(String readItem : readItems){
                insights.add(new ItemReadMismatch(readItem,unfoundItems, Path.of(match.target().fileName())));
            }

            //for(String unfoundItem : unfoundItems){
            //    System.out.println("This item is in targetData but was not found" + unfoundItem + " File affected: " + match.target().fileName());
            //}


        }

    }

    private List<EventMatch> getMatches(){
        Map<String, ReadTargetEvent> targetsByFileName =
                targetData.stream()
                        .collect(Collectors.toMap(
                                ReadTargetEvent::fileName,
                                Function.identity()
                        ));

        return data.stream()
                .map(event -> {
                    String fileName = event.pathToFileRead().getFileName().toString();
                    ReadTargetEvent target = targetsByFileName.get(fileName);

                    return target != null
                            ? new EventMatch(event,target)
                            : null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
