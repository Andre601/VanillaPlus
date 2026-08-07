package ch.andre601.vanillaplus.ner;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public record ItemWrapper(ItemStack item){
    public static ItemWrapper fromVanilla(Material material){
        return new ItemWrapper(ItemStack.of(material));
    }
    
    public static ItemWrapper fromItemsAdder(String namespacedId){
        CustomStack stack = CustomStack.getInstance(namespacedId);
        if(stack == null)
            return null;
        
        return new ItemWrapper(stack.getItemStack());
    }
    
    public static ItemWrapper resolve(String value){
        ItemWrapper wrapper = fromItemsAdder(value);
        if(wrapper != null)
            return wrapper;
        
        Material material = Material.getMaterial(value.toUpperCase(Locale.ROOT));
        
        return material == null ? null : fromVanilla(material);
    }
}
