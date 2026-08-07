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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

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
            InteractionRecipe recipe = interaction.recipe();
            if(recipe == null)
                continue;
            
            registry.addRecipe(recipe);
        }
        
        registry.addCategory(new FishingCategory());
        registry.addCatalyst(FishingRecipe.class, ItemStack.of(Material.FISHING_ROD));
        
        ItemWrapper fishing_rod = ItemWrapper.fromItemsAdder("vanillaplus:iron_fishing_rod");
        if(fishing_rod != null)
            registry.addRecipe(FishingRecipe.create(fishing_rod, plugin, "loot", "fishing"));
        
        Map<String, List<ItemStack>> itemGroups = plugin.getItemGroupProcessor().process();
        
        for(Map.Entry<String, List<ItemStack>> itemGroup : itemGroups.entrySet()){
            String title = titleCase(itemGroup.getKey().replace("_", " "));
            registry.addItemGroup(itemGroup.getKey(), Component.text(title), itemGroup.getValue(), true);
            plugin.getSLF4JLogger().info("Added Item Group {} ({}) with {} entries.", title, itemGroup.getKey(), itemGroup.getValue().size());
        }
    }
    
    private String titleCase(String text){
        StringBuilder builder = new StringBuilder(text.length());
        boolean nextCharUppercase = true;
        
        for(char c : text.toCharArray()){
            if(Character.isSpaceChar(c)){
                nextCharUppercase = true;
            }else
            if(nextCharUppercase){
                builder.append(Character.toTitleCase(c));
                nextCharUppercase = false;
                continue;
            }
            
            builder.append(c);
        }
        
        return builder.toString();
    }
}
