package ch.andre601.vanillaplus.ner.fishing;

import ch.andre601.vanillaplus.ner.Textures;
import ch.andre601.vanillaplus.object.WeightedList;
import com.github.darksoulq.ner.layout.RecipeCategory;
import com.github.darksoulq.ner.model.PagedSection;
import com.github.darksoulq.ner.model.ParsedRecipeView;
import com.github.darksoulq.ner.model.SectionButton;
import com.github.darksoulq.ner.resources.UiItems;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class FishingCategory extends RecipeCategory<FishingRecipe>{
    private final int[] lootSlots = {20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
    
    @Override
    public Class<FishingRecipe> getRecipeClass(){
        return FishingRecipe.class;
    }
    
    @Override
    public ParsedRecipeView parseRecipe(FishingRecipe fishingRecipe, ItemStack itemStack){
        ParsedRecipeView.Builder builder = ParsedRecipeView.builder(Textures.FISHING, -8, itemStack)
            .set(13, fishingRecipe.fishingRod().item());
        
        if(fishingRecipe.loot().isEmpty())
            return builder.build();
        
        for(WeightedList.Weighted<ItemStack> item : fishingRecipe.loot()){
            System.out.println(item.weight());
            builder.probability(item.entry(), item.weight());
        }
        
        builder.addSection(new PagedSection(
            lootSlots,
            fishingRecipe.loot().stream().map(WeightedList.Weighted::entry).toList(),
            new SectionButton(19, UiItems.PREV.getStack().clone()),
            new SectionButton(34, UiItems.NEXT.getStack().clone())
        ));
        
        return builder.build();
    }
    
    @Override
    public Set<Integer> getResultSlots(){
        return Set.of(20, 21, 22, 23, 24, 29, 30, 31, 32, 33);
    }
    
    @Override
    public Set<Integer> getIgnoredSlots(){
        return Set.of();
    }
}
