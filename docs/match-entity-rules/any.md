# Any

## Any

```yaml
match-entity:
  any:
    entity-tag:
      - 'zombies'
    mythicmobs:
      - 'Example'
```

## Any Of

```yaml
match-entity:
  any:
    1: # GROUP 1
      entity-tag:
        - 'zombies'
      mythicmobs:
        - 'Example'
    2: # GROUP 2
      entity-type:
        - 'skeleton'
```
