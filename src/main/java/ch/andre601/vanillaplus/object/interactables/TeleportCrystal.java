package ch.andre601.vanillaplus.object.interactables;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.util.StringUtil;
import dev.lone.itemsadder.api.CustomStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class TeleportCrystal implements InteractableItem{
    private final NamespacedKey tpLocation = new NamespacedKey("vanillaplus", "teleport_location");
    
    @Override
    public List<String> itemNamespacedIDs(){
        return List.of("vanillaplus:teleport_crystal");
    }
    
    @Override
    public boolean validate(PlayerInteractEvent event, CustomStack stack){
        return stack.getItemStack().hasData(DataComponentTypes.MAX_DAMAGE);
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event){
        ItemStack item = event.getItem();
        if(item == null || item.getType().isAir())
            return;
        
        Player player = event.getPlayer();
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        
        if(container.has(tpLocation, PersistentDataType.STRING)){
            Location location = StringUtil.toLocation(container.get(tpLocation, PersistentDataType.STRING));
            if(location == null){
                player.sendRichMessage("<red>Unable to teleport you. If this issue persists, report it.");
                return;
            }
            
            player.teleport(location);
            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            
            item.editMeta(meta -> meta.getPersistentDataContainer().remove(tpLocation));
            item.setData(DataComponentTypes.LORE, ItemLore.lore().lines(lore(null)).build());
            item.resetData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
            item.damage(1, player);
        }else{
            if(player.getLevel() < 1){
                player.sendRichMessage("<red>You need at least <white>1 XP Leven</white> to store a location.");
                return;
            }
            
            player.setLevel(player.getLevel() - 1);
            player.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 2.0f);
            
            String loc = StringUtil.toString(player.getLocation());
            
            item.editMeta(meta -> meta.getPersistentDataContainer().set(tpLocation, PersistentDataType.STRING, loc));
            item.setData(DataComponentTypes.LORE, ItemLore.lore().lines(lore(player.getLocation())).build());
            item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
    }
    
    @Override
    public void onLeftClick(PlayerInteractEvent event){}
    
    private List<Component> lore(Location location){
        List<Component> lore = new ArrayList<>();
        
        lore.add(VanillaPlus.MM.deserialize("<!i><lang_or:vanillaplus.item.teleport_crystal.lore_1:'Teleports you to a previously'>"));
        lore.add(VanillaPlus.MM.deserialize("<!i><lang_or:vanillaplus.item.teleport_crystal.lore_2:'saved Location.'>"));
        lore.add(Component.empty());
        lore.add(VanillaPlus.MM.deserialize("<!i><lang_or:vanillaplus.item.teleport_crystal.lore_3:'Requires 1 XP Level to'>"));
        lore.add(VanillaPlus.MM.deserialize("<!i><lang_or:vanillaplus.item.teleport_crystal.lore_4:'save your Location.'>"));
        lore.add(Component.empty());
        lore.add(VanillaPlus.MM.deserialize("<!i><lang_or:vanillaplus.item.teleport_crystal.lore_5:'1st Right Click: Save Location.'>"));
        lore.add(VanillaPlus.MM.deserialize("<!i><lang_or:vanillaplus.item.teleport_crystal.lore_6:'2nd Right Click: Teleport.'>"));
        
        if(location != null){
            lore.add(Component.empty());
            
            
            lore.add(VanillaPlus.MM.deserialize(String.format(
                "<!i><lang_or:vanillaplus.item.teleport_crystal.lore_7:" +
                    "'Location: %s, %.3f, %.3f, %.3f':" +
                    "'<aqua>%1$s':'<aqua>%2$.3f':'<aqua>%3$.3f':'<aqua>%4$.3f'>",
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ()
            )));
        }
        
        return lore;
    }
}
