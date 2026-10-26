package net.michael.nocturne.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {

    public static final FoodProperties BLOOD_PEACH = new FoodProperties.Builder().nutrition(4).fast().alwaysEat()
            .saturationMod(0.3f)
            .build();

}
