package ch.andre601.vanillaplus.papi;

import ch.andre601.vanillaplus.VanillaPlus;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIPlaceholders extends PlaceholderExpansion{
    
    private final VanillaPlus plugin;
    
    public PAPIPlaceholders(VanillaPlus plugin){
        this.plugin = plugin;
    }
    
    @Override
    public @NotNull String getIdentifier(){
        return "vanillaplus";
    }
    
    @Override
    public @NotNull String getAuthor(){
        return "Andre_601";
    }
    
    @Override
    public @NotNull String getVersion(){
        return plugin.getPluginMeta().getVersion();
    }
    
    @Override
    public boolean persist(){
        return true;
    }
    
    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params){
        String[] args = params.split("_", 2);
        if(args.length <= 1)
            return null;
        
        if(!args[0].equalsIgnoreCase("translate"))
            return null;
        
        // %vanillaplus_translate_<translation_key>:<fallback>%
        
        String[] parts = args[1].split(":");
        
        String text = plugin.getTranslatorUtil().getTranslation(player.locale().toString(), parts[0]);
        String fallback = parts.length == 1 ? parts[0] : parts[1];
        
        return text == null ? fallback : text;
    }
}
