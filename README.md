# ✨ Welcome to **EnchantedMobs**

**EnchantedMobs** is a highly configurable Minecraft plugin that dynamically assigns **powers (abilities)** to mobs.  
Mobs can automatically scale with nearby players, gain unique combat abilities, and create far more challenging and interesting encounters.

Designed for **Spigot / Paper servers**, EnchantedMobs integrates seamlessly with popular plugins such as ItemsAdder, Oraxen, MMOItems, MythicMobs, and PlaceholderAPI.

With its **event-driven ability system**, you can create powerful enemies, dynamic combat mechanics, and scalable difficulty for your server.

---

# ✨ Features

## ⚔ Dynamic Mob Powers
- Assign **abilities (powers)** to mobs from configurable files in `powers/`.
- Supports **weighted random selection**, **level budgets**, and **unique power groups**.
- Powers can modify mob behavior such as:
    - Damage & healing
    - Burning & freezing
    - Death effects
    - Explosions
    - Target control
    - Projectile mechanics

---

## 📈 Player-Based Difficulty Scaling
Mobs can automatically scale based on **nearby player power levels**.

Features include:
- Configurable **spawn probability**
- Adjustable **level ranges**
- **Maximum mob level**
- **Player scan radius**
- World/entity blacklist

This allows mobs to naturally become stronger as players progress.

---

## 👑 Elite Mob System
Enhanced mobs can display:

- **Custom names with power tags**
- **BossBars showing health and abilities**
- Limited display with a **“more…” indicator**

Players instantly know when they encounter a dangerous enemy.

---

## 🔥 Event-Based Ability System

Abilities can trigger on many combat events:

- `on-spawn`
- `on-damage`
- `on-regain`
- `on-melee-attack`
- `on-death`
- `on-target`
- `on-explode`
- `on-projectile-hit`
- `on-shoot-bow`
- `on-projectile-tick`
- `on-tick`
- `on-target-tick`

Built-in abilities include:

- `explosion`
- `lightning`
- `particle`
- `sound`
- `set_attribute`
- `set_health`
- `potion_effect`
- `freeze`
- `fire`
- `homing_projectile`
- `arrow_rain`
- `execute_command`
- `mythic_skill`

This system allows server owners to design **complex boss-like mob mechanics purely through configuration**.

---

## 🧰 Item & Plugin Ecosystem Integration

- Save held items directly as plugin configuration.
- Export items into `generated-item-format.yml`.
- Fully compatible with major item plugins.
- Supports **PlaceholderAPI placeholders**.

---

# 📜 Commands

Main command:
Main command:


/enchantedmobs


Aliases:


/em
/emobs


---

## Reload Plugin

/enchantedmobs reload

Reload plugin configuration, language files, powers, and items.

---

## Save Item

/enchantedmobs saveitem <id> [bukkit|itemformat]


Save the item in your main hand.

Formats:
- `bukkit` (default)
- `itemformat`

---

## Give Saved Item

/enchantedmobs givesaveitem <id> [player] [amount]


Give a previously saved item to a player.

---

## Generate Item Format

/enchantedmobs generateitemformat


Export your held item to `generated-item-format.yml`.

---

## Spawn Mob With Powers

/enchantedmobs spawnmob <entityType> <level> <power...> [world x y z]


Spawn a mob with specific powers.

---

## Spawn Random Powered Mob

/enchantedmobs spawnrandommob <level> <entityType> [world x y z]


Spawn a mob with randomly assigned powers based on level.

---

## Check Player Power

/enchantedmobs power [player]


Check your own or another player's power level.

---

## Check Nearby Player Power

/enchantedmobs chunkpower


Display the **average player power in the surrounding area**, which affects mob difficulty.

---

# ⭐ Highlights

✔ **Player-scaling mobs** – enemies become stronger as players progress  
✔ **Highly configurable** – create complex mobs without writing code  
✔ **Clear visual feedback** – names + BossBars for elite mobs  
✔ **Plugin ecosystem support** – compatible with major item plugins  
✔ **Powerful admin tools** – spawn mobs, inspect power levels, manage items