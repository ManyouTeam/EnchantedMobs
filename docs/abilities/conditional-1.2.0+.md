# Conditional (1.2.0+)

## Conditional

Executes the first matching ability branch.

Branches can check distance between `SOURCE` and `TARGET`, the target's potion effects, and a random chance. This is useful for configurable AI-like behavior such as choosing a potion by player distance.

```yaml
type: conditional
cases:
  1:
    min-distance: 8
    target-has-potion: SLOWNESS
    abilities:
      1:
        type: launch_projectile
        entity-type: SPLASH_POTION
        potion: POISON
  2:
    max-distance: 3
    target-missing-potion: WEAKNESS
    random: 0.25
    match-entity:
      entity-types:
        - PLAYER
    abilities:
      1:
        type: launch_projectile
        entity-type: SPLASH_POTION
        potion: WEAKNESS
default:
  abilities:
    1:
      type: launch_projectile
      entity-type: SPLASH_POTION
      potion: INSTANT_DAMAGE
```

### **Field Details**

**cases**: Ordered branches. The first branch whose conditions match will execute.

**min-distance** and **max-distance**: Distance range between the source and target.

**distance**: Compare-style distance condition.

```yaml
distance:
  compare: >=
  value: 8
```

**match-entity**: Entity matcher for the condition target. Uses the same format as power `apply-rules.match-entity`.

**now-health** and **max-health**: Compare-style health conditions for the condition target.

**target-has-potion**: Requires the checked target to already have this potion effect. Can be a string or list.

**target-missing-potion**: Requires the checked target to not have this potion effect. Can be a string or list.

**condition-target**: Entity used for potion checks. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `TARGET`.

**random**: Optional branch chance from `0` to `1`.

**default**: Optional fallback branch when no case matches.
