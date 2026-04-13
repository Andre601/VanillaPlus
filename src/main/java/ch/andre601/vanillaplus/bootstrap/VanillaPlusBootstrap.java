package ch.andre601.vanillaplus.bootstrap;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.handler.configuration.PrioritizedLifecycleEventHandlerConfiguration;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.BlockTypeKeys;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PreFlattenTagRegistrar;
import io.papermc.paper.tag.TagEntry;
import net.kyori.adventure.key.Key;
import org.bukkit.block.BlockType;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class VanillaPlusBootstrap implements PluginBootstrap{
    
    @Override
    public void bootstrap(BootstrapContext context){
        final LifecycleEventManager<BootstrapContext> manager = context.getLifecycleManager();
        PrioritizedLifecycleEventHandlerConfiguration<BootstrapContext> blockTagConfig = LifecycleEvents.TAGS.preFlatten(RegistryKey.BLOCK).newHandler(event -> {
            final PreFlattenTagRegistrar<BlockType> blockTagRegistrar = event.registrar();
            blockTagRegistrar.setTag(
                TagKey.create(RegistryKey.BLOCK, Key.key("vanillaplus:scythe_harvestable")),
                List.of(
                    // Blocks
                    TagEntry.valueEntry(BlockTypeKeys.SHORT_GRASS),
                    TagEntry.valueEntry(BlockTypeKeys.TALL_GRASS),
                    TagEntry.valueEntry(BlockTypeKeys.SHORT_DRY_GRASS),
                    TagEntry.valueEntry(BlockTypeKeys.TALL_DRY_GRASS),
                    TagEntry.valueEntry(BlockTypeKeys.SEAGRASS),
                    TagEntry.valueEntry(BlockTypeKeys.TALL_SEAGRASS),
                    TagEntry.valueEntry(BlockTypeKeys.FERN),
                    TagEntry.valueEntry(BlockTypeKeys.LARGE_FERN),
                    TagEntry.valueEntry(BlockTypeKeys.DEAD_BUSH),
                    // Block Tags
                    TagEntry.tagEntry(BlockTypeTagKeys.FLOWERS),
                    TagEntry.tagEntry(BlockTypeTagKeys.LEAVES)
                )
            );
            blockTagRegistrar.setTag(
                TagKey.create(RegistryKey.BLOCK, Key.key("vanillaplus:convertable_to_farmland")),
                List.of(
                    TagEntry.valueEntry(BlockTypeKeys.DIRT),
                    TagEntry.valueEntry(BlockTypeKeys.DIRT_PATH),
                    TagEntry.valueEntry(BlockTypeKeys.GRASS_BLOCK)
                )
            );
        });
        
        manager.registerEventHandler(blockTagConfig);
    }
}
