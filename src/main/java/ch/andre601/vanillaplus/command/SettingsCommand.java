package ch.andre601.vanillaplus.command;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.object.PlayerSettings;
import ch.andre601.vanillaplus.object.PlayerSettingsDataType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
@Command("settings")
@CommandDescription("Changes personal settings.")
public class SettingsCommand{
    
    @Command("sorting")
    @CommandDescription("Changes settings for the Inventory Sort feature.")
    public void settings(CommandSourceStack source, VanillaPlus plugin){
        CommandSender sender = source.getSender();
        if(!(sender instanceof Player player)){
            sender.sendRichMessage("<red>Only Players may execute this command.");
            return;
        }
        
        PlayerSettings settings = PlayerSettings.fromPlayer(player);
        
        Dialog dialog = Dialog.create(
            factory -> factory.empty().base(
                DialogBase.builder(VanillaPlus.MM.deserialize("Inventory Sort Settings"))
                    .body(Collections.singletonList(DialogBody.plainMessage(VanillaPlus.MM.deserialize("""
                        This Dialog allows you to <aqua>change settings for sorting inventories</aqua>.
                        Sorting can be done by <aqua>middle-clicking the inventory</aqua>.
                        
                        Setting the <aqua>Sort Type</aqua> to <red>Disabled</red> disables Inventory sorting."""))))
                    .inputs(inputs(settings))
                    .afterAction(DialogBase.DialogAfterAction.CLOSE)
                    .pause(false)
                    .build()
            ).type(DialogType.confirmation(
                ActionButton.builder(VanillaPlus.MM.deserialize("<green>Save changes"))
                    .tooltip(VanillaPlus.MM.deserialize("Saves your changes made within this Dialog"))
                    .action(
                        DialogAction.customClick(
                            this::handle,
                            ClickCallback.Options.builder().build()
                        )
                    )
                    .build(),
                ActionButton.builder(VanillaPlus.MM.deserialize("Close"))
                    .tooltip(VanillaPlus.MM.deserialize("""
                        Closes this Dialog.
                        
                        Any changes you made will be <red>discarded</red>."""))
                    .build()
            ))
        );
        
        player.showDialog(dialog);
    }
    
    private void handle(DialogResponseView view, Audience audience){
        UUID uuid = audience.get(Identity.UUID).orElse(null);
        if(uuid == null){
            audience.sendMessage(VanillaPlus.MM.deserialize("<red>Cannot Save changes. Player UUID was null!"));
            return;
        }
        
        Player player = Bukkit.getPlayer(uuid);
        if(player == null){
            audience.sendMessage(VanillaPlus.MM.deserialize("<red>Cannot Save changes. Player was null!"));
            return;
        }
        
        if(!player.isOnline()){
            audience.sendMessage(VanillaPlus.MM.deserialize("<red>Cannot Save changes. Player is not online!"));
            return;
        }
        
        PlayerSettings.SortType type = PlayerSettings.SortType.fromString(view.getText("sort_type"));
        PlayerSettings.SortOrder order = PlayerSettings.SortOrder.fromString(view.getText("sort_order"));
        
        PlayerSettings settings = new PlayerSettings(type, order);
        
        PersistentDataContainer container = player.getPersistentDataContainer();
        
        container.set(VanillaPlus.SETTINGS_KEY, PlayerSettingsDataType.INSTANCE, settings);
        player.sendRichMessage("<green>Successfully updated Sorting settings!");
    }
    
    private List<DialogInput> inputs(PlayerSettings settings){
        List<DialogInput> inputs = new ArrayList<>();
        
        List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>();
        for(PlayerSettings.SortType type : PlayerSettings.SortType.values()){
            entries.add(SingleOptionDialogInput.OptionEntry.create(
                type.name().toLowerCase(Locale.ROOT),
                VanillaPlus.MM.deserialize(type.name().charAt(0) + type.name().substring(1).toLowerCase(Locale.ROOT)),
                type.name().equalsIgnoreCase(settings.sortType().name())
            ));
        }
        inputs.add(DialogInput.singleOption("sort_type", VanillaPlus.MM.deserialize("Sorting Type"), entries).build());
        
        entries.clear();
        for(PlayerSettings.SortOrder order : PlayerSettings.SortOrder.values()){
            entries.add(SingleOptionDialogInput.OptionEntry.create(
                order.name().toLowerCase(Locale.ROOT),
                VanillaPlus.MM.deserialize(order.name().charAt(0) + order.name().substring(1).toLowerCase(Locale.ROOT)),
                order.name().equalsIgnoreCase(settings.sortOrder().name())
            ));
        }
        inputs.add(DialogInput.singleOption("sort_order", VanillaPlus.MM.deserialize("Sort Order"), entries).build());
        
        return inputs;
    }
}
