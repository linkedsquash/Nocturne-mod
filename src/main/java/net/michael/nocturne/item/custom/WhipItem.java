package net.michael.nocturne.item.custom;

import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * Right-click to throw the whip tip. Right-click again to retract it (pulling in anything it hooked).
 * The tip stays out until retracted, the player stops holding the whip, or it goes out of range.
 */
public class WhipItem extends Item implements Vanishable {
    public WhipItem(final Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Everything below happens on the server; the client only plays the hand swing.
        if (!level.isClientSide) {
            WhipEntity activeTip = WhipEntity.findActive(player);

            if (activeTip != null) {
                int durabilityCost = activeTip.retrieve();
                itemStack.hurtAndBreak(durabilityCost, player, p -> p.broadcastBreakEvent(hand));

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL,
                        1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
            } else {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.NEUTRAL,
                        0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

                level.addFreshEntity(new WhipEntity(player, level));
                player.gameEvent(GameEvent.ITEM_INTERACT_START);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}
