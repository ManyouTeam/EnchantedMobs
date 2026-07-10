# Cancel Event

## Cance lEvent

Cancels the current trigger event.

`on-death` cannot be cancelled directly. If needed, use `on-death.modifier.revive-health` for similar behavior.

```yaml
type: cancel_event
```

Often used to "negate" one damage/behavior instance.
