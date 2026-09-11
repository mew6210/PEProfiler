package org.example.userConfig;

import org.example.PE.PEManagerType;
import org.example.presentation.InsightPresenterType;

import java.nio.file.Path;

public class Config {
    private int collectionIndex = 1;
    private Path pathToPE = null;
    private PEManagerType processManager = PEManagerType.Cropped;
    private InsightPresenterType InsightPresenter = InsightPresenterType.MarkDown;

    public Config(String... args) {

    }

    public int getCollectionIndex() {
        return collectionIndex;
    }

    public Path getPathToPE() {
        return pathToPE;
    }

    public PEManagerType getProcessManager() {
        return processManager;
    }

    public InsightPresenterType getInsightPresenter() {
        return InsightPresenter;
    }
}
