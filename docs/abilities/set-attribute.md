# Set Attribute

## Set Attribute

Sets base value of an entity attribute.

```yaml
type: set_attribute
attribute: MAX_HEALTH
value: 40
```

Default `target: SOURCE`.

#### Field Details

**attribute**: attribute name to modify.

```yaml
attribute: MOVEMENT_SPEED
```

**value**: new value. Supports `{max}` (attribute max) and `{now}` (current attribute value).

```yaml
value: 0.4
```
