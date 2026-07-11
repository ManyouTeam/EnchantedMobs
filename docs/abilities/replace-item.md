# Replace Item

## Replace Item

Replaces item in a specific slot.

```yaml
type: replace_item
slot: MAIN_HAND
item:
  material: DIAMOND_SWORD
```

Default `target: SOURCE`.

#### Field Details

**slot**: equipment slot to replace.

```yaml
slot: HELMET
```

**item**: item builder config (supports existing BuildItem format).

```yaml
item:
  material: SHIELD
```
