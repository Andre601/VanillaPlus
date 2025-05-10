package ch.andre601.vanillaplus;

import ch.andre601.vanillaplus.command.ClaimCommand;
import ch.andre601.vanillaplus.listener.InventoryListener;
import ch.andre601.vanillaplus.listener.PlayerListener;
import ch.andre601.vanillaplus.papi.PAPIPlaceholders;
import ch.andre601.vanillaplus.util.ClaimHandler;
import ch.andre601.vanillaplus.util.TranslatorUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.paper.PaperCommandManager;

public final class VanillaPlus extends JavaPlugin {
    public static final MiniMessage MM = MiniMessage.miniMessage();
    
    private final ClaimHandler claimHandler = new ClaimHandler(this);
    private final TranslatorUtil translatorUtil = new TranslatorUtil(this);
    
    @Override
    public void onEnable(){
        PluginManager manager = getServer().getPluginManager();
        if(!manager.isPluginEnabled("ItemsAdder")){
            getSLF4JLogger().error("ItemsAdder is not enabled. It is required.");
            manager.disablePlugin(this);
            return;
        }
        
        if(!manager.isPluginEnabled("WorldGuard")){
            getSLF4JLogger().error("WorldGuard is not enabled. It is required.");
            manager.disablePlugin(this);
            return;
        }
        
        if(!manager.isPluginEnabled("IAWAILA")){
            getSLF4JLogger().error("IAWAILA is not enabled. It is required.");
            manager.disablePlugin(this);
            return;
        }
        
        if(manager.isPluginEnabled("PlaceholderAPI") && translatorUtil.loadLanguages()){
            new PAPIPlaceholders(this).register();
            getSLF4JLogger().info("Loaded PlaceholderAPI Expansion!");
        }
        
        
        
        if(claimHandler.load()){
            getSLF4JLogger().info("Claims loaded!");
        }else{
            getSLF4JLogger().warn("Encountered issues while loading claims.json file!");
        }
        
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        loadCommands();
    }

    @Override
    public void onDisable(){
        if(claimHandler.save()){
            getSLF4JLogger().info("Saved claims.json file!");
        }else{
            getSLF4JLogger().warn("Error while saving claims.json file!");
        }
    }
    
    public ClaimHandler getClaimHandler(){
        return claimHandler;
    }
    
    public TranslatorUtil getTranslatorUtil(){
        return translatorUtil;
    }
    
    @SuppressWarnings("UnstableApiUsage")
    private void loadCommands(){
        PaperCommandManager<CommandSourceStack> manager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this);
        manager.parameterInjectorRegistry().registerInjector(VanillaPlus.class, ParameterInjector.constantInjector(this));
        
        AnnotationParser<CommandSourceStack> parser = new AnnotationParser<>(manager, CommandSourceStack.class);
        parser.parse(new ClaimCommand());
    }
}
