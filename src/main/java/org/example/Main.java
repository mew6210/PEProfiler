package org.example;

import org.example.Analysis.ReadDataAnalyzer;
import org.example.PE.PECroppedManager;
import org.example.PE.PEManager;
import org.example.PE.PEProcess;
import org.example.PE.ReadEvent;
import org.example.presentation.InsightPresenter;
import org.example.presentation.MarkDownPresenter;
import org.example.userConfig.Config;

import java.util.List;

public class Main {
    static void main(String... args) {
        Config userConfig = new Config(args);
        List<ReadEvent> data;
        try(PEManager manager =
                    new PECroppedManager(
                            new PEProcess(userConfig.getPathToPE()))
        ){
            manager.readCollection(userConfig.getCollectionIndex());
            data = manager.getData();
        }
        ReadDataAnalyzer analyzer = new ReadDataAnalyzer(data,userConfig.getCollectionIndex());
        var insights = analyzer.analyze();
        InsightPresenter presenter = new MarkDownPresenter();
        presenter.present(insights);
    }


}
