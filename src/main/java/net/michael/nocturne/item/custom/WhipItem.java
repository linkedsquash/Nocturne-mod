package net.michael.nocturne.item.custom;

//imports from other classes
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

// inherits the class values from Minecraft's item class
public class WhipItem extends Item implements Vanishable {
    //the class constructor that tells java how this class behaves,
    public WhipItem(final Properties properties)
    {
        super(properties);        //calls the constructor of the parent class, Item, and passes it the item settings. Expressions from the ModItem class eventually end up here


    }
    //overrides a method from another class
    @Override
    //the return TYPE that tells minecraft that it is for an item stack
    // Declares the use method and tells it to accept the Level, Player,and interaction hand as parameters,
    // minecraft calls the use method when the whip is used
    public InteractionResultHolder<ItemStack> use( Level level,  Player player, InteractionHand interactionHand){        //after this bracket is what the use method is told to do with the WhipItem,
        ItemStack itemStack = player.getItemInHand(interactionHand);  // then it retrieves the item held in the interaction hand and stores it to apply the durability damage later

        WhipEntity tip = WhipEntity.findActive(player);        // It tells it to look for existing instances of Whip Entities and the dictates its behavior accordingly
        if (tip != null) //checks whether "tip" contains a reference to a Whip entity
        {

            if(!level.isClientSide){ // Checks that the code is running on the server side, so the entity removal and durability changes happen on the authoritative side.

                    int i = tip.retrieve();     // Calls the WhipEntity's retrieve() method, which discards the entity and returns the durability damage amount.
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
                itemStack.hurtAndBreak(i, player, playerx -> playerx.broadcastBreakEvent(interactionHand)); // If the item breaks, the lambda tells Minecraft to broadcast the break event for the hand holding the Whip.
                }


                player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);   //Notifies Minecraft that the item interaction has finished.
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
        return  InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide()); //The method returns an InteractionResultHolder to the code that called use()


    }
}




