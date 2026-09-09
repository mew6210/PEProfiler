package org.example.presentation;

import org.example.Analysis.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MarkDownPresenter implements InsightPresenter{
    static final String markDownFileName = "report.md";

    @Override
    public void present(List<Insight> insights) {
        try{
            writeToMdFile(insights);
        }
        catch(IOException ioex){
            printCouldNotWriteToFileError(ioex);
        }
    }

    private void writeToMdFile(List<Insight> insights) throws IOException {

        try (FileWriter mdFileWriter = new FileWriter(markDownFileName)){

            mdFileWriter.write("# Platinum Eyes Performance Report\n\n## Summary:\n");
            for (Insight insight1 : insights) {
                if (insight1 instanceof SummaryInsight) {
                    writeToMdFileSummaryInsight(insight1, mdFileWriter);
                }
            }

            for (Insight insight1 : insights) {
                if (insight1 instanceof AverageTimestampsInsight) {
                    writeToMdFileAverageTimestampsInsight(insight1, mdFileWriter);
                }
            }

            List<Insight> itemCountMismatchInsights = insights
                    .stream()
                    .filter(insight -> insight instanceof ItemCountMismatchInsight)
                    .toList();

            List<Insight> itemReadMismatchInsights = insights
                    .stream()
                    .filter(insight -> insight instanceof ItemReadMismatchInsight)
                    .toList();

            writeToMdFileItemCountMismatchInsights(itemCountMismatchInsights,mdFileWriter);
            writeToMdFileItemReadMismatchInsights(itemReadMismatchInsights,mdFileWriter);
        }

    }

    private void writeToMdFileItemReadMismatchInsights(List<Insight> itemReadMismatchInsights, FileWriter mdFileWriter) throws IOException {

        Map<Path,List<ItemReadMismatchInsight>> insightsGroupedByFilename =
                itemReadMismatchInsights
                .stream()
                .map(ItemReadMismatchInsight.class::cast)
                .collect(Collectors.groupingBy(
                        ItemReadMismatchInsight::fileName,
                        TreeMap::new,
                        Collectors.toList()));

        mdFileWriter.write("## Item read mismatches: <br>\n");
        for(var entry: insightsGroupedByFilename.entrySet()){
            mdFileWriter.write("### "+entry.getKey().toString()+": <br>\n");
            mdFileWriter.write("!["+entry.getKey()+"](collections/1/"+entry.getKey()+")\n");
            for(var mismatch: entry.getValue()){
                mdFileWriter.write(" - **Read:** `"+mismatch.readItem()+"`\n\t - **Possible options:** `"+mismatch.getPrettyPossibleMatches() + "`\n");
            }
        }


    }

    private void writeToMdFileItemCountMismatchInsights(List<Insight> itemCountMismatchInsights, FileWriter mdFileWriter) throws IOException {

        List<ItemCountMismatchInsight> itemCountMismatchInsightsCastedList = itemCountMismatchInsights.
                stream().
                map(ItemCountMismatchInsight.class::cast).
                toList();

        mdFileWriter.write("<br><br>\n");
        mdFileWriter.write("## Item count mismatches: <br>\n");
        for(var itemCountMismatchInsight : itemCountMismatchInsightsCastedList){
            mdFileWriter.write("### File: "+itemCountMismatchInsight.fileName()+"\n");
            mdFileWriter.write("!["+itemCountMismatchInsight.fileName()+"](collections/1/"+itemCountMismatchInsight.fileName()+")\n");
            mdFileWriter.write("- "+"Estimated: "+itemCountMismatchInsight.estimatedCount()
                            +" Correct: "+ itemCountMismatchInsight.correctCount() + "<br>\n");
        }

    }

    private void writeToMdFileAverageTimestampsInsight(Insight insight, FileWriter mdFileWriter) throws IOException {
        AverageTimestampsInsight avtmInsight = (AverageTimestampsInsight) insight;
        mdFileWriter.write("## Average time spent on an action:\n");
        for(Map.Entry<String,Integer> entry : avtmInsight.timestamps().entrySet()){
            mdFileWriter.write("- **"+entry.getKey()+"**: "+entry.getValue()+"ms\n");
        }

    }

    private void writeToMdFileSummaryInsight(Insight insight, FileWriter mdFileWriter) throws IOException {
        SummaryInsight smInsight = (SummaryInsight) insight;
        int allRuns = smInsight.goodScreenshotReadings() +
                smInsight.badScreenshotReadings() +
                smInsight.badScreenshotItemCountReadings();
        String goodPercentage = String.format("%.2f", (double)smInsight.goodScreenshotReadings()/allRuns *100);
        String badPercentage = String.format("%.2f", (double)smInsight.badScreenshotReadings()/allRuns *100);
        String badCountPercentage = String.format("%.2f", (double)smInsight.badScreenshotItemCountReadings()/allRuns *100);

        mdFileWriter.write("Correct readings: "+ smInsight.goodScreenshotReadings()
                + "/"+ allRuns
                +"("+goodPercentage+"%) <br>");

        mdFileWriter.write("Bad screenshot readings: "+ smInsight.badScreenshotReadings()
                + "/"+allRuns
                +"("+badPercentage+"%) <br>");
        mdFileWriter.write("Bad screenshot item count readings: "
                + smInsight.badScreenshotItemCountReadings()
                + "/"+allRuns
                +"("+badCountPercentage+"%)"+"<br><br>\n");
    }

    private void printCouldNotWriteToFileError(IOException ioex){
        System.out.println("Could not write to "+ markDownFileName+", error: "+ioex);
    }

}
