package ch.andre601.vanillaplus.ner.interaction;

import ch.andre601.vanillaplus.ner.ItemWrapper;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

public record InteractionRecipe(ItemWrapper input, ItemWrapper tool, InteractionType type, ItemWrapper result){
    
    public ItemStack interactionTypeItem(){
        if(type() == InteractionType.LEFT){
            return CustomStack.getInstance("vanillaplus:interaction_left").getItemStack();
        }else{
            return CustomStack.getInstance("vanillaplus:interaction_right").getItemStack();
        }
    }
    
    public enum InteractionType{
        LEFT,
        RIGHT
    }
    
}
