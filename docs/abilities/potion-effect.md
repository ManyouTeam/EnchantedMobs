# Potion Effect

## Potion Effect

Applies potion effects directly to entity.

```yaml
type: potion_effect
potion: SLOWNESS
duration: 100
amplifier: 0
ambient: false
particles: true
icon: true
accumulate: true
```

Default `target: TARGET`.

#### Field Details

**potion**: potion type.

```yaml
potion: WEAKNESS
```

**duration**: ticks.

```yaml
duration: 200
```

**amplifier**: level amplifier (`0 = I`).

```yaml
amplifier: 1
```

**ambient**: ambient-style effect visuals.

```yaml
ambient: true
```

**particles**: show potion particles.

```yaml
particles: false
```

**icon**: show HUD icon.

```yaml
icon: false
```

**accumulate**: If true and the entity already has the same effect, the new duration is added to the existing remaining duration. The higher amplifier is kept. (Added in 1.2.0)

```yaml
accumulate: true
```

### RemovePotionEffect

Remove potion effects from entity.

```yaml
type: remove_potion_effect
potion: INVISIBILITY
```

Default `target: TARGET`.

#### Field Details

**potion**: potion type. Set to `ALL` means remove all potion from entity.

```yaml
potion: WEAKNESS
```

**potions:** If you has multi potion effect type to remove, you can use this format.

```yaml
potions: 
  - 'WEAKNESS'
  - 'INVISIBILI'
```
