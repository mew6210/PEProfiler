package org.example.Analysis;

import org.example.PE.EventTimestamp;
import org.example.PE.ReadEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ReadDataAnalyzer {

    private final List<ReadEvent> data;
    private List<ReadTargetEvent> targetData;

    public ReadDataAnalyzer(List<ReadEvent> data,int collectionIndex){
        this.data = data;
        getCollectionTargetData(collectionIndex);
    }

    public List<ReadTargetEvent> getTargetData(){
        return targetData;
    }

    private void getCollectionTargetData(int collectionIndex){

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
                    fileName = line.substring(6);
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

            this.targetData = targetEvents;

        } catch (FileNotFoundException e) {
            System.out.println("Could not find collection.txt in collection: "+ collectionIndex);
            e.printStackTrace();
        }

    }
    public void analyze(){
        Map<String, Integer> timestampMap = new TreeMap<>();
        for(ReadEvent datum : data){
            for(EventTimestamp stamp: datum.timestamps()){
                timestampMap.put( //could be timestampMap.merge(...)
                        stamp.eventName(),
                        timestampMap.getOrDefault(stamp.eventName(),0)+ stamp.ElapsedMs()
                );
            }
        }
    }

}
