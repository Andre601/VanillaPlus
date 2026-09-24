---
description: "The Server adds the ability to sort items in a chest by using the Middle mouse button in any slot of the chest inventory."
---

# Chest Sorting

/// warning | Known limitations
It is required to hold an item when trying to sort items.  
This is unfortunately a limitation of minecraft and nothing that a workaround can be found for
///

The Server adds the ability to sort items in a chest by using the Middle mouse button in any slot of the chest inventory.

## Customization

The Item Sorting behaviour can be changed by running `/settings sorting`.  
This will open a Dialog menu where you can change by what an inventory is sorted and in what order.

Available Sort behaviours are:

- `Alphabetically`: Sort Items by their Item name (NOT the display name!)
- `Amount`: Sort Items by their (Number of items per stack).
- `Durability`: Sort Items by their Durability. Items with no durability or a durability of one will have a sort weight of `Integer.MAX_VALUE` applied.
- `Disabled`: Disables Item Sorting completely.

Sort order can be set to ascending or descending. This has no influence when Sort behaviour is set to `Disabled`.