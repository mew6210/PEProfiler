package org.example.presentation;

import org.example.Analysis.*;

import java.io.File;
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
        File mdFile = new File(markDownFileName);
        try {
            if(mdFile.createNewFile()){
                writeToMdFile(insights);
            }
            else{
                //TODO: prompt user what to do
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    private void writeToMdFile(List<Insight> insights){

        try (FileWriter mdFileWriter = new FileWriter(markDownFileName)){

            mdFileWriter.write("# Platinum Eyes Performance Report\n\n## Summary:\n");
            insights.forEach(insight -> {
                        if(insight instanceof SummaryInsight){
                            writeToMdFileSummaryInsight(insight,mdFileWriter);
                        }
                    });
            insights.forEach(insight -> {
                if(insight instanceof AverageTimestampsInsight){
                    writeToMdFileAverageTimestampsInsight(insight,mdFileWriter);
                }

            });

            List<Insight> itemCountMismatchInsights = insights.stream().filter(insight -> insight instanceof ItemCountMismatchInsight).toList();
            List<Insight> itemReadMismatchInsights = insights.stream().filter(insight -> insight instanceof ItemReadMismatchInsight).toList();

            writeToMdFileItemCountMismatchInsights(itemCountMismatchInsights,mdFileWriter);
            writeToMdFileItemReadMismatchInsights(itemReadMismatchInsights,mdFileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void writeToMdFileItemReadMismatchInsights(List<Insight> itemReadMismatchInsights, FileWriter mdFileWriter) {

        Map<Path,List<ItemReadMismatchInsight>> insightsGroupedByFilename =
                itemReadMismatchInsights
                .stream()
                .map(ItemReadMismatchInsight.class::cast)
                .collect(Collectors.groupingBy(
                        ItemReadMismatchInsight::fileName,
                        TreeMap::new,
                        Collectors.toList()));

        try {
            mdFileWriter.write("## Item read mismatches: <br>\n");
            for(var entry: insightsGroupedByFilename.entrySet()){
                mdFileWriter.write("### "+entry.getKey().toString()+": <br>\n");
                for(var mismatch: entry.getValue()){
                    mdFileWriter.write(" - **Read:** `"+mismatch.readItem()+"`\n\t - **Possible options:** `"+mismatch.getPrettyPossibleMatches() + "`\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void writeToMdFileItemCountMismatchInsights(List<Insight> itemCountMismatchInsights, FileWriter mdFileWriter) {

        List<ItemCountMismatchInsight> itemCountMismatchInsightsCastedList = itemCountMismatchInsights.
                stream().
                map(ItemCountMismatchInsight.class::cast).
                toList();
        //Map<Path, List<ItemCountMismatchInsight>> groupedMap = itemCountMismatchInsightsCastedList.stream().collect(Collectors.groupingBy(ItemCountMismatchInsight::fileName));

        try {
            mdFileWriter.write("<br><br>\n");
            mdFileWriter.write("## Item count mismatches: <br>\n");
            for(var itemCountMismatchInsight : itemCountMismatchInsightsCastedList){
                    mdFileWriter.write(" - "+"Estimated: "+itemCountMismatchInsight.estimatedCount()
                            +" Correct count: "+ itemCountMismatchInsight.correctCount()
                            + " : "+itemCountMismatchInsight.fileName()+ "<br>\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void writeToMdFileAverageTimestampsInsight(Insight insight, FileWriter mdFileWriter) {
        AverageTimestampsInsight avtmInsight = (AverageTimestampsInsight) insight;
        try {
            mdFileWriter.write("## Average time spent on an action:\n");
            for(Map.Entry<String,Integer> entry : avtmInsight.timestamps().entrySet()){
                mdFileWriter.write("- **"+entry.getKey()+"**: "+entry.getValue()+"ms\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void writeToMdFileSummaryInsight(Insight insight, FileWriter mdFileWriter){
        SummaryInsight smInsight = (SummaryInsight) insight;
        int allRuns = smInsight.goodScreenshotReadings() +
                smInsight.badScreenshotReadings() +
                smInsight.badScreenshotItemCountReadings();
        try {
            mdFileWriter.write("Correct readings: "+ smInsight.goodScreenshotReadings() + "/"+ allRuns+"<br>");
            mdFileWriter.write("Bad screenshot readings: "+ smInsight.badScreenshotReadings()+ "/"+allRuns+"<br>");
            mdFileWriter.write("Bad screenshot item count readings: "+ smInsight.badScreenshotItemCountReadings()+ "/"+allRuns+"<br><br>\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
