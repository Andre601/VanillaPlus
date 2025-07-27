package ch.andre601.vanillaplus.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.World;

public class WorldGuardHandler{
    
    public static boolean canClaim(RegionManager manager, ProtectedCuboidRegion region){
        return region.getIntersectingRegions(manager.getRegions().values()).isEmpty();
    }
    
    public static RegionManager getRegionManager(World world){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        return container.get(BukkitAdapter.adapt(world));
    }
}
