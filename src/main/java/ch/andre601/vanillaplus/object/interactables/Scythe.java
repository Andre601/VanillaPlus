package ch.andre601.vanillaplus.object.interactables;

import dev.lone.itemsadder.api.CustomStack;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.tag.TagKey;
import net.kyori.adventure.key.Key;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Collection;
import java.util.List;

public class Scythe implements InteractableItem{
    @Override
    public List<String> itemNamespacedIDs(){
        return List.of(
            "vanillaplus:wooden_scythe",
            "vanillaplus:stone_scythe",
            "vanillaplus:golden_scythe",
            "vanillaplus:copper_scythe",
            "vanillaplus:iron_scythe",
            "vanillaplus:diamond_scythe",
            "vanillaplus:netherite_scythe"
        );
    }
    
    @Override
    public boolean validate(PlayerInteractEvent event, CustomStack stack){
        return event.getClickedBlock() != null && !event.getClickedBlock().getType().isAir();
    }
    
    @Override
    public void onRightClick(PlayerInteractEvent event){
        Registry<BlockType> blockTypeRegistry = RegistryAccess.registryAccess()
            .getRegistry(RegistryKey.BLOCK);
        
        Collection<BlockType> convertibleToFarmland = blockTypeRegistry.getTagValues(
            TagKey.create(RegistryKey.BLOCK, Key.key("vanillaplus:convertable_to_farmland"))
        );
        
        if(convertibleToFarmland.contains(event.getClickedBlock().getType().asBlockType()))
            event.setCancelled(true);
    }
    
    @Override
    public void onLeftClick(PlayerInteractEvent event){}
}
