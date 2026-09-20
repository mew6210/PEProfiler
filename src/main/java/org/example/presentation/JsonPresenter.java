package org.example.presentation;

import org.example.Analysis.AverageTimestampsInsight;
import org.example.Analysis.Insight;
import org.example.Analysis.ItemReadMismatchInsight;
import org.example.Analysis.SummaryInsight;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonPresenter implements InsightPresenter {
    static final String jsonFileName = "report.json";

    @Override
    public void present(List<Insight> insights) {
        try{
            writeToJsonFile(insights);
        }catch(IOException ioex){
            printCouldNotWriteToFileError(ioex);
        }
    }

    private void writeToJsonFile(List<Insight> insights) throws IOException {
        try(FileWriter writer = new FileWriter(jsonFileName)){
            writer.write("{\n");
            for(Insight insight : insights){
                if(insight instanceof SummaryInsight){
                    writeToJsonFileSummary(insight,writer);
                }
            }
            for(Insight insight : insights){
                if(insight instanceof AverageTimestampsInsight){
                    writeToJsonFileTimestamps(insight,writer);
                }
            }
            List<ItemReadMismatchInsight> itemReadMismatchInsightList = insights.
                    stream().
                    filter(insight -> insight instanceof ItemReadMismatchInsight).
                    map(insight -> (ItemReadMismatchInsight) insight).
                    toList();

            writeToJsonFileItemReadMismatchInsights(itemReadMismatchInsightList,writer);



            writer.write("\n}\n");
        }
    }

    //TODO: make it grouped by filename
    private void writeToJsonFileItemReadMismatchInsights(List<ItemReadMismatchInsight> itemReadMismatchInsightList, FileWriter writer) throws IOException {
        Iterator<ItemReadMismatchInsight> iterator = itemReadMismatchInsightList.iterator();
        writer.write("\"Bad reads\": [\n");
        while(iterator.hasNext()){
            writer.write("{");
            var mismatch = iterator.next();
            String possibleMatches = prettyBadReadNameToJsonElement(mismatch.getPrettyPossibleMatches());
            writer.write("\"Read\": \""+mismatch.readItem() +"\","+" \"Possible items\": "+ possibleMatches + ", \"Filename\": \""+mismatch.fileName()+"\"");

            writer.write("}");
            if(iterator.hasNext()){
                writer.write(",");
            }
            writer.write("\n");
        }


        writer.write("]\n");
    }

    //converts a string "[itemName1,itemName2,...]" to a string "[\"itemName1\",\"itemName2\",...]"
    private String prettyBadReadNameToJsonElement(String s){
        if (s == null || s.length() < 2) {
            return s;
        }

        String content = s.substring(1, s.length() - 1);

        if (content.isEmpty()) {
            return "[]";
        }

        return "[\"" + content.replace(",", "\",\"") + "\"]";
    }

    private void writeToJsonFileTimestamps(Insight timestamps, FileWriter writer) throws IOException {
        AverageTimestampsInsight timestampsInsight = (AverageTimestampsInsight) timestamps;

        Iterator<Map.Entry<String,Integer>> iterator = timestampsInsight.timestamps().entrySet().iterator();
        writer.write("\"timestamps\":{\n");
        while(iterator.hasNext()){
            var entry = iterator.next();
            writer.write("\t\""+entry.getKey()+"\": "+entry.getValue());
            if(iterator.hasNext()){
                writer.write(",");
            }
            writer.write("\n");
        }

        writer.write("\t},\n");
    }

    private void writeToJsonFileSummary(Insight summary, FileWriter writer) throws IOException{
        SummaryInsight summaryInsight = (SummaryInsight) summary;
        int totalCount =
                        summaryInsight.goodScreenshotReadings()
                        + summaryInsight.badScreenshotReadings()
                        + summaryInsight.badScreenshotItemCountReadings();

        writer.write("\"summary\": {");
        writer.write("\n\t\"total: \": "+totalCount+",");
        writer.write("\n\t\"good\": " + summaryInsight.goodScreenshotReadings()+",");
        writer.write("\n\t\"bad readings\": " + summaryInsight.badScreenshotReadings()+",");
        writer.write("\n\t\"bad count readings\": " + summaryInsight.badScreenshotItemCountReadings());

        writer.write("\t\n},\n");

    }

    private void printCouldNotWriteToFileError(IOException ioex){
        System.out.println("Could not write to "+ jsonFileName+", error: "+ioex);
    }
}
