# Damage Entity (1.2.0+)

## Damage Entity

Deals direct damage to a living entity.

Use this ability when a power should actively apply extra damage outside the original event damage value.

```yaml
type: damage_entity
target: SOURCE
source: TARGET
amount: '{damage} * 0.25'
```

### **Field Details**

**target**: Entity to damage. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `TARGET`.

**source**: Entity credited as the damage source. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `SOURCE`.

**amount**: Damage amount. Supports formulas with `{level}`, `{damage}`, and `{original}`.

`{damage}` and `{original}` are available during damage-related events and represent the original event damage. Outside those events they are `0`.

```yaml
amount: 4
```

```yaml
amount: '{original} * 0.2'
```

The target must be a living entity with health above `0`.
