package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.nbt.CompoundTag;
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


public class WhipEntity extends Projectile {

    // Maximum distance, in blocks, that the whip tip may travel away from where it launched.
    public static final double MAX_REACH = 8; // Maximum distance the tip travels from its launch point.
    // Names used to store the whip's state and launch coordinates in a saved entity.
    private static final String TAG_STATE = "WhipState";
    private static final String TAG_SPAWN_X = "SpawnX";
    private static final String TAG_SPAWN_Y = "SpawnY";
    private static final String TAG_SPAWN_Z = "SpawnZ";
    // Shared key used to synchronize the whip state between the server and client.
    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.INT);
    // Position where this whip tip started, used to measure its travel distance.
    private Vec3 spawnPosition = Vec3.ZERO;

    // Constructor used by Minecraft when loading or creating this entity by type.
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {
        super(entityType, level); // Initialize the inherited Projectile state.
        this.noCulling = false; // Allow Minecraft to cull this entity when it is outside the view.
    }


    public WhipEntityState getCurrentState() {
        int value = this.entityData.get(DATA_STATE);
        // Convert the saved number into one of the named states, such as EXTENDING or RETRACTING.
        WhipEntityState[] states = WhipEntityState.values();
        // Check the number first so an unexpected value cannot cause an array-index error.
        // If it is outside the valid range, use EXTENDING as a safe default.
        return value >= 0 && value < states.length ? states[value] : WhipEntityState.EXTENDING;
    }

    public void setCurrentState(WhipEntityState state) {
        this.entityData.set(DATA_STATE, state.ordinal());
    }

    public boolean checkRETRACTING() {
        // Return true only when the tip is currently travelling back to its owner.
        return getCurrentState() == WhipEntityState.RETRACTING;
    }

    // Returns the vector from the tip to the owner's eyes; it is not normalized.
    public Vec3 getRetractionDirection() {
        Player player = getPlayerOwner();
        if (player == null) return Vec3.ZERO; // Without a player owner, no return direction is available.
        return player.getEyePosition().subtract(this.position());
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
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_STATE, WhipEntityState.EXTENDING.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString(TAG_STATE, getCurrentState().name());
        tag.putDouble(TAG_SPAWN_X, spawnPosition.x);
        tag.putDouble(TAG_SPAWN_Y, spawnPosition.y);
        tag.putDouble(TAG_SPAWN_Z, spawnPosition.z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains(TAG_STATE)) {
            try {
                setCurrentState(WhipEntityState.valueOf(tag.getString(TAG_STATE)));
            } catch (IllegalArgumentException ignored) {
                setCurrentState(WhipEntityState.EXTENDING);
            }
        }
        if (tag.contains(TAG_SPAWN_X) && tag.contains(TAG_SPAWN_Y) && tag.contains(TAG_SPAWN_Z)) {
            spawnPosition = new Vec3(tag.getDouble(TAG_SPAWN_X), tag.getDouble(TAG_SPAWN_Y), tag.getDouble(TAG_SPAWN_Z));
        } else {
            spawnPosition = position();
        }
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

        if (player == null) {
            this.discard();
            return;
        }
        // Collision callbacks can change state or damage entities, so run them
        // only on the authoritative side and only while the tip is outbound.
        if (!this.level().isClientSide && getCurrentState() == WhipEntityState.EXTENDING) {
            this.checkCollision();
        }
        this.move(MoverType.SELF, this.getDeltaMovement()); // Move the tip using its current velocity.
        double distanceTravelled = this.position().distanceTo(spawnPosition);


        if (!this.level().isClientSide) {
            Vec3 playerEyePosition = player.getEyePosition();

            if (getCurrentState() == WhipEntityState.EXTENDING && (distanceTravelled >= MAX_REACH
                    || this.distanceToSqr(playerEyePosition) >= MAX_REACH * MAX_REACH))
            {
                setCurrentState(WhipEntityState.RETRACTING);
            }
            // This handles owner death/removal or switching away from
            // the whip. It deliberately does not enforce travel range.
            if (!this.mustRetractWhip(player)) {
                if (getCurrentState() == WhipEntityState.RETRACTING)
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
        // Keep the owner in a variable so we can check that it really is a player before using it.
        Player playerOwner = getPlayerOwner();
        // A player is needed to create this type of damage source, so only apply damage when one exists.
        if (playerOwner != null) {
            DamageSource damageSource = this.damageSources().playerAttack(playerOwner);
            target.hurt(damageSource, damage);
        } else {
            // This whip has no player owner, so remove it instead of attempting player-sourced damage.
            this.discard();
        }
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
        return entity != getOwner() && entity instanceof LivingEntity livingEntity
                && livingEntity.isAlive() && !livingEntity.isSpectator();
    }



    public enum WhipEntityState { // The three movement states used by the whip tip.
        // The tip is travelling away from the player.
        EXTENDING,
        // The tip is travelling back toward the player.
        RETRACTING,
        // Reserved state for a tip that is not extending or retracting.
        IDLE
    }
}
