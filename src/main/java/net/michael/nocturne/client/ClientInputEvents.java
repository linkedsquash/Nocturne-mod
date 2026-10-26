package net.michael.nocturne.client;

import net.michael.nocturne.Nocturne;
import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.network.ModPacketHandler;
import net.michael.nocturne.network.packets.SweepAttackPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = Nocturne.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ClientInputEvents {
    public static boolean sweepPacketSent;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            sweepPacketSent = false;
            return;
        }

        boolean holdingWhip =
                minecraft.player.getMainHandItem().is(ModItems.WHIP.get())
                        || minecraft.player.getOffhandItem().is(ModItems.WHIP.get());

        boolean shouldSweep =
                minecraft.screen == null
                        && minecraft.player.isAlive()
                        && holdingWhip
                        && ModKeyMapping.SWEEP_KEY.isDown();

        if (shouldSweep && !sweepPacketSent) {
            ModPacketHandler.CHANNEL.sendToServer(new SweepAttackPacket(SweepAttackPacket.Behavior.SWEEP)); // Ask the server to start the sweep behavior.
            sweepPacketSent = true;
        } else if (!shouldSweep && sweepPacketSent) {
            ModPacketHandler.CHANNEL.sendToServer(new SweepAttackPacket(SweepAttackPacket.Behavior.RETRACT)); // Ask the server to retract when the key is released.
            sweepPacketSent = false;
        }
    }
}
