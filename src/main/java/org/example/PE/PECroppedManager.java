package org.example.PE;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class PECroppedManager implements PEManager,AutoCloseable {
    PEProcess process;

    public PECroppedManager(PEProcess process){
        this.process = process;

        try {
            this.process.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readCollection(int collectionIndex) {
        Path collection = Paths.get("collections/"+collectionIndex);
        if(!Files.isDirectory(collection)) throw new IllegalStateException("No such collection exists: "+collectionIndex);

        List<Path> screenshotFiles = getFilesFromCollection(collection);

        screenshotFiles.sort(Comparator.comparing(Path::getFileName));
        screenshotFiles.forEach(this::readScreenshot);
    }

    private List<Path> getFilesFromCollection(Path collection){
        List<Path> filesCollection = new ArrayList<>();
        try(DirectoryStream<Path> files = Files.newDirectoryStream(collection)){
            for(Path file: files){
                if(file.getFileName().toString().endsWith(".bmp")){
                    filesCollection.add(file);
                }
            }
        } catch(IOException ioe){
            throw new IllegalStateException("could not open collection: "+ collection,ioe);
        }
        return filesCollection;
    }

    @Override
    public List<ReadEvent> getData() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return process.getEvents();
    }

    void readScreenshot(Path screenshot) {
        try {
            while(!process.isReady()) Thread.sleep(100);
            Thread.sleep(200);
            Path target = process.pathToPE.getParent().resolve("screenshot.bmp");
            Files.copy(screenshot,target, StandardCopyOption.REPLACE_EXISTING);
            process.pressPreviousScreenshot(screenshot);
        } catch (IOException | InterruptedException | AWTException e) {
            System.out.println("Failure on reading a screenshot, adding a errorish event."+e.getMessage());
            process.addErrorEvent(screenshot);
        }
    }

    @Override
    public void close() {
        process.close();
    }
}
