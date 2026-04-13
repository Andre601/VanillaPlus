# Tags

VanillaPlus adds custom tags that are used for various features.  
This page lists all the currently existing custom tags and the items, blocks, etc. they apply to.

## Block Tags

### `#vanillaplus:scythe_harvestable` { #vanillaplus-scythe_harvestable }

Blocks containing this block tag can be harvested by the [Scythe's](../tools/scythe.md) 3x3x3 break area.

/// html | div.grid.cards
-   #### Blocks
    
    Any blocks in this list are harvestable by the Scythe:
    
    - `minecraft:brown_mushroom`
    - `minecraft:dead_bush`
    - `minecraft:fern`
    - `minecraft:large_fern`
    - `minecraft:red_mushroom`
    - `minecraft:seagrass`
    - `minecraft:short_dry_grass`
    - `minecraft:short_grass`
    - `minecraft:tall_dry_grass`
    - `minecraft:tall_grass`
    - `minecraft:tall_seagrass`

-   #### Block Tags
    
    Any block that contains one or multiple of the below listed block tags is harvestable by the Scythe:
    
    - `#minecraft:flowers`
    - `#minecraft:leaves`
///

### `#vanillaplus:convertable_to_farmland`

Blocks containing this block tag are considered to be convertable to farmland.  
The main usage is for the Scythe to not convert these blocks to farmland when right-clicked.

/// html | div.grid.cards
-   #### Blocks
    
    Any blocks in this list are convertable to farmland:

    - `minecraft:dirt`
    - `minecraft:dirt_path`
    - `minecraft:grass_block`
///