package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.util.StringUtil;
import ch.andre601.vanillaplus.util.TrashcanInventory;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerListener implements Listener{
    
    private final NamespacedKey tpLocation;
    
    public PlayerListener(VanillaPlus plugin){
        this.tpLocation = NamespacedKey.fromString("teleport_location", plugin);
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR){
            handleRightClick(event);
        }else{
            handleLeftClick(event);
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
    
    private void handleLeftClick(PlayerInteractEvent event){
        if(!event.hasItem() || event.getHand() != EquipmentSlot.HAND)
            return;
        
        ItemStack stack = event.getItem();
        if(!stack.hasData(DataComponentTypes.MAX_DAMAGE))
            return;
        
        CustomStack item = CustomStack.byItemStack(stack);
        if(item == null)
            return;
        
        if(!item.getNamespacedID().equals("vanillaplus:aluminum_hammer"))
            return;
        
        Block block = event.getClickedBlock();
        if(block == null || block.getType().isAir())
            return;
        
        switch(block.getType()){
            case AMETHYST_BLOCK -> block.setType(Material.BUDDING_AMETHYST);
            case DEEPSLATE_BRICKS -> block.setType(Material.CRACKED_DEEPSLATE_BRICKS);
            case DEEPSLATE_TILES -> block.setType(Material.CRACKED_DEEPSLATE_TILES);
            case NETHER_BRICKS -> block.setType(Material.CRACKED_NETHER_BRICKS);
            case POLISHED_BLACKSTONE_BRICKS -> block.setType(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS);
            case STONE -> block.setType(Material.COBBLESTONE);
            case STONE_BRICKS -> block.setType(Material.CRACKED_STONE_BRICKS);
            default -> {
                return;
            }
        }
        
        event.getPlayer().playSound(block.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 0.5f, 1.0f);
        event.getPlayer().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());
        
        stack.damage(1, event.getPlayer());
    }
    
    private void handleRightClick(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if(block != null && !block.getType().isAir()){
            CustomFurniture furniture = CustomFurniture.byAlreadySpawned(block);
            if(furniture != null && furniture.getNamespacedID().equals("vanillaplus:trashcan")){
                event.setCancelled(true);
                event.getPlayer().openInventory(new TrashcanInventory(block.getLocation()).getInventory());
                return;
            }
        }
        
        if(!event.hasItem() || event.getHand() != EquipmentSlot.HAND)
            return;
        
        CustomStack item = CustomStack.byItemStack(event.getItem());
        if(item == null)
            return;
        
        if(item.getId().endsWith("_scythe")){
            event.setCancelled(true);
            return;
        }
        
        if(!item.getNamespacedID().equals("vanillaplus:teleport_crystal"))
            return;
        
        ItemStack stack = event.getItem();
        if(!stack.hasData(DataComponentTypes.MAX_DAMAGE))
            return;
        
        PersistentDataContainer pdc = stack.getItemMeta().getPersistentDataContainer();
        
        Player player = event.getPlayer();
        
        if(pdc.has(tpLocation, PersistentDataType.STRING)){
            Location location = StringUtil.toLocation(pdc.get(tpLocation, PersistentDataType.STRING));
            if(location == null){
                player.sendMessage(VanillaPlus.MM.deserialize("<red>Unable to teleport you. If this issue persists, report it!"));
                return;
            }
            
            player.teleport(location);
            player.playSound(player, Sound.ENTITY_PLAYER_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
            
            stack.editMeta(meta -> meta.getPersistentDataContainer().remove(tpLocation));
            stack.setData(DataComponentTypes.LORE, ItemLore.lore().lines(getTPLore(null)).build());
            stack.resetData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE);
            stack.damage(1, player);
        }else{
            if(player.getLevel() < 1){
                player.sendMessage(VanillaPlus.MM.deserialize("<red>You need at least <white>1 XP Level</white> to store a Location!"));
                return;
            }
            
            player.setLevel(player.getLevel() - 1);
            player.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 2.0f);
            
            String loc = StringUtil.toString(player.getLocation());
            if(loc == null){
                player.sendMessage(VanillaPlus.MM.deserialize("<red>Unable to set Location. If this issue persists, report it!"));
                return;
            }
            
            stack.editMeta(meta -> meta.getPersistentDataContainer().set(tpLocation, PersistentDataType.STRING, loc));
            stack.setData(DataComponentTypes.LORE, ItemLore.lore().lines(getTPLore(player.getLocation())).build());
            stack.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
    }
    
    private List<Component> getTPLore(Location location){
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
