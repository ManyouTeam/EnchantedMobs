# ⌨️Commands & Permissions

Main command: `/enchantedmobs`\
Aliases: `/emobs`, `/em`

> All commands below are subcommands in this format: `/enchantedmobs <subcommand> ...`

### `reload`

* Reload plugin configs, language files, items, and power managers.

### `saveitem <id> [bukkit|itemformat]` (player only)

* Save the item in your main hand under the specified ID.
* `bukkit` or default: save as Bukkit item data.
* `itemformat`: save as formatted item data.

### `givesaveitem <id> [player] [amount]`

* Give a saved item to yourself or a specified player. Console usage must include player.

### `generateitemformat` (player only)

* Export the item in your main hand to `generated-item-format.yml`.

### `spawnmob <entityType> <level> <power...> [world x y z]`

* Spawn the specified entity with a custom power list and optional world/coordinates.
* Console usage requires coordinates.

### `spawnrandommob <level> <entityType> [world x y z]`

* Spawn the specified entity type and auto-roll random powers based on level.
* Players may omit coordinates to use current location; console must provide full coordinates.

### `power [player]`

* Check your own strength or the strength of a specified online player.

### `chunkpower` (player only)

* Check nearby average player strength (used as a spawn strength reference).
