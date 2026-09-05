package org.example.Analysis;

public record SummaryInsight(
        int correctReadings,
        int badReadings,
        int badCountReadings
) implements Insight {
    @Override
    public Severity severity() {
        return Severity.Info;
    }

    @Override
    public String title() {
        return "Brief report on numbers";
    }

    @Override
    public InsightType type() {
        return InsightType.Summary;
    }
}
