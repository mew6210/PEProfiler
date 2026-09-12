package org.example.userConfig;

import org.example.PE.PEManagerType;
import org.example.presentation.InsightPresenterType;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Locale;

public class Config {
    private int collectionIndex = 1;
    private Path pathToPE = null;
    private PEManagerType processManager = PEManagerType.Cropped;
    private InsightPresenterType InsightPresenter = InsightPresenterType.MarkDown;

    public Config(String... args) {
        if(args.length %2 != 0){
            IO.println("Incorrect amount of command line parameters, defaulting");
            return;
        }
        parseCmdLineArgs(args);
        printParameters();
    }
    private void printParameters(){
        IO.println("Collection index: "+collectionIndex);
        String ptoPe = pathToPE == null ? "not set (will be looked for automatically later)" : pathToPE.toString();
        IO.println("Path to PE: "+ptoPe);
        IO.println("Process Manager: "+processManager.toString());
        IO.println("Report type: "+ InsightPresenter.toString());
    }

    private void parseCmdLineArgs(String... args){
        for(int i =0;i<args.length;i+=2){
            if(!args[i].startsWith("--")){
                IO.println("First argument should have -- at the beginning");
                return;
            }
            String parameterName = args[i].substring(2);
            String parameterValue = args[i+1];
            try{
                setParameter(parameterName,parameterValue);
            } catch (IllegalArgumentException e) {
                IO.println("Error: "+e.getMessage());
            }
        }
    }

    private void setParameter(String name,String value) throws IllegalArgumentException {
        name = name.toLowerCase(Locale.ROOT);
        value = value.toLowerCase(Locale.ROOT);
        switch(name){
            case "collection" -> setCollection(value);
            case "pathtope" -> setPathToPE(value);
            case "pemanager" -> setPEManager(value);
            case "reporttype" -> setInsightPresenterType(value);

            default -> throw new IllegalArgumentException("Unknown parameter name: "+name);
        }
    }

    private void setInsightPresenterType(String value) throws IllegalArgumentException {
        switch(value){
            case "markdown" -> this.InsightPresenter = InsightPresenterType.MarkDown;
            default -> throw new IllegalArgumentException("'"+value+"' is not a valid report type");
        }
    }

    private void setPEManager(String value) throws IllegalArgumentException {
        switch(value){
            case "cropped" -> this.processManager = PEManagerType.Cropped;
            default -> throw new IllegalArgumentException("'"+value+"' is not a valid PEManager type");
        }
    }

    private void setPathToPE(String value) throws IllegalArgumentException {
        try{
            Path p = Path.of(value);
            if(!Files.isRegularFile(p)) throw new IllegalArgumentException("PathToPE does not point to a PE executable");
            this.pathToPE = p;
        }catch (InvalidPathException e){
            throw new IllegalArgumentException("PathToPE does not point to a PE executable");
        }
    }

    private void setCollection(String value) throws IllegalArgumentException {
        try{
            this.collectionIndex = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Collection index should be an integer, '"+value+"' is not an integer");
        }
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
