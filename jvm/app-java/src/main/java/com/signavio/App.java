
package com.signavio;

import java.util.List;

public class App {
    
    public static void main(String[] args) throws InterruptedException {
        List<EventlogRow> eventlogRows = CSVReader.readFile("samples/Activity_Log_2004_to_2014.csv");
    
        long begin = System.currentTimeMillis();
        
        // TODO: Add the call to your solution here
        
        long end = System.currentTimeMillis();
    
        System.out.println(String.format("Duration: %s milliseconds", end - begin));
    }
}
