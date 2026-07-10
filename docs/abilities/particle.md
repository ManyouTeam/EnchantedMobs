# Particle

## Particle

Plays particle effects at location.

```yaml
type: particle
particle: FLAME
count: 8
offset-x: 0.3
offset-y: 0.2
offset-z: 0.3
extra: 0
```

Default `target: TARGET`.

#### Basic Fields

**particle**: particle type.

```yaml
particle: SMOKE
```

**count**: amount per play.

```yaml
count: 30
```

**offset-x / offset-y / offset-z**: spread range.

```yaml
offset-x: 0.5
offset-y: 1.0
offset-z: 0.5
```

**extra**: extra particle parameter (often speed).

```yaml
extra: 0.01
```

#### Special Fields (particle-specific)

**block**: block data for block particles.

```yaml
particle: BLOCK
block: STONE
```

**item**: item data for item particles.

```yaml
particle: ITEM
item: DIAMOND
```

**color / size**: color-based particles (for example `DUST`).

```yaml
particle: DUST
color: "255,0,0"
size: 1
```

**from / to**: gradient color particles (for example `DUST_COLOR_TRANSITION`).

```yaml
from: "255,0,0"
to: "0,0,255"
```

**value / delay / duration**: vibration-related parameters.

```yaml
value: 1.0
delay: 20
duration: 40
```
