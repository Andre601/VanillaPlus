package ch.andre601.vanillaplus.ner;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
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
    
    public record ItemWrapper(ItemStack item){
        public static ItemWrapper fromVanilla(Material material){
            return new ItemWrapper(ItemStack.of(material));
        }
        
        public static ItemWrapper fromItemsAdder(String namespacedId){
            CustomStack stack = CustomStack.getInstance(namespacedId);
            if(stack == null)
                return ItemWrapper.fromVanilla(Material.STONE);
            
            return new ItemWrapper(stack.getItemStack());
        }
    }
}
