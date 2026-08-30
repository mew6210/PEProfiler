package org.example.PE;


import java.io.IOException;
import java.nio.file.Path;

public class PEProcess implements AutoCloseable {
    private final Path pathToPE;
    private Process process;

    public PEProcess(String PEPath){
        //TODO: CHECK PATH
        this.pathToPE = Path.of(PEPath);
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

}
