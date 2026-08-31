package org.example.PE;


import java.util.ArrayList;

public record ReadEvent(
        Integer id,
        ArrayList<EventTimestamp> timestamps,
        ArrayList<String> items)
{}
