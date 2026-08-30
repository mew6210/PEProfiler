package org.example.PE;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PEProcess implements AutoCloseable {
    private final Path pathToPE;
    private Process process;

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
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        process = pb.start();
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

}
