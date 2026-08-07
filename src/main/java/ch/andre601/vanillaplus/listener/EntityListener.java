package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.VanillaPlus;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.List;

public class EntityListener implements Listener{
    
    private final VanillaPlus plugin;
    
    public EntityListener(VanillaPlus plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityAdd(EntityAddToWorldEvent event){
        if(!(event.getEntity() instanceof Item item))
            return;
        
        CustomStack stack = CustomStack.byItemStack(item.getItemStack());
        if(stack == null || !stack.getNamespacedID().equalsIgnoreCase("vanillaplus:totem_of_experience_active"))
            return;
        
        item.getScheduler().runAtFixedRate(plugin,
            task -> item.getWorld().spawnParticle(Particle.ENCHANT, item.getLocation().add(0, 2, 0), 5, 0.05, 0.05, 0.05),
            () -> {}, 1L, 10L);
    }
    
    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event){
        MerchantRecipe trade = event.getRecipe();
    
        List<ItemStack> items = trade.getIngredients();
        ItemStack adjusted = trade.getAdjustedIngredient1();
        ItemStack result = trade.getResult();
    
        // Treat Gold Ingot -> Emerald Recipe differently
        if(items.getFirst() != null && items.getFirst().getType() == Material.GOLD_INGOT && result.getType() == Material.EMERALD){
            MerchantRecipe recipe = new MerchantRecipe(
                ItemStack.of(Material.BLAZE_POWDER, 4),
                trade.getUses(),
                trade.getMaxUses(),
                trade.hasExperienceReward(),
                trade.getVillagerExperience(),
                trade.getPriceMultiplier(),
                trade.getDemand(),
                trade.getSpecialPrice(),
                trade.shouldIgnoreDiscounts()
            );
            
            recipe.setIngredients(items);
            
            event.setRecipe(recipe);
            return;
        }
    
        int adjustedAmount = adjusted != null ? adjusted.getAmount() : -1;
    
        for(int i = 0; i < items.size(); i++){
            ItemStack item = items.get(i);
            if(item == null || item.getType() != Material.EMERALD)
                continue;
    
            int amount = (i == 0 && adjustedAmount != -1) ? adjustedAmount : item.getAmount();
            items.set(i, ItemStack.of(Material.GOLD_INGOT, amount));
        }
        
        if(result.getType() == Material.EMERALD){
            result = ItemStack.of(Material.GOLD_INGOT, result.getAmount());
        }
        
        MerchantRecipe recipe = new MerchantRecipe(
            result,
            trade.getUses(),
            trade.getMaxUses(),
            trade.hasExperienceReward(),
            trade.getVillagerExperience(),
            trade.getPriceMultiplier(),
            trade.getDemand(),
            trade.getSpecialPrice(),
            trade.shouldIgnoreDiscounts()
        );
        
        recipe.setIngredients(items);
        
        event.setRecipe(recipe);
    }
}
