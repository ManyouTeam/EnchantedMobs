# Creeper Stats

## Creeper Stats

Modifies explosion-related stats of a Creeper.

Used to change a target Creeper's explosion radius, fuse time, and powered state.\
If the target is not a `Creeper`, this ability does nothing.\
The default target entity type is **SOURCE**.

#### Field Details

**explosion-radius:** sets the Creeper explosion radius.\
The value is clamped to a minimum of `0`.

**fuse-ticks:** sets the Creeper fuse time in ticks.\
The value is clamped to a minimum of `1`.

**powered:** sets whether the Creeper is charged.

* `true` = charged Creeper
* `false` = normal Creeper

#### Example

```yaml
type: creeper_stats
explosion-radius: 6
fuse-ticks: 20
powered: true
```

#### Notes

* `explosion-radius`, `fuse-ticks`, and `powered` are all optional.
* Only fields present in the config are applied. Missing fields keep their current values.
* This ability defaults to `SOURCE`, so it is typically used on the Creeper itself.
