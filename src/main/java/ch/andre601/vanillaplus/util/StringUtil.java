package ch.andre601.vanillaplus.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class StringUtil{
    
    public static Location toLocation(String str){
        if(str == null || str.trim().isEmpty())
            return null;
        
        String[] split = str.split(":");
        if(split.length < 6)
            return null;
        
        World world = world(split[0]);
        if(world == null)
            return null;
        
        try{
            return new Location(
                world,
                Float.parseFloat(split[1]), Float.parseFloat(split[2]), Float.parseFloat(split[3]),
                Float.parseFloat(split[4]), Float.parseFloat(split[5])
            );
        }catch(NumberFormatException ignored){
            return null;
        }
    }
    
    public static String toString(Location loc){
        if(loc == null)
            return null;
        
        return String.format("%s:%.3f:%.3f:%.3f:%.3f:%.3f", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
    }
    
    private static World world(String str){
        World world = Bukkit.getWorld(str);
        if(world != null)
            return world;
        
        try{
            UUID uuid = UUID.fromString(str);
            world = Bukkit.getWorld(uuid);
        }catch(IllegalArgumentException ignored){}
        
        return world;
    }
}
