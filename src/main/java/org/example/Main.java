package org.example;

import org.example.PE.PEProcess;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main {
    static void main() {

        try(PEProcess proc = new PEProcess(pathToPE())){
            proc.open();
            Thread.sleep(2000);
            proc.pressPreviousScreenshot();
            Thread.sleep(7000);
            var events = proc.getEvents();
            for(var event : events){
                System.out.println("Event id: "+event.id()+ " Event items: ");
                for(var item: event.items()){
                    System.out.print(item+" ");
                }
            }
        }
        catch(IOException IOe){
            IOe.printStackTrace();
        } catch (InterruptedException | AWTException e) {
            throw new RuntimeException(e);
        }


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
