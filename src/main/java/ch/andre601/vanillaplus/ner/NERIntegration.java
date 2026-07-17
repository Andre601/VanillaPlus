package ch.andre601.vanillaplus.ner;

import com.github.darksoulq.ner.plugin.NerPlugin;
import com.github.darksoulq.ner.plugin.Registration;
import dev.lone.itemsadder.api.CustomStack;

public class NERIntegration implements NerPlugin{
    
    @Override
    public void register(Registration registry){
        registry.addCategory(new InteractionCategory());
        registry.addCatalyst(InteractionRecipe.class, CustomStack.getInstance("vanillaplus:interaction").getItemStack());
        
        for(Interaction interaction : Interaction.values()){
            registry.addRecipe(interaction.recipe());
        }
    }
}
