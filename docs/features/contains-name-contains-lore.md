# Contains Name/Contains Lore

## Contains Name

Item display name must contain any listed text (ignores color/format).

```yaml
match-item:
  contains-name:
    - 'test1'
```

## Contains Lore

Any lore line containing listed text will pass (ignores color/format).

```yaml
match-item:
  contains-lore:
    - 'test1'
```
