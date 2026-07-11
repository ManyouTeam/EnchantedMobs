# Nearby Entities (1.2.0+)

## Nearby Entities

Executes nested abilities for entities near the selected base entity or location.

Each matched nearby entity becomes `TARGET` while the nested `abilities` are executed. This makes it possible to reuse existing abilities such as `potion_effect`, `set_health`, `sound`, and `particle` as area effects.

```yaml
type: nearby_entities
target: SOURCE
radius: 7
match-entity:
  not:
    entity-tag:
      - monster
abilities:
  1:
    type: potion_effect
    target: TARGET
    potion: SLOWNESS
    duration: 80
    amplifier: 1
```

### **Field Details**

**target**: Base entity used as the center of the search.

Supported values are `SOURCE`, `TARGET`, and `SKILL`. Defaults to `SOURCE`.

```yaml
target: SOURCE
```

**radius**: Search radius on all axes.

```yaml
radius: 7
```

**radius-x**, **radius-y**, **radius-z**: Optional axis-specific search radius values.

```yaml
radius-x: 7
radius-y: 4
radius-z: 7
```

**match-entity**: Optional entity matcher used to filter nearby living entities.

It uses the same matcher format as power `apply-rules.match-entity`. The source entity is skipped automatically.

```yaml
match-entity:
  entity-tag:
    - monster
```

To affect non-monsters only:

```yaml
match-entity:
  not:
    entity-tag:
      - monster
```

**abilities**: Nested abilities to execute on every matched nearby entity.

```yaml
abilities:
  1:
    type: potion_effect
    target: TARGET
    potion: SPEED
    duration: 100
    amplifier: 0
```

For repeated area effects, wrap `nearby_entities` inside `repeat`.

```yaml
type: repeat
duration: 100
interval: 20
abilities:
  1:
    type: nearby_entities
    radius: 7
    abilities:
      1:
        type: potion_effect
        target: TARGET
        potion: SLOWNESS
        duration: 80
        amplifier: 1
```
