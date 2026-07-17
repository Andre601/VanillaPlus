package ch.andre601.vanillaplus.ner;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.ner.fishing.FishingCategory;
import ch.andre601.vanillaplus.ner.fishing.FishingRecipe;
import ch.andre601.vanillaplus.ner.interaction.Interaction;
import ch.andre601.vanillaplus.ner.interaction.InteractionCategory;
import ch.andre601.vanillaplus.ner.interaction.InteractionRecipe;
import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.Registration;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NERIntegration implements NerPlugin{
    
    private final VanillaPlus plugin;
    
    public NERIntegration(VanillaPlus plugin){
        this.plugin = plugin;
    }
    
    @Override
    public void register(Registration registry){
        registry.addCategory(new InteractionCategory());
        registry.addCatalyst(InteractionRecipe.class, CustomStack.getInstance("vanillaplus:interaction").getItemStack());
        
        for(Interaction interaction : Interaction.values()){
            registry.addRecipe(interaction.recipe());
        }
        
        registry.addCategory(new FishingCategory());
        registry.addCatalyst(FishingRecipe.class, ItemStack.of(Material.FISHING_ROD));
        registry.addRecipe(FishingRecipe.create(ItemWrapper.fromItemsAdder("vanillaplus:iron_fishing_rod"), plugin, "loot", "fishing"));
    }
}
