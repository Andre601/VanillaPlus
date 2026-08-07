package ch.andre601.vanillaplus.ner;

import ch.andre601.vanillaplus.VanillaPlus;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class ItemGroupProcessor{
    private final File recipesFolder;
    
    public ItemGroupProcessor(VanillaPlus plugin){
        this.recipesFolder = plugin.getDataPath().getParent()
            .resolve("ItemsAdder")
            .resolve("contents")
            .resolve("vanillaplus")
            .resolve("configs")
            .resolve("recipes")
            .toFile();
    }
    
    public Map<String, List<ItemStack>> process(){
        Map<String, List<ItemStack>> itemGroups = new HashMap<>();
        File[] files = recipesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if(files == null || files.length == 0)
            return Collections.emptyMap();
        
        for(File file : files){
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection recipes = config.getConfigurationSection("recipes");
            
            if(recipes == null)
                continue;
            
            for(String key : recipes.getKeys(false)){
                ConfigurationSection section = recipes.getConfigurationSection(key);
                if(section == null)
                    continue;
                
                for(String sectionKey : section.getKeys(false)){
                    ConfigurationSection recipe = section.getConfigurationSection(sectionKey);
                    if(recipe == null)
                        continue;
                    
                    ItemWrapper item = ItemWrapper.resolve(recipe.getString("result.item"));
                    String group = recipe.getString("recipe_group");
                    
                    if(item == null || group == null || group.isEmpty())
                        continue;
                    
                    List<ItemStack> itemList = itemGroups.get(group.toLowerCase(Locale.ROOT));
                    if(itemList == null)
                        itemList = new ArrayList<>();
                    
                    itemList.add(item.item());
                    
                    itemGroups.put(group.toLowerCase(Locale.ROOT), itemList);
                }
            }
        }
        
        return itemGroups;
    }
}
