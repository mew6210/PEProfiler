package org.example.Analysis;

import java.util.List;

public record ReadTargetEvent(
        String fileName,
        List<String> items
) {
}
