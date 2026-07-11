# Explosion

## Explosion

Creates an explosion at location, with optional fire and block damage control.

```yaml
type: explosion
yield: 2.0
set-fire: false
break-blocks: false
```

Default `target: TARGET`.

#### Field Details

**yield**

Explosion power (larger value = larger radius).

```yaml
yield: 4.0
```

**set-fire**

Whether the explosion ignites nearby blocks.

```yaml
set-fire: true
```

**break-blocks**

Whether the explosion breaks blocks.

```yaml
break-blocks: true
```
