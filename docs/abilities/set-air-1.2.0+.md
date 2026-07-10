# Set Air (1.2.0+)

## Set Air

Sets the remaining air ticks of a living entity.

Use this ability for suffocation-style powers. The value is clamped between `-20` and the entity's maximum air.

```yaml
type: set_air
target: TARGET
amount: 0
```

### **Field Details**

**target**: Entity whose air will be changed. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `TARGET`.

**amount**: New remaining air value, in ticks. Supports formulas with `{level}`, `{air}`, and `{max-air}`.

```yaml
amount: '{air} - 20'
```

`20` ticks is about 1 second. Setting the value to `0` empties the oxygen bar; negative values can trigger drowning damage sooner.
