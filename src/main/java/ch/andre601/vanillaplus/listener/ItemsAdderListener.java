package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.ner.NERIntegration;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener{
    
    private final VanillaPlus plugin;
    
    public ItemsAdderListener(VanillaPlus plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onIAReady(ItemsAdderLoadDataEvent event){
        plugin.getLogger().warning("ItemsAdderLoadDataEvent called!");
        if(!VanillaPlus.NER_RECIPES_REGISTERED){
            NeverEnoughRecipes.registerPlugin(new NERIntegration(plugin));
            VanillaPlus.NER_RECIPES_REGISTERED = true;
        }
        
        NeverEnoughRecipes.reloadRegistries();
    }
}
