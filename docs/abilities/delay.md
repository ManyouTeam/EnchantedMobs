# Delay

## Delay

Delays execution, then runs child `abilities`.

```yaml
type: delay
ticks: 20
abilities:
  1:
    type: Sound
    sound: ENTITY_ENDERMAN_SCREAM
```

#### Field Details

**ticks**

Delay in ticks (`20 ticks = 1 second`).

```yaml
ticks: 40
```

**delay**

Legacy alias of `ticks`; used only when `ticks` is not set.

```yaml
delay: 40
```

**abilities**

Child actions to execute after delay.

```yaml
abilities:
  1:
    type: particle
    particle: FLAME
```

***
