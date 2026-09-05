package org.example.PE;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public record ReadEvent(
        Integer id,
        ArrayList<EventTimestamp> timestamps,
        ArrayList<String> items,
        Path pathToFileRead
        )
{
        public int getItemCount(){
                if(items.isEmpty()){
                        return -1;
                }
                int smallestNumber = Integer.MAX_VALUE;
                for(String item : items){
                        int val = Character.getNumericValue(item.charAt(0));
                        if(val < smallestNumber) smallestNumber = val;
                }

                return smallestNumber;
        }

        public List<String> getParsedItemsSatisfyingItemCount(){
                int itemCount = getItemCount();
                return items.stream().
                        filter(item -> item.charAt(0) - '0' == itemCount).
                        map(item -> item.substring(item.indexOf(":") + 1)).
                        toList();
        }

}
