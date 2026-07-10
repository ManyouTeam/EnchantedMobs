# Send Message

## Send Message

Sends text messages to players (single message, multi-message, or nearby mode).

```yaml
type: send_message
mode: target-player
messages:
  - "&cYou are cursed, {player}!"
```

Default `target: SOURCE`.

#### Field Details

**messages**: list of messages sent in order.

```yaml
messages:
  - "&eFirst"
  - "&cSecond"
```

**message**: single message; used when `messages` is empty.

```yaml
message: "&aHello {player}"
```

**mode**: target selection mode.

* `target-player`: current hate target player
* `nearby`: all nearby players

```yaml
mode: nearby
```

**radius**: search radius for `mode: nearby`.

```yaml
radius: 12
```

#### Variables

* `{player}`: current receiver
* `{target}`: mob hate target
* `{level}`: mob level
