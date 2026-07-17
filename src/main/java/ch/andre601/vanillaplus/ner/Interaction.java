package ch.andre601.vanillaplus.ner;

import org.bukkit.Material;

public enum Interaction{
    BUDDING_AMETHYST(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.AMETHYST_BLOCK),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.BUDDING_AMETHYST)
    ),
    CRACKED_DEEPSLATE_BRICKS(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.DEEPSLATE_BRICKS),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.CRACKED_DEEPSLATE_BRICKS)
    ),
    CRACKED_DEEPSLATE_TILES(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.DEEPSLATE_TILES),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.CRACKED_DEEPSLATE_TILES)
    ),
    CRACKED_NETHER_BRICKS(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.NETHER_BRICKS),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.CRACKED_NETHER_BRICKS)
    ),
    CRACKED_POLISHED_BLACKSTONE_BRICKS(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.POLISHED_BLACKSTONE_BRICKS),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.CRACKED_POLISHED_BLACKSTONE_BRICKS)
    ),
    RED_SAND(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.RED_SANDSTONE),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.RED_SAND)
    ),
    SAND(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.SANDSTONE),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.SAND)
    ),
    COBBLESTONE(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.STONE),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.COBBLESTONE)
    ),
    CRACKED_STONE_BRICKS(
        InteractionRecipe.ItemWrapper.fromVanilla(Material.STONE_BRICKS),
        InteractionRecipe.ItemWrapper.fromItemsAdder("vanillaplus:aluminum_hammer"),
        InteractionRecipe.InteractionType.LEFT,
        InteractionRecipe.ItemWrapper.fromVanilla(Material.CRACKED_STONE_BRICKS)
    );
    
    private final InteractionRecipe.ItemWrapper input, tool, result;
    private final InteractionRecipe.InteractionType type;
    
    Interaction(InteractionRecipe.ItemWrapper input, InteractionRecipe.ItemWrapper tool, InteractionRecipe.InteractionType type, InteractionRecipe.ItemWrapper result){
        this.input = input;
        this.tool = tool;
        this.result = result;
        this.type = type;
    }
    
    public InteractionRecipe recipe(){
        return new InteractionRecipe(input, tool, type, result);
    }
}
