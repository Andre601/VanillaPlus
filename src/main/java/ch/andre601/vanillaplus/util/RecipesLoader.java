package ch.andre601.vanillaplus.util;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.object.CraftingRecipe;
import ch.andre601.vanillaplus.object.CraftingRecipeSerializer;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RecipesLoader{
    
    private final List<CraftingRecipe> recipes = new ArrayList<>();
    private final VanillaPlus plugin;
    private final Path basePath;
    
    public RecipesLoader(VanillaPlus plugin){
        this.plugin = plugin;
        this.basePath = plugin.getDataPath().getParent().resolve("ItemsAdder").resolve("contents")
            .resolve("vanillaplus").resolve("configs");
    }
    
    public void loadRecipes(){
        Path recipesPath = basePath.resolve("_recipes.yml");
        
    }
    
    private ConfigurationNode loadNode(Path path){
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
            .path(path)
            .defaultOptions(options -> options.serializers(builder -> builder.register(CraftingRecipe.class, CraftingRecipeSerializer.INSTANCE)))
            .build();
        
        try{
            return loader.load();
        }catch(IOException ex){
            return null;
        }
    }
}
