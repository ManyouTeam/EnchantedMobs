# Launch Projectile

## Launch Projectile

Makes source entity launch a projectile.

```yaml
type: launch_projectile
entity-type: ARROW
speed: 1.5
spawn-offset: 0.8
damage: 10
extra-y: 0
fireball-yield: 1.0
fireball-incendiary: true
```

Default `target: SOURCE`.

#### Field Details

**entity-type**: projectile entity type (for example `ARROW`, `FIREBALL`).

```yaml
entity-type: FIREBALL
```

**damage:** the damage when hit to target.

```yaml
damage: 10
```

**speed**: projectile velocity multiplier.

```yaml
speed: 2.2
```

**spawn-offset:**&#x20;

```yaml
spawn-offset: 0.8
```

**extra-y**: upward addition to launch direction.

```yaml
extra-y: 0.2
```

**fireball-yield**: fireball explosion power (fireball types only).

```yaml
fireball-yield: 2.5
```

**fireball-incendiary**: whether fireball ignition is enabled.

```yaml
fireball-incendiary: false
```
