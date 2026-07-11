# 🛠️Configuration files

The plugin generates the following configuration files, some of which will only be generated after you first use this feature.

* `items` folder: stores saved item files.
* `languages` folder: stores language message files.

These two folders are less critical. Focus on the following:

* `powers` folder: stores all created power files.
* `config.yml`: stores general plugin configuration.
* `player-power.yml`: stores how player strength is calculated.

## Config.yml file content

It is recommend that you view this file at GitHub, becuase Wiki's `config.yml` maybe not **latest**. Click [here](https://github.com/ManyouTeam/EnchantedMobs/blob/master/plugin/src/main/resources/config.yml) to view this file on **Github.**

```yaml
# EnchantedMobs by @PQguanfang
#
# Read the Wiki here: https://enchantedmobs.superiormc.cn

debug: false
debug-categories:
  spawn: true
  projectile: true
  ability: true
# Require player has enchantedmobs.nodify permission to display.
display-spawn-message: false

optimize:
  enabled-projectile-tick: true
  enabled-entity-scanner-cache: true

config-files:
  language: 'en_US'
  # Premium version only.
  per-player-language: true
  force-parse-mini-message: true

debuild-item-method: 'LEGACY'

math:
  enabled: true

mob-power-generator:
  enabled: true
  ignore-custom-spawn: true
  default-level: '15~60'
  max-level: 400
  spawn-chance: '5~25'
  player-scan-range: 48
  disabled-worlds: []
  # Ghost, Slime and Ender Dragon are disabled by default and can not being changed.
  # All peaceful mobs are disabled by default and can not being changed.
  disabled-entity:
    entity-types:
      - WARDEN
      - WITHER

mob-display:
  name:
    format: '&6[{powers}] &f{mob}'
    separator: ', '
    max-show: 4
    more: '...(+{count})'
  bossbar:
    min-powers: 2
    keep-ticks: 20
    radius: 16
    color: 'RED'
    style: 'SOLID'
    title: '&c{entity} &7[{health}/{max-health}] &f{powers_full}'

mob-combat:
  # If true, damage between monsters will be cancelled.
  disable-powered-mob-friendly-fire: false
  # If true, dropped item by disarm ability will auto return back to player inventory if they are going to removed.
  disarm-auto-return-item: true
```
