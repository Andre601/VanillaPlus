package ch.andre601.vanillaplus.util;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.object.WeightedItemSerializer;
import ch.andre601.vanillaplus.object.WeightedList;
import io.leangen.geantyref.TypeToken;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class ConfigUtil{
    
    private final TypeToken<WeightedList.Weighted<ItemStack>> type = new TypeToken<>(){};
    
    private final VanillaPlus plugin;
    private final Path config;
    
    private ConfigurationNode node = null;
    
    public ConfigUtil(VanillaPlus plugin){
        this.plugin = plugin;
        this.config = plugin.getDataPath().resolve("config.yml");
    }
    
    public boolean loadConfig(){
        File folder = plugin.getDataFolder();
        if(!folder.exists() && !folder.mkdirs()){
            plugin.getLogger().warning("Unable to create folder for plugin. Lacking permission?");
            return false;
        }
        
        if(!config.toFile().exists()){
            try(InputStream stream = plugin.getClass().getResourceAsStream("/config.yml")){
                if(stream == null){
                    plugin.getLogger().warning("Received null InputStream.");
                    return false;
                }
                
                Files.copy(stream, config);
                plugin.getLogger().info("Created config.yml!");
            }catch(IOException ex){
                plugin.getSLF4JLogger().warn("Encountered IOException while creating config.yml!", ex);
                return false;
            }
        }
        
        return reloadConfig();
    }
    
    public boolean reloadConfig(){
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .defaultOptions(opts -> opts.serializers(build -> build.register(type, WeightedItemSerializer.INSTANCE)))
            .path(config)
            .build();
        
        try{
            node = loader.load();
            return true;
        }catch(IOException ex){
            return false;
        }
    }
    
    public List<WeightedList.Weighted<ItemStack>> getWeightedItems(Object... path){
        try{
            return node.node(path).getList(type);
        }catch(SerializationException ex){
            return Collections.emptyList();
        }
    }
}
