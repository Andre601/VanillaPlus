package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.object.FletchingTableInventory;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockListener implements Listener{
    
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.hasBlock())
            return;
        
        Block block = event.getClickedBlock();
        if(block == null || block.getType() != Material.FLETCHING_TABLE)
            return;
        
        event.getPlayer().openInventory(new FletchingTableInventory().getInventory());
    }
}
