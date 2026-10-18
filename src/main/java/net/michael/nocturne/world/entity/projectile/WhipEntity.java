package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class WhipEntity extends Projectile {// the class declarations that tells it to inherit the Projectile class's properties

    private static WhipEntity.WhipEntityState currentState = WhipEntityState.EXTENDING;
    public static final double MAX_REACH = 32;

    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {    // The class constructor that specifies the entity type used to create the entity and the entity type and level as the parameters
        super(entityType, level); // super(...) calls the constructor of the parent class, which in this case is Projectile.This is basically the constructor saying "Before finishing the setup of my WhipEntity, initialise the inherited Projectile part using these arguments."
        this.noCulling = false;     // Minecraft can stop rendering it when it considers it outside the relevant view
    }
    public WhipEntity(Player player, Level level) {// The second class constructor(or convenience constructor) that lets the Whip item create the WhipEntity without manually supplying the entity type each time
        this(ModEntities.WHIP_TIPS.get(), level); // this(...) delegates the other constructor in the same class which then calls super(...) to initialize the parent 'Projectile' class
        this.setOwner(player); //sets this WHIP_TIPS instance to belong to the player that used the whip
        float playerXRot = player.getXRot(); //get the x rotation of the player
        float playerYRot = player.getYRot(); //get the y rotation of the player
        double playerX = player.getX(); // the players X Co-ord
        double playerEyeY = player.getEyeY();// represents the vertical position of the player's eyes
        double playerZ = player.getZ(); // the players Z Co-ord
        Vec3 lookDirection = player.getLookAngle(); //The result is a vector containing the X, Y and Z components of the player's viewing direction.
        Vec3 lookPosition = player.getEyePosition().add(lookDirection.scale(0.5)); // gives you the player's eye position as a Vec3, 0.5 moves that position half a block forward along the look direction.
       this.moveTo( //sets the whip's starting position
               lookPosition.x,
               lookPosition.y,
               lookPosition.z,
               playerYRot,
               playerXRot
       );
        this.setDeltaMovement(lookDirection.scale(1.5));
        //Here, scale(1.5) multiplies all three components by 1.5,
        // and setDeltaMovement(...) assigns the resulting vector as the entity's velocity.

    }

    Vec3 spawnPosition = this.position(); //saves the spawn pos
    double distanceTravelled = this.position().distanceTo(spawnPosition); //calculates the distance between the spawn pos and the current pos

    public static WhipEntityState getCurrentState() {
        return currentState;
    }

    @Override
    protected void defineSynchedData() {
    }


    public int retrieve() { //declares the retrieve method that the WhipItem class uses which returns an int value
        this.discard(); // destroys the whip entity when the retrieve method is run
        return 1; // supplies the durability damage amount to WhipItem.hurtAndBreak()
    }

     public static WhipEntity findActive(Player player){ // Declares the findActive Method belonging to this class and passes the player as the parameter.
                                                            // It's the method call that performs the search; the declaration creates the variable that holds the results.
         List<WhipEntity>  tips = player.level().getEntitiesOfClass( //'List<WhipEntity> tips' declares a variable that can hold a list of WhipEntity references.
                                                                    // The call to getEntitiesOfClass(...) asks Minecraft to find matching entities in the level.
                 WhipEntity.class, //Tells the method to search for the WhipEntity.Class
                 player.getBoundingBox().inflate(32.0D), // sets the range in which it searches for whip entities.
                                                                        // it takes the player's bounding box and expands it by 40 blocks along each axis.
                 tip -> tip.getOwner() == player); //a lambda expression that tells the method to find any entities belonging to the player
                 return tips.isEmpty() ? null : tips.get(0); // returns null if it doesn't find any, and grabs the first one it finds if it does
                //The ? : syntax is called the ternary operator. It chooses between two values depending on a condition.
                     //tips.isEmpty() checks whether the list contains no elements.
                     //null is returned if the list is empty.
                     //tips.get(0) returns the first element if the list isn't empty.

     }

    public boolean mustRetractWhip(Player player) { //declares boolean Method that passes the Player as a parameter
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

    @Override
            public void tick() {
                super.tick();
                this.move(MoverType.SELF, this.getDeltaMovement());
                Player player = this.getPlayerOwner();
                if (player == null){
                    this.discard();
                }else if (!this.level().isClientSide && player != null && !this.mustRetractWhip(player));{
                    if (currentState == WhipEntityState.EXTENDING){
                        if (distanceTravelled >= MAX_REACH) {
                            currentState = WhipEntityState.RETRACTING;
                        }
                        else{

                        }

                    }
        }


    }
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double e = 64.0;
        return distance < 4096.0;
    }
    @Override
    public boolean canChangeDimensions(){
        return false;
    }
    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player)entity : null;
    }

    public enum WhipEntityState{ //An enum defines a fixed collection of named constants.
        EXTENDING,
        RETRACTING,
        IDLE
    }
}
