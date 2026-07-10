# ANY

## ANY&#x20;

If item meet any of those match item rules, we will consider it meet the rule.

```yaml
match-item:
  any:
    rarity: COMMON
    material:
      - 'diamond'
```

## ANY Of&#x20;

```yaml
match-item:
  any:
    1: 
      contains-name:
        - 'Hello'
      contains-lore:
        - 'Oh'
    2:
      contains-lore:
        - 'What'
```
