# Claims

**Claims** is a Server mechanic allowing a Player to claim a `3x3` chunks area in the main world to build in.

## Usage

Having a Claim Certificate in their Inventory, the player can use the `/claim` command in the Server's main world to try and claim an area as their own.  
Should no other Claim or the Spawn area intersect with the wanted area will the claim be confirmed and a region be created with the Player as the owner.

A normal player can only have one area as their claim. Donators will be able to have one additional claim made.  
Claims cannot be removed by a normal player. Only admins are able to do this.

## Commands

| Command                      | Description                                                                                                                                                |
|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/claim`                     | Claims a `3x3` area with the chunk of the player as the center.<br>Only works if the Player has a Claim Permit in their inventory and no other claims yet. |
| `/claim info [player]`       | Lists all claims a Player has.<br>Not providing a Player will use the command executor instead.                                                            |
| `/claim remove <world> <id>` | Removes the Claim with ID `<id>`.<br>Only executable by Admins.                                                                                            |
| `/claim title <id> [title]`  | Sets the Title to display when a player enters the claim.<br>Uses the MiniMessage text formatting. Leaving the title empty resets it to the default.       |
| `/claim help [command]`      | Lists all claim commands or provides additional info about a specific one if a command name is provided.                                                   |