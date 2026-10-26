package net.michael.nocturne.item.custom;

//imports from other classes

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// inherits the class values from Minecraft's item class
public class WhipItem extends Item implements Vanishable {

    //the class constructor that tells java how this class behaves,
    public WhipItem(final Properties properties)
    {
        super(properties);
        //calls the constructor of the parent class, Item, and passes it the item settings. Expressions from the ModItem class eventually end up here
    }

    //Whip weapon class

    //overrides a method from another class
    @Override
    //the return TYPE that tells Minecraft that it is for an item stack
    // Declares the use method and tells it to accept the Level, Player,and interaction hand as parameters,
    // Minecraft calls the use method when the whip is used
    public InteractionResultHolder<ItemStack> use( Level level,  Player player, InteractionHand interactionHand){        //after this bracket is what the use method is told to do with the WhipItem,
        ItemStack itemStack = player.getItemInHand(interactionHand);  // then it retrieves the item held in the interaction hand and stores it to apply the durability damage later

        WhipEntity tip = WhipEntity.findActive(player);        // It tells it to look for existing instances of Whip Entities and the dictates its behavior accordingly
        if (tip != null) //checks whether "tip" contains a reference to a Whip entity
        {

            if(!level.isClientSide){ // Checks that the code is running on the server side, so the entity removal and durability changes happen on the authoritative side.

                tip.retrieve();
                level.playSound(                //tells minecraft to play a sound based on what the if statement finds

                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.AMETHYST_BLOCK_BREAK,
                        SoundSource.NEUTRAL,
                        5.0F,
                        0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
                );
                }
        }
        else
        {
            if (!level.isClientSide){
                level.playSound(                //plays a different sound when the interaction is completed

                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.NEUTRAL,
                    10f,
                    0.4F /(level.getRandom().nextFloat() * 0.4F + 0.8F)); // adds a newly created entity on the server side and not the client

                level.addFreshEntity(new WhipEntity(player ,level )); //The entity is constructed by new WhipEntity(player, level), and the world is then asked to add it. THIS IS WHERE THE WHIP ENTITY IS ACTUALLY CREATED
            }

        }
        // Minecraft runs item-use code on both the client and server. Fire this event only on the server so it is not handled twice.
        if (!level.isClientSide) {
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH); // Notify Minecraft that the interaction has finished.
        }
        return  InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()); //The method returns an InteractionResultHolder to the code that called use()
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltip, pIsAdvanced);
    }
}




