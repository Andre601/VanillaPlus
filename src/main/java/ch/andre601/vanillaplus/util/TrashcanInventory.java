package ch.andre601.vanillaplus.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class TrashcanInventory implements InventoryHolder{
    
    private final Inventory inventory;
    
    private final String world;
    private final int x;
    private final int y;
    private final int z;
    
    public TrashcanInventory(Location loc){
        this.inventory = Bukkit.createInventory(this, 27, Component.translatable("vanillaplus.item.trashcan", "Trashcan"));
        
        this.world = loc.getWorld().getName();
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
    }
    
    @Override
    public @NotNull Inventory getInventory(){
        return this.inventory;
    }
    
    public String getWorld(){
        return world;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public int getZ(){
        return z;
    }
}
