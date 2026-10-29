package net.michael.nocturne.network.packets;

import net.michael.nocturne.tag.ModTags;
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Sent by the client to tell the server which whip behavior the player requested. */
public record SweepAttackPacket(Behavior behavior) {

    /** The two server actions requested by pressing or releasing the sweep key. */
    public enum Behavior { SWEEP, RETRACT }

    public static void encode(SweepAttackPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.behavior()); // Write the requested action so the server can read it.
    }

    public static SweepAttackPacket decode(FriendlyByteBuf buffer) {
        return new SweepAttackPacket(buffer.readEnum(Behavior.class)); // Rebuild the action requested by the client.
    }

    public static void handle(
            SweepAttackPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> { // Run world and entity changes on the server thread.
            ServerPlayer player = context.getSender(); // Get the player who sent this packet.
            if (player == null || !player.isAlive() || player.isSpectator()) { // Reject invalid senders.
                return;
            }

            WhipEntity active = WhipEntity.findActive(player); // Find this player's current whip, if any.
            if (packet.behavior() == Behavior.RETRACT) { // A release request ends the held sweep.
                if (active != null) active.releaseSweep(); // Apply the release to the server-side whip.
                return;
            }

            ItemStack mainHand = player.getMainHandItem(); // Read the server's main-hand stack.
            ItemStack offHand = player.getOffhandItem(); // Read the server's off-hand stack.
            if (!mainHand.is(ModTags.Items.WHIPS) && !offHand.is(ModTags.Items.WHIPS)) { // Do not trust the client's held-item claim.
                return;
            }

            if (active == null) {
                Level level = player.level(); // Use the player's server world.
                WhipEntity whip = new WhipEntity(player, level); // Create the whip owned by this player.
                level.addFreshEntity(whip); // Add it to the server world so it synchronises to clients.
                active = whip;
            }
            active.startSweep(); // Start or switch the active whip to sweep behaviour.
        });
        context.setPacketHandled(true); // Mark the packet handled regardless of the requested action.
    }
}
