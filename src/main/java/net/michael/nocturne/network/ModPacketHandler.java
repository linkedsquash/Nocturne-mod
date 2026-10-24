package net.michael.nocturne.network;

import net.michael.nocturne.Nocturne;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;


public class ModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Nocturne.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextPacketId = 0;

    private ModPacketHandler() {
    }

    public static void register() {
        CHANNEL.messageBuilder(
                        SweepAttackPacket.class,
                        nextPacketId++,
                        NetworkDirection.PLAY_TO_SERVER
                )
                .encoder(SweepAttackPacket::encode)
                .decoder(SweepAttackPacket::decode)
                .consumerMainThread(SweepAttackPacket::handle)
                .add();
    }
}