# Potion Cloud

## Potion Cloud

Creates an area effect cloud.

```yaml
type: potion_cloud
radius: 3
duration: 120
potion: POISON
potion-duration: 100
potion-amplifier: 1
accumulate: true
```

Default `target: TARGET`.

#### Field Details

**radius**: cloud radius.

```yaml
radius: 5
```

**duration**: cloud lifetime in ticks.

```yaml
duration: 200
```

**potion**: potion effect type.

```yaml
potion: SLOWNESS
```

**potion-duration**: applied effect duration in ticks.

```yaml
potion-duration: 60
```

**potion-amplifier**: effect amplifier (`0 = level I`, `1 = level II`).

```yaml
potion-amplifier: 2
```

**accumulate**: If true and the entity already has the same effect, the new duration is added to the existing remaining duration. The higher amplifier is kept. (Added in 1.2.0)

```yaml
accumulate: true
```
