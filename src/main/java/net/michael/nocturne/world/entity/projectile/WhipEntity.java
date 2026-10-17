package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class WhipEntity extends Projectile {// the class declarations that tells it to inherit the Projectile class's properties


    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level plevel) {    // The class constructor that specifies the entity type used to create the entity and the entity type and level as the parameters
        super(entityType, plevel); // super(...) calls the constructor of the parent class, which in this case is Projectile.
        // This is basically the constructor saying "Before finishing the setup of my WhipEntity, initialise the inherited Projectile part using these arguments."
    }

    @Override
    protected void defineSynchedData() {
    }

    public int retrieve() { //declares the retrieve method that the WhipItem class uses which returns an int value
        this.discard(); // destroys the whip entity when the retrieve method is run
        return 1; // supplies the durability damage amount to WhipItem.hurtAndBreak()
    }
    public WhipEntity(Player player, Level level) {// The second class constructor(or convenience constructor) that lets the Whip item create the WhipEntity without manually supplying the entity type each time
        this(ModEntities.WHIP_TIPS.get(), level); // this(...) delegates the other constructor in the same class which then calls super(...) to initialize the parent 'Projectile' class
        this.setOwner(player); //sets this WHIP_TIPS instance to belong to the player that used the whip
        double playerX = player.getX(); // the players X Co-ord
        double playerEyeY = player.getEyeY();// represents the vertical position of the player's eyes
        double playerZ = player.getZ(); // the players Z Co-ord
        this.moveTo(playerX, playerEyeY, playerZ); // moves the WHIP_TIPS entity to the specified position
    }
     public static WhipEntity findActive(Player player){ // Declares the findActive Method belonging to this class and passes the player as the parameter. It's the method call that performs the search; the declaration creates the variable that holds the results.
         List<WhipEntity>  tips = player.level().getEntitiesOfClass( //'List<WhipEntity> tips' declares a variable that can hold a list of WhipEntity references.The call to getEntitiesOfClass(...) asks Minecraft to find matching entities in the level.
                 WhipEntity.class, //Tells the method to search for the WhipEntity.Class
                 player.getBoundingBox().inflate(40.0D), // sets the range in which it searches for whip entities. it takes the player's bounding box and expands it by 40 blocks along each axis.
                 tip -> tip.getOwner() == player); //a lambda expression that tells the method to find any entities belonging to the player
                 return tips.isEmpty() ? null : tips.get(0); // returns null if it doesn't find any, and grabs the first one it finds if it does
                //The ? : syntax is called the ternary operator. It chooses between two values depending on a condition.
                     //tips.isEmpty() checks whether the list contains no elements.
                     //null is returned if the list is empty.
                     //tips.get(0) returns the first element if the list isn't empty.

     }

    public boolean retractWhip(Player player) { //declares boolean Method that passes the Player as a parameter
        ItemStack itemStack = player.getMainHandItem(); //gets the item in the players main hand
        ItemStack itemStack2 = player.getOffhandItem(); //gets the item in the players off-hand
        boolean bl = itemStack.is(ModItems.WHIP.get()); // the bl checks if the item in the players hand is the ModItems.WHIP
        boolean bl2 = itemStack2.is(ModItems.WHIP.get());
        if (!player.isRemoved() && player.isAlive() && (bl || bl2) && !(this.distanceToSqr(player) > 1024.0)) {// if statement that checks if all the conditions are still met for the whip entity && operator means AND all the conditions must be true
            return false; //If all the conditions are met, the method returns false without discarding the entity.
        }
        this.discard(); //if not all conditions are met retractWhip discards the Whip entity
        return true;

    }
    public enum WhipEntityState{ //An enum defines a fixed collection of named constants.

    }
}
