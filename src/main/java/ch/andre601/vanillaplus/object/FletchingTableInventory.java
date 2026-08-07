package ch.andre601.vanillaplus.object;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class FletchingTableInventory implements InventoryHolder{
    
    public static final int TIP_SLOT_INDEX = 3;
    public static final int ARROW_SLOT_INDEX = 11;
    public static final int FEATHER_SLOT_INDEX = 19;
    public static final int RESULT_SLOT_INDEX = 15;
    
    public static final NamespacedKey FEATHER_KEY = NamespacedKey.fromString("vanillaplus:feather", null);
    public static final NamespacedKey TIP_KEY = NamespacedKey.fromString("vanillaplus:tip", null);
    
    private final Inventory inventory;
    
    public FletchingTableInventory(){
        this.inventory = Bukkit.createInventory(this, 36, Component.text(new FontImageWrapper("vanillamcuis:fletching_table").setOffset(-8).getString()));
    }
    
    @Override
    public @NotNull Inventory getInventory(){
        return this.inventory;
    }
    
    public @Nullable ItemStack getTipItem(){
        return this.getInventory().getItem(TIP_SLOT_INDEX);
    }
    
    public @Nullable ItemStack getArrowItem(){
        return this.getInventory().getItem(ARROW_SLOT_INDEX);
    }
    
    public @Nullable ItemStack getFeatherItem(){
        return this.getInventory().getItem(FEATHER_SLOT_INDEX);
    }
    
    public @Nullable ItemStack getResultItem(){
        return this.getInventory().getItem(RESULT_SLOT_INDEX);
    }
    
    public void setTipItem(@Nullable ItemStack item){
        this.getInventory().setItem(TIP_SLOT_INDEX, item);
    }
    
    public void setArrowItem(@Nullable ItemStack item){
        this.getInventory().setItem(ARROW_SLOT_INDEX, item);
    }
    
    public void setFeatherItem(@Nullable ItemStack item){
        this.getInventory().setItem(FEATHER_SLOT_INDEX, item);
    }
    
    public void setResultSlotItem(@Nullable ItemStack item){
        this.getInventory().setItem(RESULT_SLOT_INDEX, item);
    }
    
    public void update(){
        ItemStack arrow;
        
        ItemStack tip = getTipItem();
        if(tip != null){
            switch(tip.getType()){
                case COPPER_NUGGET -> {
                    arrow = CustomStack.getInstance("vanillaplus:copper_tipped_arrow").getItemStack();
                    
                    arrow.editPersistentDataContainer(container -> container.set(TIP_KEY, PersistentDataType.STRING, "copper"));
                }
                case IRON_NUGGET -> {
                    arrow = CustomStack.getInstance("vanillaplus:copper_tipped_arrow").getItemStack();
                    
                    arrow.editPersistentDataContainer(container -> container.set(TIP_KEY, PersistentDataType.STRING, "iron"));
                }
                case GOLD_NUGGET -> {
                    arrow = CustomStack.getInstance("vanillaplus:gold_tipped_arrow").getItemStack();
                    
                    arrow.editPersistentDataContainer(container -> container.set(TIP_KEY, PersistentDataType.STRING, "gold"));
                }
                default -> arrow = ItemStack.of(Material.ARROW);
            }
        }
    }
}
