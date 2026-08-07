package ch.andre601.vanillaplus.object;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class PlayerSettingsDataType implements PersistentDataType<PersistentDataContainer, PlayerSettings>{
    public static final NamespacedKey SORT_TYPE = NamespacedKey.fromString("vanillaplus:sort_type", null);
    public static final NamespacedKey SORT_ORDER = NamespacedKey.fromString("vanillaplus:sort_order", null);
    
    public static final PlayerSettingsDataType INSTANCE = new PlayerSettingsDataType();
    
    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType(){
        return PersistentDataContainer.class;
    }
    
    @Override
    public @NotNull Class<PlayerSettings> getComplexType(){
        return PlayerSettings.class;
    }
    
    @Override
    public @NonNull PersistentDataContainer toPrimitive(@NonNull PlayerSettings complex, @NotNull PersistentDataAdapterContext context){
        PersistentDataContainer pdc = context.newPersistentDataContainer();
        
        pdc.set(SORT_TYPE, PersistentDataType.STRING, complex.sortType().name());
        pdc.set(SORT_ORDER, PersistentDataType.STRING, complex.sortOrder().name());
        return pdc;
    }
    
    @Override
    public @NonNull PlayerSettings fromPrimitive(@NonNull PersistentDataContainer primitive, @NotNull PersistentDataAdapterContext context){
        PlayerSettings.SortType sortType = PlayerSettings.SortType.fromString(primitive.get(SORT_TYPE, PersistentDataType.STRING));
        PlayerSettings.SortOrder sortOrder = PlayerSettings.SortOrder.fromString(primitive.get(SORT_ORDER, PersistentDataType.STRING));
        
        return new PlayerSettings(sortType, sortOrder);
    }
}
