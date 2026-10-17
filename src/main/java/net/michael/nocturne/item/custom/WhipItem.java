package net.michael.nocturne.item.custom;

import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class WhipItem extends Item implements Vanishable {
    public WhipItem(final Properties properties)
    {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use( Level level,  Player player, InteractionHand interactionHand){
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (player.tip_out != null) {
            if (!level.isClientSide) {
                int i = player.tip_out.retrieve(itemStack);
                itemStack.hurtAndBreak(i, player, playerx -> playerx.broadcastBreakEvent(interactionHand));
            }
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.NEUTRAL,
                    1.0F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_BREAK,
                    SoundSource.NEUTRAL,
                    0.5f,
                    0.4F /(level.getRandom().nextFloat() * 0.4F + 0.8F)
            );
            if (!level.isClientSide){
                level.addFreshEntity(new WhipEntity(Player player,Level level ));
            }

        }

        return  InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

}


