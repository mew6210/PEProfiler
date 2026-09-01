package org.example.PE;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PEProcess implements AutoCloseable {
    public final Path pathToPE;
    private Process process;
    private List<ReadEvent> events = Collections.synchronizedList(new ArrayList<>());

    public PEProcess(String PEPath){
        try{
            this.pathToPE=Path.of(PEPath);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid path: "+PEPath);
        }

        if(!Files.isRegularFile(this.pathToPE)) {
            throw new IllegalArgumentException("Executable does not exist at this path: " + this.pathToPE);
        }
    }

    public void open() throws IOException {
        if(process != null && process.isAlive())
            throw new IllegalStateException("Process is already running");


        ProcessBuilder pb = new ProcessBuilder(pathToPE.toString());

        pb.directory(pathToPE.toAbsolutePath().getParent().toFile());

        Thread outputThread = new Thread(this::listenToProcessOutput);

        process = pb.start();
        outputThread.start();
        System.out.println("Process opened");
    }

    @Override
    public void close(){
        if(process != null && process.isAlive())
            process.destroy();
        System.out.println("Process closed");
    }

    public void pressPreviousScreenshot() throws AWTException{
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_X);

        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(KeyEvent.VK_X);
    }

    void listenToProcessOutput(){
        Integer id = 1;
        ArrayList<String> items = new ArrayList<>();
        ArrayList<EventTimestamp> timestamps = new ArrayList<>();

        try(BufferedReader reader = process.inputReader()){
            String line;
            while((line = reader.readLine())!=null){
                System.out.println(line);

                if(line.startsWith("Reading result: ")){
                    String itemName = line.substring(line.indexOf(":")+2);
                    items.add(itemName);
                }

                if(line.startsWith("[") &&
                        Character.isDigit(line.charAt(1))){
                    Integer ms = Integer.parseInt(line.substring(1,line.indexOf("ms")));
                    String eventName = line.substring(line.indexOf("]")+2);
                    timestamps.add(new EventTimestamp(eventName,ms));
                }

                if(line.startsWith("Item prices:")){
                    events.add(
                            new ReadEvent(
                                    id,
                                    new ArrayList<>(timestamps),
                                    new ArrayList<>(items)
                            ));
                    timestamps.clear();
                    items.clear();
                    id++;
                }

            }
        }catch(IOException exc){
            exc.printStackTrace();
        }
    }

    public List<ReadEvent> getEvents(){
        return List.copyOf(events);
    }
}
