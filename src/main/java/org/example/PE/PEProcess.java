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
    private volatile Path currentlyReadFile;
    private volatile boolean isReadyToRead = false;

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
        isReadyToRead = true;
    }

    @Override
    public void close(){
        if(process != null && process.isAlive())
            process.destroy();
        System.out.println("Process closed");
    }

    public void pressPreviousScreenshot(Path expectedFile) throws AWTException{
        isReadyToRead = false;
        this.currentlyReadFile = expectedFile;
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
        char currentItemCount = '4';

        try(BufferedReader reader = process.inputReader()){
            String line;
            while((line = reader.readLine())!=null){
                System.out.println(line);

                if(line.startsWith("reading items for count ")){
                      currentItemCount = line.charAt(line.length() -1);
                }

                if(line.startsWith("Reading result: ")){
                    String itemName = line.substring(line.indexOf(":")+2);
                    items.add(currentItemCount + ":" + itemName);
                }

                if(line.startsWith("[") &&
                        Character.isDigit(line.charAt(1))){
                    Integer ms = Integer.parseInt(line.substring(1,line.indexOf("ms")));
                    String eventName = line.substring(line.indexOf("]")+2);
                    timestamps.add(new EventTimestamp(eventName,ms));
                }
                if(line.startsWith("[+] Successfully copied prices to clipboard, avalible to paste them in the chat with ctrl + v")){
                    isReadyToRead = true;
                }

                if(line.startsWith("[+] Successfully initialized tesseract")){
                    isReadyToRead = true;
                }

                if(line.startsWith("Item prices:")){
                    events.add(
                            new ReadEvent(
                                    id,
                                    new ArrayList<>(timestamps),
                                    new ArrayList<>(items),
                                    currentlyReadFile
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

    public boolean isReady(){
        return isReadyToRead;
    }

    public List<ReadEvent> getEvents(){
        return List.copyOf(events);
    }

    public void addErrorEvent(Path screenshot){
        events.add(new ReadEvent(-1,null,null,screenshot));
    }
}
