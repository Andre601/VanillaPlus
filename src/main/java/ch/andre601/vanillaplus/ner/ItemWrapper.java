package ch.andre601.vanillaplus.ner;

import ch.andre601.vanillaplus.ner.interaction.InteractionRecipe;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
