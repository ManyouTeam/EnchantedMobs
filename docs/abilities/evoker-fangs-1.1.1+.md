# Evoker Fangs (1.1.1+)

## Evoker Fangs

Summons evoker fangs around or toward the target.

When the target is within `close-range`, the ability summons two rings of fangs around the caster. Otherwise, it summons a line of fangs toward the target.

```yaml
type: evoker_fangs
target: TARGET
close-range: 3
damage: 6
```

### **Field Details**

**target**: Target entity used as the fang attack target.

```yaml
target: TARGET
```

**close-range**: Distance threshold used to choose the close-range fang pattern.

If the target is closer than this value, the fangs spawn in rings around the caster. If the target is farther away, the fangs spawn in a line toward the target.

```yaml
close-range: 3
```

**damage**: Damage dealt by each fang hit.

```yaml
damage: 6
```

The damage field supports level-based values.

```yaml
damage:
  "<35": 6
  ">=35;;<50": 8
  ">=50": 10
```
