# Summon

## Summon

Spawns an entity at a location and optionally applies stats and Powers to it.

Used to create a new entity at the resolved location.\
The entity type must be spawnable, and extra stat/Power settings require the spawned entity to be a `LivingEntity`.\
The default target entity type is **SOURCE**.

#### Field Details

**entity / entity-type:** the entity type to spawn.\
Use a Bukkit `EntityType` name, for example:

```yaml
type: summon
entity: ZOMBIE
```

or

```yaml
type: summon
entity-type: CREEPER
```

***

**max-health:** sets the spawned entity's maximum health.\
If greater than `0`, this updates the entity's `MAX_HEALTH` attribute and also sets its current health to that maximum.

***

**health:** sets the spawned entity's current health.\
If greater than `0`, the final value will not exceed the entity's max health.

***

**attack-damage:** sets the spawned entity's attack damage.\
If greater than or equal to `0`, the plugin will try to update the entity's `ATTACK_DAMAGE` attribute.

***

**power-level / power.level:** the level used when assigning Powers to the spawned entity.\
Defaults to the current ability context level.

***

**powers:** directly assigns multiple Powers to the spawned entity.\
Only valid existing Power IDs are applied, and duplicates are removed.

```yaml
powers:
  - ExplosionArrow
  - WebTrap
```

***

**power.list:** an alternative form of `powers`.\
Works the same as `powers`.

***

**power:** appends one extra single Power to the spawned entity.\
If `powers` or `power.list` is also set, this value is added to that list.

```yaml
power: ExplosionArrow
```

***

**random-power-by-level / power.random-by-level:**\
If no valid `powers` / `power.list` / `power` are configured, this can be used to assign Powers randomly based on level.

* `true` = assign random Powers by level
* `false` = do not assign random Powers

***

#### Creeper-only fields

If the spawned entity is a `Creeper`, the following extra fields are also supported:

**creeper.explosion-radius:** sets the spawned Creeper's explosion radius.\
The value is rounded to an integer and clamped to a minimum of `0`.

**creeper.fuse-ticks:** sets the spawned Creeper's fuse time in ticks.\
The value is clamped to a minimum of `1`.

**creeper.powered:** sets whether the spawned Creeper is charged.
