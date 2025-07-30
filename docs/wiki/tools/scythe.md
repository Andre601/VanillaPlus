# Scythe

/// html | div[style="float: right; margin-left: .75rem;"]
<table>
  <thead>
    <tr>
      <th style="text-align: center;" colspan="2">Scythe</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td colspan="2"><img src="../../../assets/img/items/scythe.gif" style="max-width: 250px;">
    </tr>
    <tr>
      <td>Durability</td>
      <td>Wood: 59<br>Stone: 131<br>Gold: 32<br>Copper: 190<br>Iron: 250<br>Diamond: 1561<br>Netherite: 2031</td>
    </tr>
    <tr>
      <td>Stackable</td>
      <td>No</td>
    </tr>
  </tbody>
</table>
///

**Scythe** is a tool that allows you to harvest multiple crops and plants simultaneously.

## Usage

Breaking a block with the Scythe will break a 3x3x3 area with the broken block as the center.  
Only blocks that match one of the following criteria will be broken, with non-matching blocks being ignored:

- The block is a fully aged, harvestable crop (Carrot, Potato, Wheat, etc).
- The block has the `#minecraft:flowers` block tag.
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