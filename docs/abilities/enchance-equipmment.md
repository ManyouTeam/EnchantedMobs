# Enchance Equipmment

## Enhance Equipment

Enhances mob armor (material replacement / random enchants).

```yaml
type: enhance_equipment
armor-material: NETHERITE
pieces:
  - HELMET
  - CHESTPLATE
enchant:
  min-amount: 1
  max-amount: 3
  min-level: 1
  max-level: 4
  enchantments:
    - PROTECTION
```

Default `target: SOURCE`.

#### Field Details

**armor-material**: armor material prefix (`NETHERITE`, `DIAMOND`, etc.).

```yaml
armor-material: DIAMOND
```

**pieces**: armor pieces to modify.

```yaml
pieces:
  - HELMET
  - LEGGINGS
```

**enchant.min-amount**: minimum enchants per item.

```yaml
enchant:
  min-amount: 1
```

**enchant.max-amount**: maximum enchants per item.

```yaml
enchant:
  max-amount: 4
```

**enchant.min-level**: minimum enchant level.

```yaml
enchant:
  min-level: 2
```

**enchant.max-level**: maximum enchant level.

```yaml
enchant:
  max-level: 5
```

**enchant.enchantments**: enchant pool for random selection.

```yaml
enchant:
  enchantments:
    - PROTECTION
    - THORNS
```
