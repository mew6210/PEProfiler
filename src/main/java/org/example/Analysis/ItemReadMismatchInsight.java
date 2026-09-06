package org.example.Analysis;

import java.nio.file.Path;
import java.util.List;

public record ItemReadMismatchInsight(String readItem, List<String> possibleMatches, Path fileName) implements Insight {
    @Override
    public Severity severity() {
        return Severity.Error;
    }

    @Override
    public String title() {
        return "Item Read Mismatch";
    }

    @Override
    public InsightType type() {
        return InsightType.ItemReadMismatch;
    }
}
