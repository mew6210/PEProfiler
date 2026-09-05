package org.example.Analysis;

import java.util.Map;

public record AverageTimestampsInsight(
        Map<String,Integer> timestamps
) implements Insight {
    @Override
    public Severity severity() {
        return Severity.Info;
    }

    @Override
    public String title() {
        return "Average time taken for each action";
    }

    @Override
    public InsightType type() {
        return InsightType.AverageTimestamps;
    }
}
