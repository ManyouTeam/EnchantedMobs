# 🔗Compatibility

This plugin mainly adds **direct compatibility** for some item plugins.

## **Direct compatibility** <a href="#direct-compatibility" id="direct-compatibility"></a>

### <mark style="color:red;">Directly</mark> supported item plugins list <a href="#directly-supported-item-plugins-list" id="directly-supported-item-plugins-list"></a>

You can use items from these plugins in [ItemFormat](https://ultimateshop.superiormc.cn/format/itemformat-tm).

* ItemsAdder
* Oraxen
* EcoItems
* EcoArmor
* MMOItems
* MythicMobs
* eco
* NeigeItems
* ExecutableItems
* Nexo
* CraftEngine

## NBTAPI: Extra Item Format option <a href="#nbtapi-extra-item-format-option-premium" id="nbtapi-extra-item-format-option-premium"></a>

For info about ItemFormat, please view UltimateShop wiki [here](https://ultimateshop.superiormc.cn/format/itemformat-tm).

The format of this option is:

```yaml
nbt:
  <NBT Type>:
    <NBT Key>: <NBT Value>
```

Supported NBT Type:

* byte
* short
* int
* long
* float
* double
* string

For example:

```yaml
nbt:
  string: 
    customNBT: 'Hello!'
  int:
    anotherNBTComponent.theNBTKey: 5
```

## PlaceholderAPI: Extra placeholders <a href="#placeholderapi-extra-placeholders" id="placeholderapi-extra-placeholders"></a>

w.i.p.
