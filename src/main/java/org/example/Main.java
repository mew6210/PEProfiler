package org.example;

import org.example.PE.PEProcess;
import java.io.IOException;

public class Main {
    static void main() {

        try(PEProcess proc = new PEProcess("path")){
            proc.open();
        }
        catch(IOException IOe){
            IOe.printStackTrace();
        }


    }
}
