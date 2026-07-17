package ch.andre601.vanillaplus.object.interactables;

import dev.lone.itemsadder.api.CustomStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class AluminumHammer implements InteractableItem{
    @Override
    public List<String> itemNamespacedIDs(){
        return List.of("vanillaplus:aluminum_hammer");
    }
    
    @Override
    public boolean validate(PlayerInteractEvent event, CustomStack stack){
        if(!stack.getItemStack().hasData(DataComponentTypes.MAX_DAMAGE))
            return false;
        
        Block block = event.getClickedBlock();
        return (block != null) && !block.getType().isAir();
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event){}
    
    @Override
    public void onLeftClick(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if(block == null) // Never through based on validation... But IJ complains
            return;
        
        switch(block.getType()){
            case AMETHYST_BLOCK -> block.setType(Material.BUDDING_AMETHYST);
            case DEEPSLATE_BRICKS -> block.setType(Material.CRACKED_DEEPSLATE_BRICKS);
            case DEEPSLATE_TILES -> block.setType(Material.CRACKED_DEEPSLATE_TILES);
            case NETHER_BRICKS -> block.setType(Material.CRACKED_NETHER_BRICKS);
            case POLISHED_BLACKSTONE_BRICKS -> block.setType(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS);
            case RED_SANDSTONE -> block.setType(Material.RED_SAND, true);
            case SANDSTONE -> block.setType(Material.SAND, true);
            case STONE -> block.setType(Material.COBBLESTONE);
            case STONE_BRICKS -> block.setType(Material.CRACKED_STONE_BRICKS);
            default -> {
                return;
            }
        }
        
        event.getPlayer().playSound(block.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 0.5f, 1.0f);
        event.getPlayer().playEffect(block.getLocation(), Effect.DESTROY_BLOCK, block.getBlockData());
        
        event.getPlayer().getInventory().getItemInMainHand().damage(1, event.getPlayer());
    }
    
    
}
