# Consume Food (1.2.0+)

## Consume Food

Consumes a player's saturation and/or food level.

This ability only affects players. It supports decimal food amounts by storing the remainder on the player, so repeated `0.25` consumes will remove `1` food point after four triggers.

```yaml
type: consume_food
target: TARGET
amount: 0.25
saturation-first: false
```

### **Field Details**

**target**: Player to affect. Supports `SOURCE`, `TARGET`, and `SKILL`. Defaults to `TARGET`.

**amount**: Amount to consume. Supports formulas with `{level}`, `{food}`, and `{saturation}`.

```yaml
amount: '0.2 + {level} / 200'
```

**saturation-first**: Whether `amount` should consume hidden saturation before visible food level. Defaults to `true`.

When this is `true`, the food bar may not move until the player's saturation reaches `0`.

```yaml
saturation-first: true
```

When this is `false`, decimal consumption is applied directly to the visible food level through accumulation.

```yaml
saturation-first: false
```

**saturation-amount**: Optional explicit saturation amount to consume.

**food-amount**: Optional explicit food amount to consume.

When either `saturation-amount` or `food-amount` is present, the ability ignores `amount` and `saturation-first`, then consumes the two explicit values separately.

```yaml
type: consume_food
target: SOURCE
saturation-amount: 0.5
food-amount: 0.25
```
