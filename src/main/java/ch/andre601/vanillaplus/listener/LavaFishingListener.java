package ch.andre601.vanillaplus.listener;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.object.LavaBobberState;
import ch.andre601.vanillaplus.object.LavaBobberTask;
import dev.lone.itemsadder.api.CustomStack;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Original Code and (c) copyright Fish-Rework by EightL:
 * Plugin: https://hangar.papermc.io/EightL/Fish-Rework
 * Source (this class): https://github.com/EightL/fish-rework-plugin/blob/main/src/main/java/com/fishrework/listener/LavaFishingListener.java
 */
public class LavaFishingListener implements Listener{
    
    private final NamespacedKey ironFishingHookBobber = NamespacedKey.fromString("vanillaplus:iron_fishing_rod_bobber");
    private final NamespacedKey tempFireResistance = NamespacedKey.fromString("vanillaplus:tem_fire_resistance");
    
    private final Map<UUID, LavaBobberState> activeSessions = new ConcurrentHashMap<>();
    
    private final VanillaPlus plugin;
    
    public LavaFishingListener(VanillaPlus plugin){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event){
        if(!(event.getEntity() instanceof FishHook hook)) return;
        if(!(hook.getShooter() instanceof Player player)) return;
        
        if(player.getWorld().getEnvironment() != World.Environment.NETHER) return;
        
        ItemStack stack = player.getInventory().getItemInMainHand();
        if(stack.getType().isAir())
            return;
        
        CustomStack item = CustomStack.byItemStack(stack);
        if(item == null || !item.getNamespacedID().equalsIgnoreCase("vanillaplus:iron_fishing_rod"))
            return;
        
        if(activeSessions.containsKey(player.getUniqueId()))
            removeSession(player.getUniqueId());
        
        hook.setInvulnerable(true);
        
        PersistentDataContainer pdc = hook.getPersistentDataContainer();
        pdc.set(ironFishingHookBobber, PersistentDataType.BOOLEAN, true);
        
        int lureLevel = stack.getEnchantmentLevel(Enchantment.LURE);
        
        int minTicks = Math.max(100, 400 - lureLevel * 100);
        int maxTicks = Math.max(minTicks, 600 - lureLevel * 100);
        
        hook.setWaitTime(Integer.MAX_VALUE, Integer.MAX_VALUE);
        LavaBobberState state = new LavaBobberState(hook, minTicks, maxTicks);
        activeSessions.put(player.getUniqueId(), state);
        
        LavaBobberTask task = new LavaBobberTask(plugin, player.getUniqueId(), state);
        task.runTaskTimer(plugin, 1L, 1L);
    }
    
    @EventHandler
    public void onFish(PlayerFishEvent event){
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if(!activeSessions.containsKey(uuid)) return;
        
        CustomStack item = CustomStack.byItemStack(player.getInventory().getItemInMainHand());
        if(item == null || !item.getNamespacedID().equalsIgnoreCase("vanillaplus:iron_fishing_rod"))
            return;
        
        LavaBobberState state = activeSessions.get(uuid);
        
        if(event.getState() == PlayerFishEvent.State.REEL_IN || event.getState() == PlayerFishEvent.State.FAILED_ATTEMPT){
            int damage;
            if(state.hasCatch()){
                damage = handleCatch(player, state);
            }else{
                if(state.getHook().isOnGround()){
                    damage = 2;
                }else{
                    damage = 1;
                }
            }
            
            removeSession(uuid);
            
            if(damage > 0)
                player.damageItemStack(item.getItemStack(), damage);
            
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof FishHook hook)) return;
        
        PersistentDataContainer pdc = hook.getPersistentDataContainer();
        if(pdc.has(ironFishingHookBobber)) return;
        
        if(hook.getShooter() instanceof Player player){
            ItemStack item = player.getInventory().getItemInMainHand();
            if(item.getType() == Material.FISHING_ROD){
                item.damage(1, player);
                player.sendMessage(
                    VanillaPlus.MM.deserialize("Your fishing rod cannot handle the heat of the Nether and got damaged!")
                );
            }
        }
        
        hook.remove();
    }
    
    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent event){
        ItemStack item = event.getItem().getItemStack();
        
        PersistentDataContainerView pdcv = item.getPersistentDataContainer();
        if(!pdcv.has(tempFireResistance)) return;
        
        item.resetData(DataComponentTypes.DAMAGE_RESISTANT);
        item.editMeta(meta -> {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            
            pdc.remove(tempFireResistance);
        });
    }
    
    public int handleCatch(Player player, LavaBobberState state){
        if(!state.getHook().isValid() || !player.isOnline()) return -1;
        
        Location hookLocation = state.getHook().getLocation();
        Location playerLocation = player.getLocation();
        
        ItemStack item = plugin.getFishingLoot().getRandom();
        if(item == null)
            return -1;
        
        hookLocation.getWorld().dropItem(hookLocation, item, it -> {
            double dx = playerLocation.getX() - it.getX();
            double dy = playerLocation.getY() - it.getY();
            double dz = playerLocation.getZ() - it.getZ();
            double dv = 0.1;
            
            it.setInvulnerable(true);
            
            Vector velocity = new Vector(dx * dv, dy * dv + Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.08, dz * dv);
            it.setVelocity(velocity);
        });
        
        return 1;
    }
    
    public void removeSession(UUID playerUUID){
        LavaBobberState state = activeSessions.remove(playerUUID);
        if(state != null && state.getHook() != null && state.getHook().isValid())
            state.getHook().remove();
    }
}
