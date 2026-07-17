package ch.andre601.vanillaplus.object.interactables;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public interface InteractableItem{
    
    List<String> itemNamespacedIDs();
    
    boolean validate(PlayerInteractEvent event, CustomStack stack);
    
    void onRightClick(PlayerInteractEvent event);
    
    void onLeftClick(PlayerInteractEvent event);
}
