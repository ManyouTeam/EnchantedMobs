# Execute Command

## Execute Command

Executes commands (single or multiple), as console or player.

```yaml
type: execute_command
commands:
  - give {player} diamond 1
```

Default `target: SOURCE`.

#### Field Details

**commands**: command list executed in order.

```yaml
commands:
  - effect give {player} slowness 5 1
  - say affected {player}
```

**command**: single command; used when `commands` is empty.

```yaml
command: give {player} golden_apple 1
```

**as-console**:

* `true`: run as console (default)
* `false`: run as player

```yaml
as-console: false
```

**mode**: target player selection.

* `target-player`: current hate target
* `nearby`: all nearby players

```yaml
mode: nearby
```

**radius**: search radius for `mode: nearby`.

```yaml
radius: 16
```

#### Variables

* `{player}`: current command target player
* `{target}`: mob hate target
* `{level}`: mob level
