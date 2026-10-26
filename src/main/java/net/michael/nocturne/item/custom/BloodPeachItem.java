package net.michael.nocturne.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BloodPeachItem extends Item {

    public BloodPeachItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(itemStack, level, livingEntity);

        if (!level.isClientSide) {
            double previousMaxHealth = livingEntity.getMaxHealth();
            MobEffectInstance activeHealthBoost = livingEntity.getEffect(MobEffects.HEALTH_BOOST);
            int duration = 100;
            int amplifier = 0;

            if (activeHealthBoost != null) {
                duration += activeHealthBoost.getDuration();
                amplifier += activeHealthBoost.getAmplifier() + 1;
            }

            livingEntity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, duration, amplifier));

            if (livingEntity instanceof Player player && livingEntity.getMaxHealth() > previousMaxHealth) {
                player.setHealth(livingEntity.getMaxHealth());
            }
        }

        return result;
    }
}
