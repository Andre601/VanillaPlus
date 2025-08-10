# Scythe

{{ infobox(
  "scythe",
  img = "scythe.gif",
  durability = {
    "Wood": 59,
    "Stone": 131,
    "Gold": 32,
    "Copper": 190,
    "Iron": 250,
    "Diamond": 1561,
    "Netherite": 2031
  }
) }}

**Scythe** is a tool that allows you to harvest multiple crops and plants simultaneously.

## Usage

Breaking a block with the Scythe will break a 3x3x3 area with the broken block as the center.  
Only blocks that match one of the following criteria will be broken, with non-matching blocks being ignored:

- The block is a fully aged, harvestable crop (Carrot, Potato, Wheat, etc).
- The block has the [`#vanillaplus:scythe_harvestable`](../mechanics/tags.md#vanillaplus-scythe_harvestable) block tag.

## Obtaining

### Crafting

{{ crafting("wooden_scythe", footer=False) }}
{{ crafting("stone_scythe", header=False, footer=False) }}
{{ crafting("golden_scythe", header=False, footer=False) }}
{{ crafting("copper_scythe", header=False, footer=False) }}
{{ crafting("iron_scythe", header=False, footer=False) }}
{{ crafting("diamond_scythe", header=False) }}

### Smithing

{{ smithing("netherite_scythe") }}

## Data Values

### ID

| Name             | Identifier                     |
|------------------|--------------------------------|
| Wooden Scythe    | `vanillaplus:wooden_scythe`    |
| Stone Scythe     | `vanillaplus:stone_scythe`     |
| Golden Scythe    | `vanillaplus:_scythe`          |
| Copper Scythe    | `vanillaplus:copper_scythe`    |
| Iron Scythe      | `vanillaplus:iron_scythe`      |
| Diamond Scythe   | `vanillaplus:diamond_scythe`   |
| Netherite Scythe | `vanillaplus:netherite_scythe` |