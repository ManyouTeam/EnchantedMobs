# Repeat (1.2.0+)

## Repeat

Executes nested abilities immediately, then repeats them for a configured duration.

Use this ability when an effect should keep running over time. For example, a death aura can use `repeat` outside `nearby_entities`.

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

### **Field Details**

**duration**: How long the nested abilities keep repeating, in ticks.

When omitted or set to `0`, the nested abilities run once immediately.

```yaml
duration: 100
```

**interval**: Repeat interval in ticks when `duration` is greater than `0`.

```yaml
interval: 20
```

**abilities**: Nested abilities to execute.

```yaml
abilities:
  1:
    type: sound
    sound: ENTITY_WITHER_AMBIENT
```
