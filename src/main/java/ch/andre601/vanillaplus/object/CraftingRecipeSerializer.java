package ch.andre601.vanillaplus.object;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.*;

public class CraftingRecipeSerializer implements TypeSerializer<CraftingRecipe>{
    
    public static final CraftingRecipeSerializer INSTANCE = new CraftingRecipeSerializer();
    
    @Override
    public CraftingRecipe deserialize(Type type, ConfigurationNode node) throws SerializationException{
        boolean shapeless = node.node("shapeless").getBoolean();
        
        List<List<String>> patterns = new ArrayList<>();
        Map<String, String> ingredients = new HashMap<>();
        for(Map.Entry<Object, ? extends ConfigurationNode> entry : node.childrenMap().entrySet()){
            if(!(entry.getKey() instanceof String str))
                continue;
            
            if(str.toLowerCase(Locale.ROOT).startsWith("pattern")){
                List<String> lines = entry.getValue().getList(String.class);
                if(lines == null || lines.isEmpty())
                    continue;
                
                patterns.add(lines);
                continue;
            }
            
            if(str.equalsIgnoreCase("ingredients")){
                for(Map.Entry<Object, ? extends ConfigurationNode> ingredient : node.node(entry.getKey()).childrenMap().entrySet()){
                    ingredients.put((String)ingredient.getKey(), ingredient.getValue().getString());
                }
            }
        }
        
        CraftingRecipe.Result result = new CraftingRecipe.Result(
            node.node("result", "item").getString(),
            node.node("result", "amount").getInt(1)
        );
        
        if(shapeless){
            return new CraftingRecipe.Shapeless(ingredients, result, null);
        }else{
            return new CraftingRecipe.Shaped(patterns, ingredients, result, null);
        }
    }
    
    @Override
    public void serialize(Type type, @Nullable CraftingRecipe obj, ConfigurationNode node) throws SerializationException{
        
    }
}
