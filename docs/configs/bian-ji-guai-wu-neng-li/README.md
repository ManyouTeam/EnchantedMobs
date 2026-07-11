# 💥Mob Powers

All mob power configuration files are stored in `plugins/EnchantedMobs/powers`.

* The filename (without extension) is the power ID. Every ID must be unique.
* To create a new power, create a new file and paste a valid power config into it.
* Most changes require a plugin reload/restart. Some features require a full server restart.
* All power configs use YAML format.

### Dynamic Numeric Values

In power configs, almost all numeric fields support math expressions and the `{level}` variable (mob level).

You can also use range-based conditions like this:

```yaml
random:
  ">=15;;<23": 0.3
  ">=23": 0.6
```

This means:

* if level is `>= 15` and `< 23`, value is `0.3`
* if level is `>= 23`, value is `0.6`

### Example Power Config

```yaml
# Web Trap
# The projectile launched by a monster with this ability will generate a temporary spider web upon landing.

enabled: true
placeholder: '{lang:power.web_arrow}'

apply-rules:
  group: ranged_projectile
  group-unique: true
  conflicts:
    - Flying
  weight: 5
  match-entity:
    equip:
      main-hand:
        material:
          - bow
          - crossbow
          - trident
  level-weight: 15~25

limit:
  random:
    ">=15;;<23": 0.3
    ">=23": 0.6
  cooldown:
    ">=15;;<22": 10
    ">=22": 5
  no-attack-ticks: 100

on-projectile-hit:
  abilities:
    1:
      type: delay
      delay:
        ">=15;;<20": 20
        ">=20": -1
      abilities:
        1:
          type: place_block
          block: cobweb
          duration: '{level} * 4'
        2:
          type: sound
          sound: BLOCK_WOOL_PLACE
        3:
          type: particle
          particle: BLOCK
          count: 20
          block: cobweb
        4:
          type: remove

on-shoot-bow:
  abilities:
    1:
      type: mark

on-projectile-tick:
  abilities:
    1:
      type: particle
      particle: WHITE_ASH
      count: 6
      offset-x: 0.05
      offset-y: 0.05
      offset-z: 0.05
```

Key fields:

* `enabled`: Enable/disable this power.
* `placeholder`: Display name placeholder.
* `apply-rules`: Power selection rules (see [Mechanics](../ji-zhi.md)).
* `limit`: Execution limits for the power:
  * `random`: chance to execute (`1.0` = 100%)
  * `cooldown`: cooldown in seconds
  * `times`: maximum execution count
  * `no-attack-ticks`: has target but didn't attack target for a certain period of time (Added in 1.1.0)
* event sections starting with `on-...`: actions that run when the event triggers.

### Power Trigger Events

A power can run actions when specific events are triggered.

Most events expose these contexts:

* **Source entity**: who triggers the event
* **Skill entity**: intermediate entity used by the event (for example, projectile)
* **Target entity**: final target entity
* **Location**: where the event happens

Power effects themselves do not recursively trigger new power events.

| Event                | Source                                                        | Skill      | Target              | Location                |
| -------------------- | ------------------------------------------------------------- | ---------- | ------------------- | ----------------------- |
| `on-projectile-hit`  | Shooter                                                       | Projectile | Hit entity (if any) | Hit location            |
| `on-shoot-bow`       | Shooter                                                       | Projectile | Projectile          | Projectile location     |
| `on-projectile-tick` | Shooter                                                       | Projectile | Projectile          | Projectile location     |
| `on-tick`            | Self                                                          | Self       | Self                | Current entity location |
| `on-spawn`           | Self                                                          | Self       | Self                | Current entity location |
| `on-combust`         | Self                                                          | Self       | Self                | Current entity location |
| `on-damage`          | Damager (if any)                                              | Self       | Self                | Current entity location |
| `on-regain`          | Self                                                          | Self       | Self                | Current entity location |
| `on-melee-attack`    | Self                                                          | Self       | Victim              | Victim location         |
| `on-death`           | Self                                                          | Self       | Self                | Current entity location |
| `on-target`          | Self                                                          | Self       | Hate target         | Target location         |
| `on-target-tick`     | Self                                                          | Self       | Hate target         | Target location         |
| `on-untarget`        | Self                                                          | Self       | Self                | Current entity location |
| `on-explode`         | Self / Shooter (if explode entity is shooted by other entity) | Self       | Self                | Current entity location |

### Event Section Structure

Each event section can contain:

* `abilities`: actions to execute
* `modifier`: modify the event values
* `conditions`: conditions that must pass

### Event Modifiers

Not every event supports modifiers. Supported examples:

#### `on-shoot-bow`

```yaml
on-shoot-bow:
  modifier:
    projectile: TNT
    fuse: 40
```

* `projectile`: new projectile type
* `fuse`: TNT fuse ticks
* `fireball-yield`: explosion yield for fireballs
* `fireball-incendiary`: whether fireball creates fire

Splash potion example:

```yaml
on-shoot-bow:
  modifier:
    projectile: SPLASH_POTION
    potion-type: HARMING
    potion-effects:
      SLOW:
        duration: "60 + {level} * 20"
        amplifier: 1
      POISON:
        duration: 100
        amplifier: 0
```

#### `on-combust`

```yaml
on-combust:
  modifier:
    duration: '60'
```

#### `on-damage`

```yaml
on-damage:
  modifier:
    damage: '{original} * (0.8 - {level} / 500)'
```

#### `on-regain`

```yaml
on-regain:
  modifier:
    amount: 15
```

#### `on-melee-attack`

```yaml
on-melee-attack:
  modifier:
    damage: '{original} * (1.2 + {level} / 50)'
```

#### `on-death` (Paper only)

```yaml
on-death:
  modifier:
    revive-health: '{original} / 2'
```

#### `on-explode`

```yaml
on-explode:
  modifier:
    yield: 5
```

### Conditions

Unless explicitly configured, conditions are considered unused.

#### Health conditions (most events except `on-death`)

```yaml
on-melee-attack:
  conditions:
    now-health:
      compare: '>='
      value: '{max-health} / 2'
    max-health:
      compare: '>='
      value: 200
```

`compare` supports: `>`, `>=`, `<`, `<=`, `!=`, `=`.

#### `on-combust`

```yaml
on-combust:
  conditions:
    by-block: false
    by-entity: false
    min-duration: 5
    max-duration: 10
```

#### `on-damage`

```yaml
on-damage:
  conditions:
    by-block: false
    by-entity: false
    min-damage: 5
    max-damage: 10
    match-damager: # Use Match Entity Format here.
      entity-types:
        - 'PLAYER'
    damage-cause:
      - 'BLOCK_EXPLOSION'
    ignore-damage-cause:
      - 'BLOCK_EXPLOSION'
```

Damage causes are listed on the [Damage Source](shang-hai-lai-yuan.md) page.

#### `on-regain`

```yaml
on-regain:
  conditions:
    min-amount: 0
    max-amount: 5
```

#### `on-melee-attack`

```yaml
on-melee-attack:
  conditions:
    accept-source: false
    min-amount: 0
    max-amount: 20
```

#### `on-explode`

```yaml
on-explode:
  conditions:
    min-yield: 0
    max-yield: 5
    from-creeper: false
```

#### `on-tag/on-untag`

```yaml
on-tag: # OR on-untag
  conditions:
    reason:
      - 'TARGET_DIED'
    ignored-reason:
      - 'NEARBY_PLAYER'
```

Possible reason:

```yaml
TARGET_DIED,
CLOSEST_PLAYER,
TARGET_ATTACKED_ENTITY,
@Deprecated
PIG_ZOMBIE_TARGET,
FORGOT_TARGET,
TARGET_ATTACKED_OWNER,
OWNER_ATTACKED_TARGET,
RANDOM_TARGET,
DEFEND_VILLAGE,
TARGET_ATTACKED_NEARBY_ENTITY,
REINFORCEMENT_TARGET,
COLLISION,
CUSTOM,
CLOSEST_ENTITY,
FOLLOW_LEADER,
TEMPT,
TARGET_OTHER_LEVEL,
TARGET_INVALID,
UNKNOWN
```
