package ch.andre601.vanillaplus.util;

import ch.andre601.vanillaplus.VanillaPlus;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CommandInfoBuilder{
    
    private final String command;
    private final List<Argument> arguments = new ArrayList<>();
    private String description = "<i>No Description provided.</i>";
    
    public CommandInfoBuilder(String command){
        this.command = command;
    }
    
    public CommandInfoBuilder argument(Argument argument){
        this.arguments.add(argument);
        return this;
    }
    
    public CommandInfoBuilder description(String description){
        this.description = description;
        return this;
    }
    
    public void send(CommandSender sender){
        StringBuilder builder = new StringBuilder("<grey>Command Info: <aqua>/" + command + "<aqua>");
        
        builder.append('\n').append('\n').append("<grey>Syntax: <aqua>/").append(command).append("</aqua>");
        
        for(Argument argument : arguments){
            builder.append(' ').append(argument.asMMString());
        }
        
        builder.append('\n').append('\n').append(description);
        
        builder.append('\n').append('\n').append("<grey><i>Hover over an argument for more info</i></grey>");
        
        sender.sendMessage(VanillaPlus.MM.deserialize(builder.toString()));
    }
    
    
    public record Argument(String name, String hover, boolean required){
        public String asMMString(){
            return String.format(
                "<grey><hover:show_text:\"%s\">%s<white>%s</white>%s</hover></grey>",
                hover(),
                required() ? '<' : '[',
                name(),
                required() ? '>' : ']'
            );
        }
    }
}
