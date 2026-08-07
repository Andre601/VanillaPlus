package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.object.interactables.*;
import dev.lone.itemsadder.api.CustomStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DeathProtection;
import io.papermc.paper.persistence.PersistentDataContainerView;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PlayerListener implements Listener{
    
    private final List<InteractableItem> interactableItems;
    
    public PlayerListener(){
        this.interactableItems = List.of(
            new AluminumHammer(),
            new Scythe(),
            new TeleportCrystal(),
            new TotemOfExperience()
        );
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(!event.hasItem() || event.getHand() != EquipmentSlot.HAND)
            return;
        
        CustomStack stack = CustomStack.byItemStack(event.getItem());
        if(stack == null)
            return;
        
        for(InteractableItem interactableItem : interactableItems){
            if(interactableItem.itemNamespacedIDs().contains(stack.getNamespacedID()) && interactableItem.validate(event, stack)){
                if(event.getAction().isRightClick()){
                    interactableItem.onRightClick(event);
                }else{
                    interactableItem.onLeftClick(event);
                }
                break;
            }
        }
    }
    
    @EventHandler
    public void onPlayerResourcePackStatus(PlayerJoinEvent event){
        Player player = event.getPlayer();
        
        player.sendMessage(VanillaPlus.MM.deserialize("<!shadow><font:vanillaplus:logo>a</font>"));
        if(player.hasPlayedBefore()){
            player.sendMessage(Component.empty());
            player.sendMessage(VanillaPlus.MM.deserialize(String.format(
                "<font:waila:offset>\uF009\uF00A</font> <grey>Welcome back <aqua>%s</aqua>!",
                player.getName()
            )));
            player.sendMessage(VanillaPlus.MM.deserialize(
                "<font:waila:offset>\uF009\uF00A</font> <grey>Glad to see you again."
            ));
            player.sendMessage(Component.empty());
            player.sendMessage(Component.empty());
        }else{
            player.sendMessage(VanillaPlus.MM.deserialize(String.format(
                "<font:waila:offset>\uF009\uF00A</font> <grey>Welcome to <bold><gold>Vanilla<green>+</green></gold></bold> <aqua>%s</aqua>!",
                player.getName()
            )));
            player.sendMessage(VanillaPlus.MM.deserialize(
                "<font:waila:offset>\uF009\uF00A</font> <grey>Please make yourself familiar with our"
            ));
            player.sendMessage(VanillaPlus.MM.deserialize(
                "<font:waila:offset>\uF009\uF00A</font> <aqua><click:run_command:/rules>" +
                    "<hover:show_text:\"<grey>Click to see them!\">/rules</hover></click></aqua> to avoid any issues."
            ));
            player.sendMessage(VanillaPlus.MM.deserialize(
                "<font:waila:offset>\uF009\uF00A</font> <aqua>Interact with NPCs <grey>for more info and help."
            ));
            player.sendMessage(Component.empty());
            player.sendMessage(VanillaPlus.MM.deserialize("<font:waila:offset>\uF009\uF00A</font> <grey>Enjoy your stay!"));
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if(event.isCancelled())
            return;
        
        ItemStack stack = event.getPlayer().getInventory().getItemInMainHand();
        if(stack.getType().isAir())
            return;
        
        CustomStack item = CustomStack.byItemStack(stack);
        if(item == null || !item.getId().endsWith("_scythe"))
            return;
        
        int x = event.getBlock().getX();
        int y = event.getBlock().getY();
        int z = event.getBlock().getZ();
        World world = event.getBlock().getWorld();
        
        for(int iy = y - 1; iy <= y + 1; iy++){
            for(int ix = x - 1; ix <= x + 1; ix++){
                for(int iz = z - 1; iz <= z + 1; iz++){
                    Block block = world.getBlockAt(ix, iy, iz);
                    if(block.getType().isAir())
                        continue;
                    
                    handleHarvestableBlocks(block, stack);
                }
            }
        }
        
        stack.damage(1, event.getPlayer());
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        if(event.isCancelled())
            return;
        
        Player player = event.getPlayer();
        if(player.calculateTotalExperiencePoints() == 0)
            return;
        
        List<ItemStack> items = event.getDrops();
        for(int i = 0; i < items.size(); i++){
            CustomStack stack = CustomStack.byItemStack(items.get(i));
            if(stack == null || !stack.getNamespacedID().equalsIgnoreCase("vanillaplus:totem_of_experience"))
                continue;
            
            ItemStack item = stack.getItemStack();
            PersistentDataContainerView container = item.getPersistentDataContainer();
            if(!container.has(TotemOfExperience.XP_STORED, PersistentDataType.INTEGER)){
                stack = CustomStack.getInstance("vanillaplus:totem_of_experience_active");
                
                item = stack.getItemStack();
                item.editMeta(meta -> meta.getPersistentDataContainer().set(TotemOfExperience.XP_STORED, PersistentDataType.INTEGER, player.calculateTotalExperiencePoints()));
                item.setData(DataComponentTypes.DEATH_PROTECTION, DeathProtection.deathProtection().build());
                
                items.set(i, item);
                event.setShouldDropExperience(false);
                
                player.playSound(player, Sound.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 0.8f, 0.5f);
            }
        }
    }
    
    @EventHandler
    public void onResurrect(EntityResurrectEvent event){
        if(!(event.getEntity() instanceof Player player))
            return;
        
        EquipmentSlot hand = event.getHand();
        if(hand == null)
            return;
        
        CustomStack item = CustomStack.byItemStack(player.getInventory().getItem(hand));
        if(item != null && item.getNamespacedID().equalsIgnoreCase("vanillaplus:totem_of_experience_active"))
            event.setCancelled(true);
    }
    
    private void handleHarvestableBlocks(Block block, ItemStack item){
        BlockData data = block.getBlockData();
        if(data instanceof Ageable ageable){
            if(ageable.getAge() != ageable.getMaximumAge())
                return;
            
            block.breakNaturally(item);
        }else{
            Registry<BlockType> blockTypeRegistry = RegistryAccess.registryAccess()
                .getRegistry(RegistryKey.BLOCK);
            
            Collection<BlockType> harvestable = blockTypeRegistry.getTagValues(
                TagKey.create(RegistryKey.BLOCK, Key.key("vanillaplus:scythe_harvestable"))
            );
            
            if(!harvestable.contains(block.getType().asBlockType()))
                return;
            
            block.breakNaturally(item);
        }
    }
}
