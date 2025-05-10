package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.util.TrashcanInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryListener implements Listener{
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){
        if(event.isCancelled())
            return;
        
        if(!(event.getInventory().getHolder() instanceof TrashcanInventory inventory))
            return;
        
        if(!(event.getPlayer() instanceof Player player))
            return;
        
        Location loc = new Location(Bukkit.getWorld(inventory.getWorld()), inventory.getX(), inventory.getY(), inventory.getZ());
        player.playSound(loc, Sound.BLOCK_BARREL_OPEN, 0.5f, 0.75f);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(!(event.getInventory().getHolder() instanceof TrashcanInventory inventory))
            return;
        
        if(!(event.getPlayer() instanceof Player player))
            return;
        
        Location loc = new Location(Bukkit.getWorld(inventory.getWorld()), inventory.getX(), inventory.getY(), inventory.getZ());
        player.playSound(loc, Sound.BLOCK_BARREL_CLOSE, 0.5f, 0.75f);
    }
}
