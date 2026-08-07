package ch.andre601.vanillaplus.ner.fishing;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.ner.ItemWrapper;
import ch.andre601.vanillaplus.object.WeightedList;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record FishingRecipe(ItemWrapper fishingRod, List<WeightedList.Weighted<ItemStack>> loot){
    public static FishingRecipe create(ItemWrapper fishingRod, VanillaPlus plugin, Object... configPath){
        List<WeightedList.Weighted<ItemStack>> items = plugin.getConfigUtil().getWeightedItems(configPath);
        return new FishingRecipe(fishingRod, Objects.requireNonNullElse(items, Collections.emptyList()));
        
    }
}
