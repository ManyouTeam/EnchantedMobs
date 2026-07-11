# Disarm

## Disarm

Disarms target hand slot, optionally dropping the item.

```yaml
type: disarm
slot: MAIN_HAND
drop: true
pickup-delay: 20
match-item:
  material:
    - diamond_sword
```

Default `target: TARGET`.

#### Field Details

**slot**: disarm slot (`MAIN_HAND` / `OFF_HAND`).

```yaml
slot: OFF_HAND
```

**drop**: whether removed item is dropped on ground.

```yaml
drop: false
```

**pickup-delay**: pickup delay in ticks.

```yaml
pickup-delay: 60
```

**match-item**: which item will active this ability. Use Match Item Format here.

```yaml
match-item:
  material:
    - diamond_sword
```
