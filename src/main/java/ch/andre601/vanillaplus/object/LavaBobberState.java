package ch.andre601.vanillaplus.object;

import org.bukkit.entity.FishHook;

/*
 * Original Code and (c) copyright Fish-Rework by EightL:
 * Plugin: https://hangar.papermc.io/EightL/Fish-Rework
 * Source (this class): https://github.com/EightL/fish-rework-plugin/blob/main/src/main/java/com/fishrework/model/LavaBobberState.java
 */
public class LavaBobberState{
    
    public static int REEL_WINDOW_TICKS = 70;
    
    private final FishHook hook;
    private State state = State.FLYING;
    
    private int biteTimer = -1;
    private int reelWindowTimer = -1;
    private boolean hasCatch = false;
    
    public LavaBobberState(FishHook hook){
        this.hook = hook;
    }
    
    public FishHook getHook(){
        return hook;
    }
    
    public State getState(){
        return state;
    }
    
    public int getBiteTimer(){
        return biteTimer;
    }
    
    public int getReelWindowTimer(){
        return reelWindowTimer;
    }
    
    public int getMinBiteTicks(){
        return hook.getMinWaitTime();
    }
    
    public int getMaxBiteTicks(){
        return hook.getMaxWaitTime();
    }
    
    public boolean hasCatch(){
        return hasCatch;
    }
    
    public void setState(State state){
        this.state = state;
    }
    
    public void setBiteTimer(int biteTimer){
        this.biteTimer = biteTimer;
    }
    
    public void setReelWindowTimer(int reelWindowTimer){
        this.reelWindowTimer = reelWindowTimer;
    }
    
    public void setHasCatch(boolean hasCatch){
        this.hasCatch = hasCatch;
    }
    
    public void decreaseBiteTimer(){
        if(biteTimer > 0)
            biteTimer--;
    }
    
    public void decreaseReelWindowTimer(){
        if(reelWindowTimer > 0)
            reelWindowTimer--;
    }
    
    public enum State {
        FLYING,
        IN_LAVA,
        NIBBLE,
        BITE,
        EXPIRED
    }
}
