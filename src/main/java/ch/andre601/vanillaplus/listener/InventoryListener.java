package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.object.FletchingTableInventory;
import ch.andre601.vanillaplus.object.PlayerSettings;
import ch.andre601.vanillaplus.object.PlayerSettingsDataType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InventoryListener implements Listener{
    private final PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
    
    @EventHandler(ignoreCancelled = true)
    public void onInvClick(InventoryClickEvent event){
        //if(event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof FletchingTableInventory fletchingTable){
        //    handleFletchingTable(event, fletchingTable);
        //    return;
        //}
        
        if(event.getClick() != ClickType.MIDDLE)
            return;
        
        HumanEntity entity = event.getWhoClicked();
        PersistentDataContainer container = entity.getPersistentDataContainer();
        
        PlayerSettings settings = container.get(VanillaPlus.SETTINGS_KEY, PlayerSettingsDataType.INSTANCE);
        if(settings == null){
            settings = PlayerSettings.createDefault();
            
            container.set(VanillaPlus.SETTINGS_KEY, PlayerSettingsDataType.INSTANCE, settings);
        }
        
        if(settings.sortType() == PlayerSettings.SortType.DISABLED)
            return;
        
        List<ItemStack> sortableItems = new ArrayList<>();
        
        ItemStack[] items = event.getInventory().getContents();
        
        for(ItemStack item : items){
            if(item != null && !item.getType().isAir())
                sortableItems.add(item);
        }
        
        Comparator<ItemStack> comparator = itemComparator(settings.sortType());
        
        if(settings.sortOrder() == PlayerSettings.SortOrder.DESCENDING)
            comparator = comparator.reversed();
        
        sortableItems.sort(comparator);
        
        event.getInventory().clear();
        for(int i = 0; i < sortableItems.size() && i < event.getInventory().getSize(); i++){
            event.getInventory().setItem(i, sortableItems.get(i));
        }
    }
    
    private Comparator<ItemStack> itemComparator(PlayerSettings.SortType type){
        return switch(type){
            case AMOUNT -> Comparator.comparing(this::name)
                .thenComparingInt(ItemStack::getAmount);
            case DAMAGE -> Comparator.comparing(this::name)
                .thenComparingInt(this::durability);
            default -> Comparator.comparing(this::name);
        };
    }
    
    private String name(ItemStack stack){
        Component component = stack.getData(DataComponentTypes.ITEM_NAME);
        if(component == null)
            return stack.getType().name();
        
        return serializer.serialize(component);
    }
    
    private int durability(ItemStack stack){
        Integer damage = stack.getData(DataComponentTypes.DAMAGE);
        Integer maxDamage = stack.getData(DataComponentTypes.MAX_DAMAGE);
        if(damage == null || damage <= 0 || maxDamage == null || maxDamage < 0)
            return Integer.MAX_VALUE;
        
        return maxDamage - damage;
    }
    
    private void handleFletchingTable(InventoryClickEvent event, FletchingTableInventory fletchingTable){
        int clickedSlot = event.getRawSlot();
        ItemStack slotItem = event.getCurrentItem();
        ItemStack cursorItem = event.getCursor();
        
        switch(clickedSlot){
            case FletchingTableInventory.TIP_SLOT_INDEX -> {
            
            }
        }
    }
}
