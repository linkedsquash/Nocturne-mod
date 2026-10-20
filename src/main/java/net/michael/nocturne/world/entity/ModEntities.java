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
    // Hold entity registrations for this mod until Forge loads them.
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Nocturne.MOD_ID);

    // Register the flying whip tip as a miscellaneous entity, rather than a creature.
    public static final RegistryObject<EntityType<WhipEntity>> WHIP_TIPS = ENTITY_TYPES.register(
            "whip_tips",
            () -> EntityType.Builder.<WhipEntity>of(WhipEntity::new, MobCategory.MISC)
                    // Set the tip's collision size to one quarter of a block in each direction.
                    .sized(0.25F, 0.25F)
                    // Send updates to clients within four chunks of the entity.
                    .clientTrackingRange(4)
                    // Send position updates every five ticks.
                    .updateInterval(5)
                    .build("whip_tips")
    );

    public static void register(IEventBus eventBus) {
        // Attach entity registration to Forge's startup event bus.
        ENTITY_TYPES.register(eventBus);
    }
}
