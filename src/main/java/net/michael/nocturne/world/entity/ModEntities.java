package net.michael.nocturne.world.entity;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Nocturne.MOD_ID);

    public static final RegistryObject<EntityType<WhipEntity>> WHIP_TIPS = ENTITY_TYPES.register("whip_tips", () -> EntityType.Builder.<WhipEntity>of(WhipEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(5).build("whip_tips"));


    public static void register (IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
