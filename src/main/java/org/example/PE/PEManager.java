package org.example.PE;

import java.util.List;

public interface PEManager extends AutoCloseable {
    void readCollection(int collectionIndex);
    List<ReadEvent> getData();
    void close();
}
