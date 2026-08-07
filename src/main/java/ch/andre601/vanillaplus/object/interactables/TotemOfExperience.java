package ch.andre601.vanillaplus.object.interactables;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.EntityEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class TotemOfExperience implements InteractableItem{
    public static final NamespacedKey XP_STORED = new NamespacedKey("vanillaplus", "stored_experience");
    @Override
    public List<String> itemNamespacedIDs(){
        return List.of("vanillaplus:totem_of_experience_active");
    }
    
    @Override
    public boolean validate(PlayerInteractEvent event, CustomStack stack){
        return stack.getItemStack().getPersistentDataContainer().has(TotemOfExperience.XP_STORED, PersistentDataType.INTEGER);
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType().isAir())
            return;
        
        Integer xp = item.getPersistentDataContainer().get(XP_STORED, PersistentDataType.INTEGER);
        if(xp == null)
            return;
        
        Player player = event.getPlayer();
        player.setExperienceLevelAndProgress(player.calculateTotalExperiencePoints() + xp);
        
        player.playEffect(EntityEffect.PROTECTED_FROM_DEATH);
        player.getInventory().setItemInMainHand(null);
    }
    
    @Override
    public void onLeftClick(PlayerInteractEvent event){}
}
