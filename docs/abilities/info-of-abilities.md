# 📂Info of abilities

### Common Options

No matter which ability you use, the following options are supported:

* `type`: Required. Defines which action to run.
* `target`: Who the action applies to. Supported values:
  * `SOURCE` (source entity)
  * `SKILL` (skill carrier entity)
  * `TARGET` (target entity)
* `random`: Execution chance. `1.0` means 100%.
* `cooldown`: Cooldown time in seconds.
* Powers also have `random` and `cooldown`. If power-level limits pass, actions begin to execute. Action-level `random`/`cooldown` are independent from power-level limits.
* Actions do not provide a `times` limit directly (unless wrapped in `Limit`).
* `location`: Offset from event location.
  * `offset-x`
  * `offset-y`
  * `offset-z`

Example:

```yaml
on-target-tick:
  abilities:
    1:
      type: arrow_rain
      location:
        offset-y: 5
      target: TARGET
      random: 0.5
      cooldown: 5
```
