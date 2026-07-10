# Mythic Skills

## Mythic Skills - Require MythicMobs

Cast skills from MythicMobs plugin.&#x20;

If `@target` fails in your Mythic skill, replace it with `@trigger`.

```yaml
type: mythic_skill
skill: FireBlast
power: 1.0
```

with this Mythic skill config in `plugins/MythicMobs/skills`.

```yaml
FireBlast:
  Skills:
    - effect:particles{particle=flame;amount=30;hS=0.4;vS=0.4;speed=0.02} @self
    - sound{s=entity.blaze.shoot;v=1;p=1} @self
    # Avoid @target if it does not work in your setup; use @trigger instead.
    - projectile{bulletType=ARROW;onTick=FireBlast-Tick;onHit=FireBlast-Hit;v=12;i=1;hR=1;vR=1;g=0.03} @trigger

FireBlast-Tick:
  Skills:
    - effect:particles{particle=flame;amount=4;hS=0.05;vS=0.05;speed=0.01} @origin

FireBlast-Hit:
  Skills:
    - damage{a=8} @trigger
    - ignite{ticks=60} @trigger
    - effect:particles{particle=explosion_large;amount=1} @origin
    - sound{s=entity.generic.explode;v=1;p=1} @origin
```
