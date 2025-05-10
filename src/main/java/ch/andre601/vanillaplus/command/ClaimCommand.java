package ch.andre601.vanillaplus.command;

import ch.andre601.vanillaplus.VanillaPlus;
import ch.andre601.vanillaplus.util.Claim;
import ch.andre601.vanillaplus.util.CommandInfoBuilder;
import ch.andre601.vanillaplus.util.WorldGuardHandler;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("UnstableApiUsage")
public class ClaimCommand{
    private final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();
    
    @Command("claim")
    public void claim(CommandSourceStack source, VanillaPlus plugin){
        if(!(source.getSender() instanceof Player player)){
            source.getSender().sendMessage(VanillaPlus.MM.deserialize("<red>Command can only be executed by a player!"));
            return;
        }
        
        World world = player.getWorld();
        if(!world.getName().equalsIgnoreCase("world")){
            player.sendMessage(VanillaPlus.MM.deserialize("<red>You can only claim areas in the main world!"));
            return;
        }
        
        plugin.getClaimHandler().claim(player);
    }
    
    @Command("claim info [player]")
    public void info(CommandSourceStack source, VanillaPlus plugin, @Argument("player") Player target){
        if(!(source.getSender() instanceof  Player player)){
            source.getSender().sendMessage(VanillaPlus.MM.deserialize("<red>Command can only be executed by a player!"));
            return;
        }
        
        if(target == null)
            target = player;
        
        List<Claim> claims = plugin.getClaimHandler().claims(target);
        if(claims == null || claims.isEmpty()){
            player.sendMessage(VanillaPlus.MM.deserialize("<grey>This Player has no claims yet."));
        }else{
            player.sendMessage(VanillaPlus.MM.deserialize(String.format("<grey>Claims of <aqua>%s</aqua>:", target.getName())));
            for(int i = 0; i < claims.size(); i++){
                player.sendMessage(VanillaPlus.MM.deserialize(String.format(
                    "- <aqua><hover:show_text:\"%s\">#%d</hover></aqua>",
                    claims.get(i).toMMString(), i + 1
                )));
            }
        }
    }
    
    @Command("claim remove <world> <id>")
    @Permission("vanillaplus.command.claim.remove")
    public void remove(CommandSourceStack source, VanillaPlus plugin, @Argument("world") World world, @Argument(value = "id", suggestions = "id") String id){
        CommandSender sender = source.getSender();
        if(!plugin.getClaimHandler().hasClaim(id)){
            sender.sendMessage(VanillaPlus.MM.deserialize(String.format(
                "<red>No region with ID <white>%s</white> found!",
                id
            )));
            return;
        }
        
        RegionManager manager = WorldGuardHandler.getRegionManager(world);
        if(manager == null){
            sender.sendMessage(VanillaPlus.MM.deserialize("<red>RegionManager is unavailable!"));
            return;
        }
        
        ProtectedRegion region = manager.getRegion(id);
        if(region == null){
            sender.sendMessage(VanillaPlus.MM.deserialize(String.format(
                "<red>No region with ID <white>%s</white> found!",
                id
            )));
            return;
        }
        
        manager.removeRegion(id);
        plugin.getClaimHandler().removeClaim(id);
        
        sender.sendMessage(VanillaPlus.MM.deserialize(String.format(
            "<green>Successfully removed Region <white>%s</white>!",
            id
        )));
    }
    
    @SuppressWarnings("deprecation")
    @Command("claim title <id> [title]")
    public void title(CommandSourceStack source, VanillaPlus plugin, @Argument(value = "id", suggestions = "id") String id, @Argument("title") @Greedy String title){
        if(!(source.getSender() instanceof Player player)){
            source.getSender().sendMessage(VanillaPlus.MM.deserialize("<red>Command can only be executed by a player!"));
            return;
        }
        
        Claim claim = plugin.getClaimHandler().claimById(id);
        if(claim == null){
            player.sendMessage(VanillaPlus.MM.deserialize(String.format(
                "<red>No Claim with ID <white>%s</white> found!",
                id
            )));
            return;
        }
        
        RegionManager manager = WorldGuardHandler.getRegionManager(player.getWorld());
        if(manager == null){
            player.sendMessage(VanillaPlus.MM.deserialize("<red>RegionManager is unavailable! If this persists, report it."));
            return;
        }
        
        ProtectedRegion region = manager.getRegion(id);
        if(region == null){
            player.sendMessage(VanillaPlus.MM.deserialize(String.format(
                "<red>No region with ID <white>%s</white> found!",
                id
            )));
            return;
        }
        
        if(!region.getMembers().contains(player.getUniqueId())){
            player.sendMessage(VanillaPlus.MM.deserialize("<red>You aren't a Member of this Claim!"));
            return;
        }
        
        if(title == null || title.isEmpty()){
            region.setFlag(Flags.GREET_TITLE, String.format(
                ChatColor.GREEN  + "%s\\nClaim of %s",
                claim.regionId(),
                player.getName()
            ));
            
            player.sendMessage(VanillaPlus.MM.deserialize("<green>Successfully reset Claim title!"));
        }else{
            region.setFlag(Flags.GREET_TITLE, String.format(
                "%s\\nClaim of %s",
                legacy.serialize(VanillaPlus.MM.deserialize(title)),
                player.getName()
            ));
            
            player.sendMessage(VanillaPlus.MM.deserialize("<green>Successfully updated Claim Title!"));
        }
    }
    
    @Command("claim help [command]")
    public void help(CommandSourceStack source, VanillaPlus plugin, @Argument(value = "command", suggestions = "commands") String command){
        CommandSender sender = source.getSender();
        if(command == null || command.isEmpty()){
            sender.sendMessage(VanillaPlus.MM.deserialize("<grey>Available commands:"));
            sender.sendMessage(Component.empty());
            sender.sendMessage(VanillaPlus.MM.deserialize(
                """
                    <grey>- <click:suggest_command:/claim help claim><hover:show_text:"<grey>Claims a <aqua>3x3 chunk area</aqua>
                    
                    Click for more info."><aqua>/claim</aqua></hover></click>"""
            ));
            sender.sendMessage(VanillaPlus.MM.deserialize(
                """
                    <grey>- <click:suggest_command:/claim help help><hover:show_text:"<grey>Shows the Help page you look at or provides command info.
                    
                    Click for more info."><aqua>/claim help <white>[<grey>command</grey>]</white></aqua></hover></click>"""
            ));
            sender.sendMessage(VanillaPlus.MM.deserialize(
                """
                    <grey>- <click:suggest_command:/claim help info><hover:show_text:"<grey>Provides info about your or someone else's claims.
                    
                    Click for more info."><aqua>/claim info <white>[<grey>player</grey>]</white></aqua></hover></click>"""
            ));
            sender.sendMessage(VanillaPlus.MM.deserialize(
                """
                    <grey>- <click:suggest_command:/claim help title><hover:show_text:"<grey>Sets or resets the title of a claim you own.
                    
                    Click for more info."><aqua>/claim title <white><<grey>id</grey>> [<grey>title</grey>]</aqua></hover></click>"""
            ));
        }else{
            switch(command.toLowerCase(Locale.ROOT)){
                case "claim" -> new CommandInfoBuilder("claim")
                    .description(
                        """
                        <grey>Claims a <aqua>3x3 Chunk area</aqua> at your position.
                        <grey>Requires a <aqua>Claim Permit</aqua> in your inventory and the area cannot overlap with any other region."""
                    )
                    .send(sender);
                
                case "help" -> new CommandInfoBuilder("claim help")
                    .argument(new CommandInfoBuilder.Argument(
                        "command",
                        """
                        <grey>Type: <aqua>String</aqua>
                        
                        <grey>The command to get information for.""",
                        false
                    ))
                    .description(
                        """
                        <grey>Lists all commands available to you.
                        <grey>Providing a command argument as argument will provide info about this command."""
                    )
                    .send(sender);
                case "info" -> new CommandInfoBuilder("claim info")
                    .argument(new CommandInfoBuilder.Argument(
                        "player",
                        """
                        <grey>Type: <aqua>Player</aqua>
                        
                        <grey>The player to get Claim info from. Defaults to you if not set.""",
                        false
                    ))
                    .description("<grey>Returns a list of either your own or a specific Player's claims.")
                    .send(sender);
                case "title" -> new CommandInfoBuilder("claim title")
                    .argument(new CommandInfoBuilder.Argument(
                        "id",
                        """
                        <grey>Type: <aqua>String</aqua>
                        
                        <grey>The ID of the Claim you want to change the title of.""",
                        true
                    ))
                    .argument(new CommandInfoBuilder.Argument(
                        "title",
                        """
                        <grey>Type: <aqua>String</aqua>
                        
                        <grey>The title to set. Leave empty to reset to '\\<green>{claim_id}'.
                        <grey>Supports basic MiniMessage tags for formatting. RGB colors are not yet supported.""",
                        false
                    ))
                    .description(
                        """
                        <grey>Sets or resets the title that displays when a Player enters your claim.
                        <grey>Note that the <white>"Claim of {player}"</white> subtitle cannot be changed."""
                    )
                    .send(sender);
                default -> sender.sendMessage(VanillaPlus.MM.deserialize(String.format(
                    "<red>No command with name <white>%s</white> found.",
                    command
                )));
            }
        }
    }
    
    @Suggestions("commands")
    public List<String> commandSuggestions(){
        return List.of(
            "claim",
            "help",
            "info",
            "title"
        );
    }
    
    @Suggestions("id")
    public List<String> claimIds(CommandContext<CommandSourceStack> context, VanillaPlus plugin){
        if(!(context.sender().getSender() instanceof Player player)){
            return plugin.getClaimHandler().allClaims().stream()
                .map(Claim::regionId)
                .toList();
        }
        
        List<Claim> claims = plugin.getClaimHandler().claims(player);
        if(claims == null || claims.isEmpty())
            return Collections.emptyList();
        
        return claims.stream()
            .map(Claim::regionId)
            .toList();
    }
}
