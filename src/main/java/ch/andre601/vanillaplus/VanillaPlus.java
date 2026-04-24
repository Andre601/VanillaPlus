package ch.andre601.vanillaplus;

import ch.andre601.vanillaplus.command.ClaimCommand;
import ch.andre601.vanillaplus.command.VanillaPlusCommand;
import ch.andre601.vanillaplus.listener.CauldronListener;
import ch.andre601.vanillaplus.listener.LavaFishingListener;
import ch.andre601.vanillaplus.listener.PlayerListener;
import ch.andre601.vanillaplus.object.WeightedList;
import ch.andre601.vanillaplus.papi.PAPIPlaceholders;
import ch.andre601.vanillaplus.util.*;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import dev.lone.itemsadder.api.CustomStack;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.List;

public final class VanillaPlus extends JavaPlugin {
    public static final MiniMessage MM;
    
    private final ClaimHandler claimHandler = new ClaimHandler(this);
    private final TranslatorUtil translatorUtil = new TranslatorUtil(this);
    private final ConfigUtil configUtil = new ConfigUtil(this);
    
    private WeightedList<ItemStack> fishingLoot;
    
    private LavaFishingListener lavaFishingListener;
    
    static {
        MM = MiniMessage.miniMessage();
    }
    
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
        
        if(configUtil.loadConfig()){
            List<WeightedList.Weighted<ItemStack>> items = configUtil.getWeightedItems("loot", "fishing");
            if(!items.isEmpty())
                fishingLoot = WeightedList.create(items);
        }else{
            getSLF4JLogger().warn("Error while loading config! Disabling plugin...");
            manager.disablePlugin(this);
            return;
        }
        
        if(claimHandler.load()){
            getSLF4JLogger().info("Claims loaded!");
        }else{
            getSLF4JLogger().warn("Encountered issues while loading claims.json file!");
        }
        
        getServer().getPluginManager().registerEvents(new CauldronListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        lavaFishingListener = new LavaFishingListener(this);
        loadCommands();
        
        setupScheduler();
    }

    @Override
    public void onDisable(){
        if(claimHandler.save()){
            getSLF4JLogger().info("Saved claims.json file!");
        }else{
            getSLF4JLogger().warn("Error while saving claims.json file!");
        }
        
        getServer().getScheduler().cancelTasks(this);
    }
    
    public ClaimHandler getClaimHandler(){
        return claimHandler;
    }
    
    public TranslatorUtil getTranslatorUtil(){
        return translatorUtil;
    }
    
    public LavaFishingListener getLavaFishingListener(){
        return lavaFishingListener;
    }
    
    public ConfigUtil getConfigUtil(){
        return configUtil;
    }
    
    public WeightedList<ItemStack> getFishingLoot(){
        return fishingLoot;
    }
    
    public void setFishingLoot(WeightedList<ItemStack> fishingLoot){
        this.fishingLoot = fishingLoot;
    }
    
    private void loadCommands(){
        PaperCommandManager<CommandSourceStack> manager = PaperCommandManager.builder()
            .executionCoordinator(ExecutionCoordinator.asyncCoordinator())
            .buildOnEnable(this);
        manager.parameterInjectorRegistry().registerInjector(VanillaPlus.class, ParameterInjector.constantInjector(this));
        
        AnnotationParser<CommandSourceStack> parser = new AnnotationParser<>(manager, CommandSourceStack.class);
        parser.parse(
            new ClaimCommand(),
            new VanillaPlusCommand()
        );
    }
    
    private void setupScheduler(){
        this.getServer().getScheduler().runTaskTimer(this, () -> {
            for(Player player : getServer().getOnlinePlayers()){
                CustomStack customStack = CustomStack.byItemStack(player.getInventory().getItemInMainHand());
                if(customStack == null || !customStack.getNamespacedID().equals("vanillaplus:claim_permit"))
                    continue;
                
                RegionManager manager = WorldGuardHandler.getRegionManager(player.getWorld());
                if(manager == null)
                    continue;
                
                Claim claim = Claim.create(player.getChunk());
                ProtectedCuboidRegion region = claim.asCuboidRegion(player.getWorld());
                
                if(WorldGuardHandler.canClaim(manager, region)){
                    player.sendActionBar(MM.deserialize("<green>You can claim this region with <white>/claim</white>!"));
                }else{
                    player.sendActionBar(MM.deserialize("<red>You cannot claim this region! Another region is intersecting with it."));
                }
            }
        }, 1L, 20L);
    }
}
