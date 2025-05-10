package ch.andre601.vanillaplus.object;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CraftingRecipe{
    
    private final boolean shapeless;
    private final List<List<String>> patterns;
    private final Map<String, String> ingredients;
    private final Result result;
    private final String returnedItem;
    
    public CraftingRecipe(boolean shapeless, List<List<String>> patterns, Map<String, String> ingredients, Result result, String returnedItem){
        this.shapeless = shapeless;
        this.patterns = patterns;
        this.ingredients = ingredients;
        this.result = result;
        this.returnedItem = returnedItem;
    }
    
    public static class Shaped extends CraftingRecipe{
        public Shaped(List<List<String>> patterns, Map<String, String> ingredients, Result result, String returnedItem){
            super(false, patterns, ingredients, result, returnedItem);
        }
    }
    
    public static class Shapeless extends CraftingRecipe{
        public Shapeless(Map<String, String> ingredients, Result result, String returnedItem){
            super(true, Collections.emptyList(), ingredients, result, returnedItem);
        }
    }
    
    public record Result(String namespacedId, int amount){}
}
