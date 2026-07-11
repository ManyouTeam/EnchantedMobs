# Limit

## Limit

Wrapper executor: apply limits first, then execute nested `abilities`.

```yaml
type: limit
cooldown: 5
random: 0.5
abilities:
  1:
    type: PotionEffect
    effect: WEAKNESS
    duration: 60
```

Default `target: TARGET`.

#### Field Details

**abilities**: child actions that run after limits pass.

```yaml
abilities:
  1:
    type: Sound
    sound: ENTITY_WITHER_AMBIENT
```

**times / random / cooldown**: standard limit fields reused for this action group.

```yaml
times: 2
random: 0.4
cooldown: 8
```
