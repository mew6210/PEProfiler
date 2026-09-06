package org.example.Analysis;

public record SummaryInsight(
        int goodScreenshotReadings,
        int badScreenshotReadings,
        int badScreenshotItemCountReadings
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
