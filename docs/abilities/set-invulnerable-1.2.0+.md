# Set Invulnerable (1.2.0+)

## Set Invulnerable

Sets temporary invulnerability on an entity.

This ability uses an internal damage-cancel marker, so this ability can reliably prevent damage during the configured duration.

```yaml
type: set_invulnerable
target: SOURCE
value: true
duration: 60
```

### **Field Details**

**target**: Entity to make invulnerable or vulnerable. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `SOURCE`.

**value**: Whether invulnerability should be enabled. Defaults to `true`.

```yaml
value: true
```

Set this to `false` to clear invulnerability created by this ability.

```yaml
value: false
```

**duration**: How long invulnerability should last, in ticks. Supports formulas and defaults to `0`.

When `duration` is greater than `0`, the ability automatically clears invulnerability after that many ticks. If it is triggered again before the old duration ends, the newer duration is kept.

```yaml
duration: 60
```

When `duration` is `0` or omitted and `value` is `true`, invulnerability remains until another ability clears it.
