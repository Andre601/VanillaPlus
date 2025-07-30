package ch.andre601.vanillaplus.listener;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CauldronLevelChangeEvent;

public class CauldronListener implements Listener{
    
    @EventHandler
    public void onCauldronLevelChange(CauldronLevelChangeEvent event){
        if(event.getReason() != CauldronLevelChangeEvent.ChangeReason.ARMOR_WASH)
            return;
        
        if(!(event.getEntity() instanceof Player player))
            return;
        
        CustomStack mainHand = CustomStack.byItemStack(player.getInventory().getItemInMainHand());
        CustomStack offHand = CustomStack.byItemStack(player.getInventory().getItemInOffHand());
        if(mainHand == null && offHand == null)
            return;
        
        if(mainHand != null && mainHand.getNamespacedID().equals("vanillaplus:trashcan")){
            event.setCancelled(true);
        }else
        if(offHand != null && offHand.getNamespacedID().equals("vanillaplus:trashcan")){
            event.setCancelled(true);
        }
    }
}
