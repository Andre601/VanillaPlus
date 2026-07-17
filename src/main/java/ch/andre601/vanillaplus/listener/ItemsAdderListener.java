package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.ner.NERIntegration;
import com.github.darksoulq.ner.NeverEnoughRecipes;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderListener implements Listener{
    
    @EventHandler
    public void onIAReady(ItemsAdderLoadDataEvent event){
        NeverEnoughRecipes.registerPlugin(new NERIntegration());
        NeverEnoughRecipes.reloadRegistries();
    }
}
