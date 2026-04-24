package ch.andre601.vanillaplus.command;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.object.WeightedList;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.List;

public class VanillaPlusCommand{
    
    @Command("vanillaplus reload")
    @Permission("vanillaplus.command.vanillaplus.reload")
    public void reload(CommandSourceStack source, VanillaPlus plugin){
        CommandSender sender = source.getSender();
        
        sender.sendMessage(VanillaPlus.MM.deserialize("<grey>Reloading plugin config..."));
        
        if(plugin.getConfigUtil().reloadConfig()){
            List<WeightedList.Weighted<ItemStack>> weightedList = plugin.getConfigUtil().getWeightedItems("loot", "fishing");
            if(weightedList != null)
                plugin.setFishingLoot(WeightedList.create(weightedList));
            
            sender.sendMessage(VanillaPlus.MM.deserialize("<green>Successfully reloaded config.yml!"));
        }else{
            sender.sendMessage(VanillaPlus.MM.deserialize("<red>Error while reloading config.yml!"));
        }
    }
}
