# 🎮Mechanic

## Player Strength

Mob strength depends on player strength. The higher the player strength, the stronger generated mobs become. Player strength calculation is configured in `plugins/EnchantedMobs/player-power.yml`.

```yaml
formula: "({equipment_sum} / 4) + ({backpack_max} + {backpack_avg}) / 2"
incremental-slot-update: true

rules:
  diamond-sword:
    match-item:
      material: 
        - DIAMOND_SWORD
    add-weight: 10
  netherite-armor:
    match-item:
      material-tag: 
        - minecraft:netherite_armor
    add-weight: 12
```

Options:

* `formula`: Defines the strength formula. Supported variables:
  * `{equipment_sum}`: Total strength value of equipment slots (helmet, chestplate, leggings, boots).
  * `{backpack_max}`: Highest strength value among backpack items (excluding equipment slots).
  * `{backpack_avg}`: Average strength value of backpack items (excluding equipment slots).
  * `{backpack_sum}`: Total strength value of backpack items (excluding equipment slots).
  * All PlaceholderAPI placeholders.&#x20;
* `incremental-slot-update`: Defines how player strength updates. Modes:
  * `true`: When a player joins, all item strength values are calculated once. After that, inventory changes update only affected slots by subtracting old item strength and adding new item strength.
  * `false`: Recalculates full inventory strength each time on join and inventory changes.
  * `true` has lower performance overhead in most cases. However, if your server has edge cases where slot updates are not detected, strength may become inaccurate and stay incorrect until the player rejoins.
  * On Spigot, this feature may miss some inventory updates. Paper does not have this issue.
* `rules`: Defines item strength calculation rules. You can add more rules based on the example. Top-level keys such as `diamond_sword` and `netherite_armor` are rule IDs and must be unique. Each rule includes:
  * `match-item`: Item matching rule using Match Item Format.
  * `add-weight`: How much strength value is added when the rule matches.
  * If one item matches multiple rules, values are accumulated.
* `placeholderapi-cache-ticks`: Final player power value cache for PlaceholderAPI results, in ticks. 0 = disable cache. Frequent requests for the value of PlaceholderAPI may cause severe performance issues, making it imperative to set up a cache in a timely manner.

Use `/es playerpower` to check your own strength, or `/es playerpower <player>` to check another player's strength.

If your server has **PlaceholderAPI**, you can display player strength with `%enchantedmobs_player_power%`.

## Mob Spawn

You can spawn **EnchantedMobs** in two ways:

* Command spawn: rarely used in most servers.
* Automatic spawn the plugin replaces newly spawning vanilla mobs with **EnchantedMobs**.

For automatic spawn:

* Players with `enchantedmobs.nodify` permission receive notifications when replacement occurs.
* Related config options are in `config.yml`:

```yaml
mob-power-generator:
  enabled: true
  ignore-custom-spawn: true
  player-scan-range: 48
  default-level: '25~100'
  max-level: 400
  spawn-chance: '35~70'
  disabled-worlds: []
  disabled-entity:
    none: true
```

Options:

* `enabled`: Enable/disable this feature.
* `ignore-custom-spawn`: Ignore mobs spawned by other plugins. Some plugin-spawned mobs are indistinguishable from vanilla mobs, so this is not guaranteed to work for every plugin.
* `disabled-worlds`: Worlds where this feature is disabled. Format: `["world1", "world2"]`.
* `disabled-entity`: Match Entity Format. Matched entities will not be enhanced.
* `max-level`: Maximum generated mob level. Must be a fixed number.

When a mob attempts to spawn, the plugin checks whether enhancement is disabled by the above settings. If disabled, no enhancement is applied; otherwise enhancement starts.

Each enhanced mob has a level. Higher level mobs can receive more powers. The generated level depends on nearby players: if players are found in scan range, the average value is used; otherwise the `default-level` value is used. You can configure range with `player-scan-range`.

Not all mobs are enhanced. Use `spawn-chance` to control enhancement probability.

Both `default-level` and `spawn-chance` support random range format like the example (`A~B`), but do not support formulas or variables.

## Power Rolling

Each power config has an `apply-rules` section that defines how that power is selected. Example:

```yaml
apply-rules:
  group: death
  group-unique: true
  weight: 5
  conflicts:
    - Flying
  match-entity:
    equip:
      main-hand:
        material:
          - bow
          - crossbow
          - trident
  level-weight: 15~25
```

Options:

* `group`: The group this power belongs to (`apply-rules.group`). Powers with the same group are in one pool. The plugin shuffles groups each cycle, then rolls one power per group from first to last. If rolling is not finished after one full pass, it starts another pass, and continues until all valid powers are exhausted or rolling ends.
* `group-unique`: If enabled, powers in this group can only be selected once and will not participate in later passes.
* `conflicts:` Other powers that conflict with this power will not be selected. (Added in 1.1.1)
* `weight`: Selection weight. Higher values are more likely to be selected.
* `match-entity`: Match Entity Format describing which entities can roll this power.
* `level-weight`: Each enhanced mob starts with a level budget. When a power is selected, this value is consumed from the budget. Selection ends when budget is fully consumed. Supports range format like `15~25`, variable `{level}`, and math expressions.

## Entity List Optimization

The plugin needs to detect which mobs are enhanced in real time, so it must frequently read entities existing on the server. This process is expensive, so the plugin provides an optimization mechanism: after first startup, it scans all server entities once until it finds the first enhanced mob. Then it caches the scan result and only updates the cache when new entities spawn or existing entities die.

However, on Spigot, entity spawn detection may be limited, and some enhanced mobs spawned by special methods might not be detected. This issue does not occur on Paper.

You can disable this feature in `config.yml`.

```yaml
optimize:
  enabled-entity-scanner-cache: true
```
