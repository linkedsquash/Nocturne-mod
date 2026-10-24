package net.michael.nocturne.client;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.world.entity.ModEntities;
//import net.minecraft.client.renderer.entity.NoopRenderer;
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Nocturne.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    // Forge calls this method when it is time to connect entity types to their client renderers.
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Tell Minecraft to draw whip tip entities using WhipEntityRenderer.
        event.registerEntityRenderer(ModEntities.WHIP_TIPS.get(), WhipEntityRenderer::new);
    }

}
