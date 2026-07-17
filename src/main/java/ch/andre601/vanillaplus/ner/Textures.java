package ch.andre601.vanillaplus.ner;

import ch.andre601.vanillaplus.VanillaPlus;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;

public class Textures{
    public static Font.TextureGlyph INTERACTION;
    
    public static void init(VanillaPlus plugin){
        ResourcePack pack = new ResourcePack(plugin, "vanillaplus");
        Namespace ns = pack.namespace("vanillaplus");
        
        ns.icon();
        
        Texture interactionTexture = ns.texture("gui/interaction");
        
        Font font = ns.font("gui", false);
        
        INTERACTION = font.glyph(interactionTexture, 222, 13);
        
        pack.register(false);
    }
}
