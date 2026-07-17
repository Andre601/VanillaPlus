package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.VanillaPlus;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityListener implements Listener{
    
    private final VanillaPlus plugin;
    
    public EntityListener(VanillaPlus plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityAdd(EntityAddToWorldEvent event){
        if(!(event.getEntity() instanceof Item item))
            return;
        
        CustomStack stack = CustomStack.byItemStack(item.getItemStack());
        if(stack == null || !stack.getNamespacedID().equalsIgnoreCase("vanillaplus:totem_of_experience_active"))
            return;
        
        item.getScheduler().runAtFixedRate(plugin,
            task -> item.getWorld().spawnParticle(Particle.ENCHANT, item.getLocation().add(0, 2, 0), 5, 0.05, 0.05, 0.05),
            () -> {}, 1L, 10L);
    }
}
