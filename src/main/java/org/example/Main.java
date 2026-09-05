package org.example;

import org.example.Analysis.ReadDataAnalyzer;
import org.example.PE.PECroppedManager;
import org.example.PE.PEManager;
import org.example.PE.PEProcess;
import org.example.PE.ReadEvent;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public class Main {
    static void main() {
        List<ReadEvent> data;
        try(PEManager manager =
                    new PECroppedManager(
                            new PEProcess(pathToPE()))
        ){
            manager.readCollection(1);
            data = manager.getData();
        }
        ReadDataAnalyzer analyzer = new ReadDataAnalyzer(data,1);
        var insights = analyzer.analyze();

    }

    static String pathToPE(){

        Properties config = new Properties();
        try (FileInputStream input = new FileInputStream("config.properties")) {
            config.load(input);
            String userPath = config.getProperty("path","");
            if(!userPath.isBlank()) //user gave a string, so we trust it
                return userPath;
        } catch (IOException e) {
            System.out.println("Could not load config.properties, trying common paths for PE executable");
        }

        //otherwise, try some common paths in the current directory
        String[] paths = {
                "Platinum-Eyes/Platinum_Eyes.exe",
                "Platinum_Eyes/Platinum_Eyes.exe"
        };

        for(String path : paths){
            if(Files.isRegularFile(Path.of(path))){
                return path;
            }
        }

        throw new RuntimeException("no path found, either add Platinum-Eyes/Platinum_Eyes.exe to a directory or add path to config.properties");
    }
}
