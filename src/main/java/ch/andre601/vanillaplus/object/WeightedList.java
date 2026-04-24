package ch.andre601.vanillaplus.object;

import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class WeightedList<E>{
    private final NavigableMap<Integer, E> map = new TreeMap<>();
    private final Random random;
    private int total = 0;
    
    public static <E> WeightedList<E> create(List<Weighted<E>> entries){
        return new WeightedList<>(entries);
    }
    
    private WeightedList(List<Weighted<E>> entries){
        this.random = new Random();
        process(entries);
    }
    
    public E getRandom(){
        int select = random.nextInt(total) + 1;
        return map.ceilingEntry(select).getValue();
    }
    
    private void process(List<Weighted<E>> entries){
        for(Weighted<E> entry : entries){
            if(entry.weight() <= 0) continue;
            
            this.total += entry.weight();
            
            map.put(this.total, entry.entry());
        }
    }
    
    public record Weighted<E>(E entry, int weight){}
}
