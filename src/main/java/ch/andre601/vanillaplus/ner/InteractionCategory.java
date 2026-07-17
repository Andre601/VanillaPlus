package ch.andre601.vanillaplus.ner;

import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class InteractionCategory extends RecipeCategory<InteractionRecipe>{
    
    private final int[] slots = {30, 31, 32, 34};
    
    @Override
    public Class<InteractionRecipe> getRecipeClass(){
        return InteractionRecipe.class;
    }
    
    @Override
    public ParsedRecipeView parseRecipe(InteractionRecipe interactionRecipe, ItemStack itemStack){
        return ParsedRecipeView.builder(Textures.INTERACTION, -8, itemStack)
            .set(slots[0], interactionRecipe.tool().item())
            .set(slots[1], interactionRecipe.interactionTypeItem())
            .set(slots[2], interactionRecipe.input().item())
            .set(slots[3], interactionRecipe.result().item())
            .build();
    }
    
    @Override
    public Set<Integer> getResultSlots(){
        return Set.of(34);
    }
    
    @Override
    public Set<Integer> getIgnoredSlots(){
        return Set.of();
    }
}
