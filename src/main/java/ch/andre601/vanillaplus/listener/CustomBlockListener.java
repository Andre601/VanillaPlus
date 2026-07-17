package ch.andre601.vanillaplus.listener;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CustomBlockListener implements Listener{
    
    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event){
        Player player = event.getPlayer();
        CustomFurniture furniture = event.getFurniture();
        if(furniture == null) return;
        
        FluidJar jar = FluidJar.fromString(furniture.getId());
        if(jar == null) return;
        
        FluidJar newJar = getNewJar(jar, player.getInventory().getItemInMainHand());
        if(newJar.equals(jar)) return;
        
        ItemStack bucket = player.getInventory().getItemInMainHand();
        if(bucket.getType() == Material.BUCKET){
            Material fluid = jar.type() == FluidType.WATER ? Material.WATER_BUCKET : Material.LAVA_BUCKET;
            if(bucket.getAmount() > 1){
                bucket.setAmount(bucket.getAmount() - 1);
                player.getInventory().addItem(new ItemStack(fluid));
            }else{
                player.getInventory().setItem(EquipmentSlot.HAND, new ItemStack(fluid));
            }
            
            player.playSound(player.getLocation(), jar.type() == FluidType.WATER ? Sound.ITEM_BUCKET_FILL : Sound.ITEM_BUCKET_FILL_LAVA, 0.8f, 1.0f);
        }else if(bucket.getType() == Material.LAVA_BUCKET || bucket.getType() == Material.WATER_BUCKET){
            player.getInventory().setItem(EquipmentSlot.HAND, new ItemStack(Material.BUCKET));
        }else{
            return;
        }
        
        furniture.replaceFurniture(newJar.toString());
    }
    
    private FluidJar getNewJar(FluidJar current, ItemStack item){
        return switch(item.getType()){
            case BUCKET -> {
                if(current.type() == FluidType.NONE){
                    yield current;
                }
                
                int level = current.level() - 1;
                if(level <= 0) yield FluidJar.EMPTY;
                
                yield new FluidJar(current.type(), level);
            }
            case LAVA_BUCKET -> {
                if(current.type() == FluidType.NONE) yield new FluidJar(FluidType.LAVA, 1);
                
                if(current.type() != FluidType.LAVA || current.level() == 6){
                    yield current;
                }
                
                yield new FluidJar(FluidType.LAVA, current.level() + 1);
            }
            case WATER_BUCKET -> {
                if(current.type() == FluidType.NONE) yield new FluidJar(FluidType.WATER, 1);
                
                if(current.type() != FluidType.WATER || current.level() == 6){
                    yield current;
                }
                
                yield new FluidJar(FluidType.WATER, current.level() + 1);
            }
            default -> current;
        };
    }
    
    private enum FluidType{
        NONE,
        WATER,
        LAVA;
        
        public static FluidType fromString(String v){
            for(FluidType value : values()){
                if(value.name().equalsIgnoreCase(v))
                    return value;
            }
            
            return null;
        }
    }
    
    private record FluidJar(FluidType type, int level){
        public static final FluidJar EMPTY = new FluidJar(FluidType.NONE, 0);
        
        public static FluidJar fromString(String value){
            if(value.equalsIgnoreCase("jar")){
                return EMPTY;
            }
            
            if(!value.toLowerCase(Locale.ROOT).startsWith("water_jar") && !value.toLowerCase(Locale.ROOT).startsWith("lava_jar")) return null;
            
            String[] parts = value.split("_");
            FluidType fluidType = FluidType.fromString(parts[0]);
            if(parts.length < 3 || fluidType == null) return null;
            
            int level;
            try{
                level = Integer.parseInt(parts[2]);
            }catch(NumberFormatException ex){
                return null;
            }
            
            if(level < 1 || level > 6) return null;
            
            return new FluidJar(fluidType, level);
        }
        
        @Override
        @NotNull
        public String toString(){
            if(type == FluidType.NONE || level == 0) return "jar";
            
            return type.name().toLowerCase(Locale.ROOT) + "_jar_" + level;
        }
        
        @Override
        public boolean equals(Object o){
            if(!(o instanceof FluidJar jar)) return false;
            
            return jar.toString().equals(this.toString());
        }
    }
}
