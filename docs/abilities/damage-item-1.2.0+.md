# Damage Item (1.2.0+)

## Damage Item

Damages equipment items on a living entity.

It ignores empty slots, non-damageable items, and unbreakable items.

```yaml
type: damage_item
target: TARGET
amount: 3~5
slots:
  - ARMOR
```

### **Field Details**

**target**: Entity whose equipment will be damaged. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `TARGET`.

**amount**: Durability damage to add. Supports formulas and ranges.

```yaml
amount: 3~5
```

**slots**: Equipment slots to damage.

Supported values:

* `MAIN_HAND`
* `OFF_HAND`
* `HELMET`
* `CHESTPLATE`
* `LEGGINGS`
* `BOOTS`
* `ARMOR`

`ARMOR` damages helmet, chestplate, leggings, and boots.

```yaml
slots:
  - MAIN_HAND
  - OFF_HAND
```
