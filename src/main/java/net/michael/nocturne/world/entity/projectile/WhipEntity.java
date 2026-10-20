package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;


public class WhipEntity extends Projectile { // A projectile entity that represents the whip tip.

    private WhipEntityState currentState = WhipEntityState.EXTENDING; // Tracks whether the tip is moving out or returning.
    public static final double MAX_REACH = 8; // Maximum distance the tip travels from its launch point.

    // Constructor used by Minecraft when loading or creating this entity by type.
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {
        super(entityType, level); // Initialize the inherited Projectile state.
        this.noCulling = false; // Allow Minecraft to cull this entity when it is outside the view.
    }


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

    // Returns the vector from the tip to the owner's eyes; it is not normalized.
    public Vec3 getRetractionDirection() {
        Player player = getPlayerOwner();
        if (player == null) return Vec3.ZERO; // Without a player owner, no return direction is available.
        return player.getEyePosition().subtract(this.position());
    }

    public void extendWhip() {

    }
    // Gives a retracting tip velocity toward its player's eyes; discards it if no player owns it.
    public void retractWhip() {

        Player player = getPlayerOwner();

        if (player == null) {
            this.discard();
            return;
        }

        if (!checkRETRACTING()) {
            return;
        }

        Vec3 toPlayerDirection = getRetractionDirection();

        // Discard the tip when it is already within one block of the player's eyes.
        if (toPlayerDirection.lengthSqr() < 1.0) {
            this.discard();
            return;
        }
        double distanceToPlayer = toPlayerDirection.length();
        double returnSpeed = Math.min(1.0, distanceToPlayer);
        this.setDeltaMovement(toPlayerDirection.normalize().scale(returnSpeed));
    }

    // Saved at launch and used to measure how far the tip has travelled.
    // Used to measure outbound travel distance against MAX_REACH.
    private Vec3 spawnPosition = Vec3.ZERO;



    // Creates a whip tip at the player's eyes and launches it in their look direction.
    public WhipEntity(Player player, Level level) {
        this(ModEntities.WHIP_TIPS.get(), level); // Create the entity using the type-based constructor.
        this.setOwner(player); // Record which player launched this whip tip.
        float playerXRot = player.getXRot(); // Player pitch.
        float playerYRot = player.getYRot(); // Player yaw.
        Vec3 lookDirection = player.getLookAngle(); // The player's normalized viewing direction.
        Vec3 lookPosition = player.getEyePosition().add(lookDirection.scale(0.5)); // Start half a block ahead of the player's eyes.
        this.moveTo( // Set the whip tip's starting position and orientation.
                lookPosition.x,
                lookPosition.y,
                lookPosition.z,
                playerYRot,
                playerXRot
        );
        this.spawnPosition = this.position(); // Save the launch point for the reach calculation.
        this.setDeltaMovement(lookDirection.scale(1.5));
        // Launch the tip at 1.5 blocks per tick along the player's look direction.

    }


    // No extra synchronized data is needed for this entity.
    @Override
    protected void defineSynchedData() {
    }


    public int retrieve() { // Remove this tip and return the durability damage amount to the caller.
        this.discard();
        return 1;
    }

    public static WhipEntity findActive(Player player) { // Finds the player's first active whip tip.
        List<WhipEntity> tips = player.level().getEntitiesOfClass(
                WhipEntity.class, // Search for whip tips.
                player.getBoundingBox().inflate(32.0D), // Expand the player's bounding box by 32 blocks on each axis.
                tip -> tip.getOwner() == player); // Keep only tips owned by this player.
        return tips.isEmpty() ? null : tips.get(0); // Return null when none are found, otherwise return the first match.

    }

    public boolean mustRetractWhip(Player player) { // Discard the tip if its owner is invalid or no longer holds a whip.
        ItemStack itemStack = player.getMainHandItem(); // Get the main-hand item.
        ItemStack itemStack2 = player.getOffhandItem(); // Get the off-hand item.
        boolean bl = itemStack.is(ModItems.WHIP.get()); // Check whether the main hand holds the whip.
        boolean bl2 = itemStack2.is(ModItems.WHIP.get()); // Check whether the whip is in the offhand.
        // Retracting tips may be farther away than the extension limit.
        // This method only checks whether the owner is valid and still holding
        // the whip. The distance limit is checked in tick(), where reaching it
        // changes the state to RETRACTING instead of deleting the tip.
        if (!player.isRemoved() && player.isAlive() && (bl || bl2)) {
            return false; // The owner is valid and still holds the whip.
        }
        this.discard(); // Remove the tip when the owner is invalid or has put the whip away.
        return true;

    }

    @Override
    public void tick() {
        super.tick();

        Player player = this.getPlayerOwner(); // Get the player that owns this tip.
        double distanceTravelled = this.position().distanceTo(spawnPosition);


        if (player == null) {
            this.discard();
            return;
        }
        // Collision callbacks can change state or damage entities, so run them
        // only on the authoritative side and only while the tip is outbound.
        if (!this.level().isClientSide && currentState == WhipEntityState.EXTENDING) {
            this.checkCollision();
        }
        this.move(MoverType.SELF, this.getDeltaMovement()); // Move the tip using its current velocity.


        if (!this.level().isClientSide) {
            Vec3 playerEyePosition = player.getEyePosition();

            if (currentState == WhipEntityState.EXTENDING && (distanceTravelled >= MAX_REACH
                    || this.distanceToSqr(playerEyePosition) >= MAX_REACH * MAX_REACH))
            {
                setCurrentState(WhipEntityState.RETRACTING);
            }
            // This handles owner death/removal or switching away from
            // the whip. It deliberately does not enforce travel range.
            if (!this.mustRetractWhip(player)) {
                if (currentState == WhipEntityState.RETRACTING)
                {
                    // Stop once close enough to the player's Eye. Adjust
                    // 1 to change the catch radius (it is squared distance).
                    if (this.distanceToSqr(playerEyePosition) < 1)
                    {
                        this.discard();
                    }
                    else
                    {
                        retractWhip();
                    }
                }
            }
        }
    }

    private void checkCollision() {
        HitResult hitResult =
                ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        this.onHit(hitResult);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        // The default projectile block-hit handler discards the projectile.
        // Keep the tip alive and let tick() bring it back to its owner.
        this.setCurrentState(WhipEntityState.RETRACTING);
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        super.onHitEntity(hit);
        this.setCurrentState(WhipEntityState.RETRACTING);
        Entity target = hit.getEntity();
        float damage = 8.0F;
        DamageSource damageSource = this.damageSources().playerAttack(getPlayerOwner() == null ? null : getPlayerOwner());
        target.hurt(damageSource, damage);
    }

    @Override
    // Render when the squared camera distance is less than 64 squared (4096), or 64 blocks.
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0;
    }

    @Override
    // Prevent this entity from being transferred between dimensions.
    public boolean canChangeDimensions() {
        return false;
    }

    @Nullable
    // Return the owner as a Player, or null when the owner is another entity or absent.
    public Player getPlayerOwner() {
        Entity entity = getOwner();
        return entity instanceof Player ? (Player) entity : null;
    }
    @Override
    public boolean canHitEntity(Entity entity) {
        return entity instanceof LivingEntity;
    }



    public enum WhipEntityState { // The three movement states used by the whip tip.
        EXTENDING,
        RETRACTING,
        IDLE
    }
}
