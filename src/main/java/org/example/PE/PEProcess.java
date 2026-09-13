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
import java.util.Scanner;

public class PEProcess implements AutoCloseable {
    public final Path pathToPE;
    private Process process;
    private int keyCodePreviousScreenshot;
    private List<ReadEvent> events = Collections.synchronizedList(new ArrayList<>());
    private volatile Path currentlyReadFile;
    private volatile boolean isReadyToRead = false;

    public PEProcess(Path PEPath){
        try{
            this.pathToPE=PEPath;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid path: "+PEPath);
        }

        if(!Files.isRegularFile(this.pathToPE)) {
            throw new IllegalArgumentException("Executable does not exist at this path: " + this.pathToPE);
        }
        initKeycodes();
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

    public void pressPreviousScreenshot(Path expectedFile) throws AWTException{
        isReadyToRead = false;
        this.currentlyReadFile = expectedFile;
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(keyCodePreviousScreenshot);

        robot.keyRelease(KeyEvent.VK_ALT);
        robot.keyRelease(keyCodePreviousScreenshot);
    }

    void listenToProcessOutput(){
        Integer id = 1;
        ArrayList<String> items = new ArrayList<>();
        ArrayList<EventTimestamp> timestamps = new ArrayList<>();
        char currentItemCount = '4';

        try(BufferedReader reader = process.inputReader()){
            String line;
            while((line = reader.readLine())!=null){
                System.out.println(line); //TODO: add entire log into an Event

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

    private void initKeycodes(){
        assert pathToPE != null;

        if(!Files.exists(pathToPE.getParent().resolve("tool_config.txt"))){
            keyCodePreviousScreenshot = KeyEvent.VK_X;
            return;
        }

        int keycodeFromToolConfig = getPreviousScreenshotKeyCodeFromToolConfig(pathToPE.getParent().resolve("tool_config.txt"));
        if(keycodeFromToolConfig == -1){
            keyCodePreviousScreenshot = KeyEvent.VK_X;
            return;
        }

        keyCodePreviousScreenshot = KeyEvent.getExtendedKeyCodeForChar(keycodeFromToolConfig);
    }

    private int getPreviousScreenshotKeyCodeFromToolConfig(Path pathToToolConfig) {

        Scanner scanner;
        try {
            scanner = new Scanner(pathToToolConfig);
        } catch (IOException e) {
            return -1;
        }

        while(scanner.hasNextLine()){
            String line = scanner.nextLine();

            if(line.startsWith("keyBind_ReadPreviousItems:")){
                return line.charAt(27);
            }
        }
        return -1;
    }
}
