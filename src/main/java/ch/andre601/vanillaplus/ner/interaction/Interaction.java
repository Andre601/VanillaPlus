package ch.andre601.vanillaplus.ner.interaction;

import ch.andre601.vanillaplus.ner.ItemWrapper;
import org.bukkit.Material;

public enum Interaction{
    BUDDING_AMETHYST(
        ItemWrapper.fromVanilla(Material.AMETHYST_BLOCK),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.BUDDING_AMETHYST)
    ),
    CRACKED_DEEPSLATE_BRICKS(
        ItemWrapper.fromVanilla(Material.DEEPSLATE_BRICKS),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.CRACKED_DEEPSLATE_BRICKS)
    ),
    CRACKED_DEEPSLATE_TILES(
        ItemWrapper.fromVanilla(Material.DEEPSLATE_TILES),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.CRACKED_DEEPSLATE_TILES)
    ),
    CRACKED_NETHER_BRICKS(
        ItemWrapper.fromVanilla(Material.NETHER_BRICKS),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.CRACKED_NETHER_BRICKS)
    ),
    CRACKED_POLISHED_BLACKSTONE_BRICKS(
        ItemWrapper.fromVanilla(Material.POLISHED_BLACKSTONE_BRICKS),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS)
    ),
    RED_SAND(
        ItemWrapper.fromVanilla(Material.RED_SANDSTONE),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.RED_SAND)
    ),
    SAND(
        ItemWrapper.fromVanilla(Material.SANDSTONE),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.SAND)
    ),
    COBBLESTONE(
        ItemWrapper.fromVanilla(Material.STONE),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.COBBLESTONE)
    ),
    CRACKED_STONE_BRICKS(
        ItemWrapper.fromVanilla(Material.STONE_BRICKS),
        ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        ItemWrapper.fromVanilla(Material.CRACKED_STONE_BRICKS)
    );
    
    private final ItemWrapper input, tool, result;
    private final InteractionRecipe.InteractionType type;
    
    Interaction(ItemWrapper input, ItemWrapper tool, InteractionRecipe.InteractionType type, ItemWrapper result){
        this.input = input;
        this.tool = tool;
        this.result = result;
        this.type = type;
    }
    
    public InteractionRecipe recipe(){
        return new InteractionRecipe(input, tool, type, result);
    }
}
