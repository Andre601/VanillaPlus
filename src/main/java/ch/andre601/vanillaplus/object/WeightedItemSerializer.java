package ch.andre601.vanillaplus.object;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class WeightedItemSerializer implements TypeSerializer<WeightedList.Weighted<ItemStack>>{
    public static final WeightedItemSerializer INSTANCE = new WeightedItemSerializer();
    
    @Override
    public WeightedList.Weighted<ItemStack> deserialize(Type type, ConfigurationNode node) throws SerializationException{
        String itemId = node.node("id").getString();
        CustomStack item = CustomStack.getInstance(itemId);
        
        ItemStack stack;
        if(item != null){
            stack = item.getItemStack();
        }else{
            try{
                stack = Bukkit.getItemFactory().createItemStack(itemId);
            }catch(IllegalArgumentException ex){
                stack = null;
            }
        }
        
        if(stack == null)
            return null;
        
        int weight = node.node("weigth").getInt(-1);
        if(weight <= 0)
            weight = 1;
        
        return new WeightedList.Weighted<>(stack, weight);
    }
    
    @Override
    public void serialize(Type type, WeightedList.Weighted<ItemStack> obj, ConfigurationNode node) throws SerializationException{
        if(obj == null){
            node.set(null);
            return;
        }
        
        CustomStack item = CustomStack.byItemStack(obj.entry());
        if(item != null){
            node.node("id").set(item.getNamespacedID());
        }else{
            node.node("id").set(obj.entry().getType().getKey().asString());
        }
        
        node.node("weight").set(obj.weight());
    }
}
