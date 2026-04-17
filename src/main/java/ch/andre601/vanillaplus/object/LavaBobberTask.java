package ch.andre601.vanillaplus.object;

import ch.andre601.vanillaplus.VanillaPlus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/*
 * Original Code and (c) copyright Fish-Rework by EightL:
 * Plugin: https://hangar.papermc.io/EightL/Fish-Rework
 * Source (this class): https://github.com/EightL/fish-rework-plugin/blob/main/src/main/java/com/fishrework/task/LavaBobberTask.java
 */
public class LavaBobberTask extends BukkitRunnable{
    
    private final Vector zeroVelocity = new Vector(0, 0, 0);
    
    private final VanillaPlus plugin;
    private final UUID playerUUID;
    private final LavaBobberState state;
    
    private Location floatAnchor;
    private Location approachStart;
    private double approachProgress;
    private double approachStep;
    private int floatTicks;
    
    private final Random random = new Random();
    
    public LavaBobberTask(VanillaPlus plugin, UUID playerUUID, LavaBobberState state){
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.state = state;
    }
    
    @Override
    public void run(){
        FishHook hook = state.getHook();
        Player player = plugin.getServer().getPlayer(playerUUID);
        
        if(player == null || !player.isOnline() || hook == null || hook.isDead() || !hook.isValid()){
            clean();
            return;
        }
        
        hook.setFireTicks(0);
        
        Location hookLocation = hook.getLocation();
        
        if(state.getState() != LavaBobberState.State.FLYING && isLava(hookLocation)){
            maintainFloat(hook);
            hookLocation = hook.getLocation();
        }
        
        switch(state.getState()){
            case FLYING -> handleFlying(hookLocation);
            case IN_LAVA -> handleInLava(hookLocation, player);
            case NIBBLE -> handleNibble(hookLocation, player);
            case BITE -> handleBite(hookLocation, player);
            case EXPIRED -> clean();
        }
    }
    
    private void maintainFloat(FishHook hook){
        if(floatAnchor == null){
            floatAnchor = hook.getLocation().clone();
        }
        
        floatTicks++;
        double surfaceY = floatAnchor.getBlockY() + 0.92;
        double bobberOffset = Math.sin(floatTicks * 0.28) * 0.03;
        Location target = floatAnchor.clone();
        target.setY(surfaceY + bobberOffset);
        
        hook.setGravity(false);
        hook.setVelocity(zeroVelocity);
        
        if(hook.getLocation().distanceSquared(target) > 0.0004){
            hook.teleport(target);
        }
    }
    
    private void handleFlying(Location location){
        if(isLava(location)){
            state.setState(LavaBobberState.State.IN_LAVA);
            startBiteTimer();
            clearApproach();
            floatAnchor = location.clone();
            floatTicks = 0;
            state.getHook().setGravity(false);
            state.getHook().setVelocity(zeroVelocity);
        }
    }
    
    private void handleInLava(Location hookLocation, Player player){
        if(!isLava(hookLocation)){
            switchToFlying();
            return;
        }
        
        state.decreaseBiteTimer();
        
        if(state.getBiteTimer() <= 0){
            startApproachTrail(hookLocation, player);
        }
    }
    
    private void handleNibble(Location hookLocation, Player player){
        if(!isLava(hookLocation)){
            switchToFlying();
            return;
        }
        
        if(approachStart == null){
            startApproachTrail(hookLocation, player);
            return;
        }
        
        Location bobberLocation = state.getHook().getLocation().clone();
        approachProgress = Math.min(1.0, approachProgress + approachStep);
        Location current = lerp(approachStart, bobberLocation, approachProgress);
        current.getWorld().spawnParticle(Particle.FLAME, current, 2, 0.03, 0.03, 0.03, 0.0);
        
        if(floatTicks % 5 == 0){
            player.playSound(hookLocation, Sound.BLOCK_LAVA_POP, 0.8f, 1.0f);
        }
        
        if(current.distanceSquared(bobberLocation) <= (0.45 * 0.45) || approachProgress >= 1.0)
            triggerBite(hookLocation, player);
    }
    
    private void handleBite(Location hookLocation, Player player){
        state.decreaseReelWindowTimer();
        
        if(state.getReelWindowTimer() % 5 == 0){
            hookLocation.getWorld().spawnParticle(Particle.LAVA, hookLocation, 2, 0.2, 0.1, 0.2);
        }
        
        if(state.getReelWindowTimer() <= 0){
            state.setHasCatch(false);
            player.playSound(hookLocation, Sound.ENTITY_GENERIC_SPLASH, 0.5f, 0.5f);
            state.setState(LavaBobberState.State.IN_LAVA);
            clearApproach();
            startBiteTimer();
        }
    }
    
    private void triggerBite(Location hookLocation, Player player){
        state.setState(LavaBobberState.State.BITE);
        state.setHasCatch(true);
        state.setReelWindowTimer(LavaBobberState.REEL_WINDOW_TICKS);
        
        hookLocation.getWorld().spawnParticle(Particle.LAVA, hookLocation, 15, 0.5, 0.3, 0.5);
        hookLocation.getWorld().spawnParticle(Particle.FLAME, hookLocation, 8, 0.4, 0.2, 0.4);
        player.playSound(hookLocation, Sound.ENTITY_GENERIC_BURN, 1.0f, 0.8f);
    }
    
    private void startBiteTimer(){
        int min = state.getMinBiteTicks();
        int max = state.getMaxBiteTicks();
        
        synchronized(random){
            int ticks = min + random.nextInt(max - min + 1);
            state.setBiteTimer(ticks);
        }
    }
    
    private void startApproachTrail(Location hookLocation, Player player){
        Location center = state.getHook().getLocation().clone();
        approachStart = randomPointInCircle(center);
        approachProgress = 0.0;
        approachStep = 1.0 / 22;
        
        state.setState(LavaBobberState.State.NIBBLE);
        player.playSound(hookLocation, Sound.BLOCK_LAVA_POP, 0.8f, 0.8f);
    }
    
    private boolean isLava(Location location){
        return location.getBlock().getType() == Material.LAVA;
    }
    
    private void switchToFlying(){
        state.setState(LavaBobberState.State.FLYING);
        state.setBiteTimer(-1);
        state.getHook().setGravity(true);
        clearApproach();
        floatAnchor = null;
        floatTicks = 0;
    }
    
    private Location randomPointInCircle(Location center){
        double angle = random.nextDouble() * Math.PI * 2.0;
        double distance = Math.sqrt(random.nextDouble()) * 2.6;
        
        double x = center.getX() + Math.cos(angle) * distance;
        double y = center.getY() + (random.nextDouble() * 0.14) - 0.04;
        double z = center.getY() + Math.sin(angle) * distance;
        
        return new Location(center.getWorld(), x, y, z);
    }
    
    private Location lerp(Location from, Location to, double t){
        double x = from.getX() + (to.getX() - from.getX()) * t;
        double y = from.getY() + (to.getY() - from.getY()) * t;
        double z = from.getZ() + (to.getZ() - from.getZ()) * t;
        
        return new Location(from.getWorld(), x, y, z);
    }
    
    private void clearApproach(){
        approachStart = null;
        approachProgress = 0.0;
        approachStep = 0.0;
    }
    
    private void clean(){
        state.setState(LavaBobberState.State.EXPIRED);
        state.setHasCatch(false);
        clearApproach();
        floatAnchor = null;
        floatTicks = 0;
        plugin.getLavaFishingListener().removeSession(playerUUID);
        cancel();
    }
}
