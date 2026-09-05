package org.example.Analysis;

import java.nio.file.Path;

public record ItemCountMismatch(
        int estimatedCount,
        int correctCount,
        Path fileName
) implements Insight {
    @Override
    public Severity severity() {
        return Severity.Error;
    }

    @Override
    public String title() {
        return "Wrong amount of items estimated";
    }

    @Override
    public InsightType type() {
        return InsightType.ItemCountMismatch;
    }
}
