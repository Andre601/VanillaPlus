package ch.andre601.vanillaplus.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public record Claim(String world, int chunkX, int chunkZ, int startX, int startY, int startZ, int endX, int endY, int endZ){
    public static Claim create(Chunk chunk){
        int startX = (chunk.getX() - 1) << 4;
        int startZ = (chunk.getZ() - 1) << 4;
        
        return new Claim(
            chunk.getWorld().getName(),
            chunk.getX(), chunk.getZ(),
            startX, chunk.getWorld().getMinHeight(), startZ,
            startX + 47, chunk.getWorld().getMaxHeight(), startZ + 47
        );
    }
    
    public ProtectedCuboidRegion asCuboidRegion(World world){
        Location loc1 = new Location(world, startX(), world.getMinHeight(), startZ());
        Location loc2 = new Location(world, endX(), world.getMaxHeight(), endZ());
        
        BlockVector3 vec1 = BukkitAdapter.asBlockVector(loc1);
        BlockVector3 vec2 = BukkitAdapter.asBlockVector(loc2);
        
        return new ProtectedCuboidRegion(regionId(), vec1, vec2);
    }
    
    public String toMMString(){
        return String.format(
            "<grey>ID: <aqua>%s</aqua>\n" +
            "Region: [<aqua>%s</aqua>, <aqua>%d</aqua>, <aqua>%d</aqua>, <aqua>%d</aqua>] -> " +
            "[<aqua>%s</aqua>, <aqua>%d</aqua>, <aqua>%d</aqua>, <aqua>%d</aqua>]",
            regionId(),
            world(), startX(), startY(), startZ(),
            world(), endX(), endY(), endZ()
        );
    }
    
    public String regionId(){
        return String.format("%d_%d", chunkX(), chunkZ());
    }
}
