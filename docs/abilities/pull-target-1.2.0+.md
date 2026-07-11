# Pull Target (1.2.0+)

## Pull Target

Changes the target entity's velocity relative to the source entity.

This ability can pull a target toward the source, push it away, or randomly choose either direction.

```yaml
type: pull_target
target: TARGET
direction: RANDOM
speed: 0.75
vertical: 1.2
remove-powder-snow: true
```

### **Field Details**

**target**: Entity to move. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `TARGET`.

**direction**: Movement direction relative to the source. Supports `TOWARD`, `AWAY`, and `RANDOM`.

**speed**: Horizontal velocity strength. `strength` is still supported as a fallback.

**vertical**: Optional vertical velocity. When omitted, the legacy pull behavior is used.

**remove-powder-snow**: If true, removes powder snow at the target's current block before moving it. This does not remove cobwebs.
