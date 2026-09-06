package org.example.Analysis;

import org.example.PE.ReadEvent;

public record EventMatch(
        ReadEvent read,
        ReadTargetEvent target

) {
}
