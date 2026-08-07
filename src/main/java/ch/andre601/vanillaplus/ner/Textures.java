package ch.andre601.vanillaplus.ner;

import ch.andre601.vanillaplus.VanillaPlus;
import com.github.darksoulq.abyssallib.server.resource.Namespace;
import com.github.darksoulq.abyssallib.server.resource.ResourcePack;
import com.github.darksoulq.abyssallib.server.resource.asset.Font;
import com.github.darksoulq.abyssallib.server.resource.asset.Texture;

public class Textures{
    public static Font.TextureGlyph INTERACTION;
    public static Font.TextureGlyph FISHING;
    
    public static void init(VanillaPlus plugin){
        ResourcePack pack = new ResourcePack(plugin, "vanillaplus");
        Namespace ns = pack.namespace("vanillaplus");
        
        Texture interactionTexture = ns.texture("gui/interaction");
        Texture fishingTexture = ns.texture("gui/fishing");
        
        Font font = ns.font("gui", false);
        
        INTERACTION = font.glyph(interactionTexture, 222, 13);
        FISHING = font.glyph(fishingTexture, 222, 13);
        
        pack.register(false);
    }
}
