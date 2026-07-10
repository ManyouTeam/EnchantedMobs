# Equip

## Equip

Match entity equipment slots (each slot uses Match Item rules).

Available slots:

* `main-hand`
* `off-hand`
* `helmet`
* `chestplate`
* `leggings`
* `boots`

```yaml
equip:
  main-hand:
    material:
      - bow
  helmet:
    has-enchants:
      - protection
```

> Current behavior: any configured slot match can return `true`.
