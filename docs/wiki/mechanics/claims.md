# Claims

**Claims** is a Server mechanic allowing a Player to claim a `3x3` chunks area in the main world to build in.

## Usage

Having a Claim Certificate in their Inventory, the player can execute `/claim` in the main world to try and claim an area.  
The claim will succeed if the Player hasn't reached their Claim limit yet (See [Limits](#limits)) and the wanted area is not intersecting with other Claim regions nor the Spawn area itself.

Claims cannot be undone by the Player and only by the Server Owner.

## Limits

Players have a limit of 1 claim, where Donators can have 2 claims.

## Commands

### `/claim`

Claims a `3x3` area with the player's chunk as the center, if they have a Claim Certificate, didn't use up their [Claim limit](#limit) and the wanted area doesn't intersect with another claim region or the Spawn area.

### `/claim help [command]`

Provides info about a specific command, or lists all available commands, if no `[command]` was specified.

### `/claim info [player]`

Lists all claims a Player has.  
Not specifying any `[player]` will show the Command Executor's own info.

### `/claim remove <world> <id>`

Removes a Claim with the provided ID from the database.  
Can only be executed by server admins.

### `/claim title <id> [title]`

Sets the Title shown when entering the Claim with the specified ID.  
Not providing a `[title]` resets it back to the default (`<id>`).

Note that the subtitle cannot be changed.
