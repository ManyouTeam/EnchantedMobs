package cn.superiormc.enchantedmobs.objects.ability;

import cn.superiormc.enchantedmobs.objects.power.ObjectPower;
import cn.superiormc.enchantedmobs.objects.power.events.AbstractHandler;

public record AbilityContext(
        ObjectPower power,
        int level,
        AbstractHandler handler
) {
}
