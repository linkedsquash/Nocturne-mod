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

    private WhipEntityState currentState = WhipEntityState.EXTENDING; // Tracks whether the tip is moving out or returning.
    public static final double MAX_REACH = 32; // Maximum distance the tip travels from its launch point.

    // Lets other code check whether the whip is extending or retracting.
    public WhipEntityState getCurrentState() {
        return currentState;
    }

    public WhipEntityState setCurrentState(WhipEntityState state) {
        return currentState = state;
    }

    public boolean checkRETRACTING() {
        return currentState == WhipEntityState.RETRACTING;
    }

    public boolean checkEXTENDING() {
        return currentState == WhipEntityState.EXTENDING;
    }

    // Finds the normalized direction from the tip back to its owner.
    public Vec3 getRetractionDirection() {
        Player player = getPlayerOwner();
        if (player == null) return Vec3.ZERO; // A missing owner gives no return direction.
        return player.getEyePosition().subtract(this.position());
    }

    public boolean extendWhip() {
        return true;
    }

    // Returns true if the tip was given return movement; false if it was removed
    // or is not currently retracting.
    public boolean retractWhip() {

        Player player = getPlayerOwner();

        if (player == null) {
            this.discard();
            return false;
        }


        if (!checkRETRACTING()) {
            return false;
        }

        Vec3 toPlayerDirection = getRetractionDirection();

        // Already close enough to the target; don't normalize a tiny vector.
        if (toPlayerDirection.lengthSqr() < 1.0) {
            this.discard();
            return false;
        }

        this.setDeltaMovement(toPlayerDirection.normalize().scale(1.5).normalize());
        return true;
    }


    // Saved at launch and used to measure how far the tip has travelled.
    // Change MAX_REACH below to adjust the maximum outbound distance.
    private Vec3 spawnPosition = Vec3.ZERO;

    // Constructor used by Minecraft when loading or creating this entity by type.
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {
        super(entityType, level); // super(...) calls the constructor of the parent class, which in this case is Projectile.This is basically the constructor saying "Before finishing the setup of my WhipEntity, initialise the inherited Projectile part using these arguments."
        this.noCulling = false;     // Minecraft can stop rendering it when it considers it outside the relevant view
    }

    // Creates a whip tip at the player's eyes and launches it in their look direction.
    public WhipEntity(Player player, Level level) {
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
        this.spawnPosition = this.position(); // Save the launch point for the reach calculation.
        this.setDeltaMovement(lookDirection.scale(1.5));
        //Here, scale(1.5) multiplies all three components by 1.5,
        // and setDeltaMovement(...) assigns the resulting vector as the entity's velocity.

    }


    // No extra synchronized data is needed for this entity.
    @Override
    protected void defineSynchedData() {
    }


    public int retrieve() { //declares the retrieve method that the WhipItem class uses which returns an int value
        this.discard(); // destroys the whip entity when the retrieve method is run
        return 1; // supplies the durability damage amount to WhipItem.hurtAndBreak()
    }

    public static WhipEntity findActive(Player player) { // Finds the player's first active whip tip.
        // It's the method call that performs the search; the declaration creates the variable that holds the results.
        List<WhipEntity> tips = player.level().getEntitiesOfClass( //'List<WhipEntity> tips' declares a variable that can hold a list of WhipEntity references.
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
        boolean bl2 = itemStack2.is(ModItems.WHIP.get()); // Check whether the whip is in the offhand.
        // Retracting tips may be farther away than the extension limit.
        // This method only checks whether the owner is valid and still holding
        // the whip. The distance limit is checked in tick(), where reaching it
        // changes the state to RETRACTING instead of deleting the tip.
        if (!player.isRemoved() && player.isAlive() && (bl || bl2)) {
            return false; //If all the conditions are met, the method returns false without discarding the entity.
        }
        this.discard(); //if not all conditions are met retractWhip discards the Whip entity
        return true;

    }

    @Override
    public void tick() {
        super.tick();

        Player player = this.getPlayerOwner(); // Get the player this tip belongs to.

        Vec3 playerEyePosition = player.getEyePosition();

        double distanceTravelled = this.position().distanceTo(spawnPosition);


        if (player == null) {
            this.discard();
            return;

        }

        if (!this.level().isClientSide) {
            if (currentState == WhipEntityState.RETRACTING) {
                retractWhip();
            }
            if (currentState == WhipEntityState.EXTENDING && (distanceTravelled >= MAX_REACH
                    || this.distanceToSqr(playerEyePosition) >= MAX_REACH * MAX_REACH)) {
                setCurrentState(WhipEntityState.RETRACTING);
            }
            // This handles owner death/removal or switching away from
            // the whip. It deliberately does not enforce travel range.
            if (!this.mustRetractWhip(player)) {
                if (currentState == WhipEntityState.RETRACTING) {
                    // Stop once close enough to the player's Eye. Adjust
                    // 1 to change the catch radius (it is squared distance).
                    if (this.distanceToSqr(playerEyePosition) < 1) {
                        this.discard();

                    } else {
                        // Recalculate direction every tick so the tip
                        // follows the player if they move while it returns.
                        this.retractWhip();
                    }
                }
            }
        }
    }

    @Override
    // Render the whip tip while it is within 64 blocks of the camera.
    public boolean shouldRenderAtSqrDistance(double distance) {
        double e = 64.0;
        return distance < 4096.0;
    }

    @Override
    // Whip tips stay in their current dimension.
    public boolean canChangeDimensions() {
        return false;
    }

    @Nullable
    // Return the owner only when it is a player.
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player) entity : null;
    }


    public enum WhipEntityState { // The three movement states used by the whip tip.
        EXTENDING,
        RETRACTING,
        IDLE
    }
}