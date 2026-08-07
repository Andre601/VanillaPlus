package ch.andre601.vanillaplus.object;

import ch.andre601.vanillaplus.VanillaPlus;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

public record PlayerSettings(SortType sortType, SortOrder sortOrder){
    
    public static PlayerSettings createDefault(){
        return new PlayerSettings(SortType.ALPHABETICALLY, SortOrder.ASCENDING);
    }
    
    public static PlayerSettings fromPlayer(Player player){
        PersistentDataContainer container = player.getPersistentDataContainer();
        
        PlayerSettings settings = container.get(VanillaPlus.SETTINGS_KEY, PlayerSettingsDataType.INSTANCE);
        if(settings == null){
            return PlayerSettings.createDefault();
        }
        
        return settings;
    }
    
    public enum SortType{
        ALPHABETICALLY, // Sort by item name
        AMOUNT,         // Sort by number of items
        DAMAGE,         // Sort by damage value
        
        DISABLED;       // Don't sort at all
        
        public static SortType fromString(String value){
            if(value == null)
                return DISABLED;
            
            for(SortType sortType : values()){
                if(sortType.name().equalsIgnoreCase(value))
                    return sortType;
            }
            
            return DISABLED;
        }
    }
    
    public enum SortOrder{
        ASCENDING,
        DESCENDING;
        
        public static SortOrder fromString(String value){
            if(value == null)
                return ASCENDING;
            
            for(SortOrder sortOrder : values()){
                if(sortOrder.name().equalsIgnoreCase(value))
                    return sortOrder;
            }
            
            return ASCENDING;
        }
    }
}
