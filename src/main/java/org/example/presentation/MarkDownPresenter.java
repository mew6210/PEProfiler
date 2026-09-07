package org.example.presentation;

import org.example.Analysis.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
