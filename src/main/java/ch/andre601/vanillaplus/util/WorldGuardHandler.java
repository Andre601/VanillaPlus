package ch.andre601.vanillaplus.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WorldGuardHandler{
    
    public static boolean canClaim(RegionManager manager, ProtectedCuboidRegion region){
        List<ProtectedRegion> regions = new ArrayList<>(manager.getRegions().values());
        
        return region.getIntersectingRegions(regions).isEmpty();
    }
    
    public static RegionManager getRegionManager(World world){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world));
    }
}
