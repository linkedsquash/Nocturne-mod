package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.InteractionHand;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class WhipEntity extends Projectile {

    // Maximum distance, in blocks, that the whip tip may travel away from where it launched.
    public static final double MAX_REACH = 8; // Maximum distance the tip travels from its launch point.
    // Names used to store the whip's state and launch coordinates in a saved entity.
    private static final String TAG_STATE = "WhipState";
    private static final String TAG_SPAWN_X = "SpawnX";
    private static final String TAG_SPAWN_Y = "SpawnY";
    private static final String TAG_SPAWN_Z = "SpawnZ";

    //checks if the whip has been discarded
    public boolean isDiscarded = false;

    // returns true when the sweep attack is held
    public boolean sweepHeld;

    // Shared key used to synchronise the whip state between the server and client.
    private static final EntityDataAccessor<Integer> DATA_STATE =
            SynchedEntityData.defineId(WhipEntity.class, EntityDataSerializers.INT);

    // Position where this whip tip started, used to measure its travel distance.
    private Vec3 spawnPosition = Vec3.ZERO;
    private static final int SWEEP_HIT_COOLDOWN = 10;
    private final Map<UUID, Long> lastSweepHitTick = new HashMap<>();

    public float sweepDamage = 4F;
    public float lashDamage = 8F;

    public double getSweepDamage() {
        return this.sweepDamage;
    }
    public double getLashDamage() {
        return this.lashDamage;
    }


    // Constructor used by Minecraft when loading or creating this entity by type.
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {
        super(entityType, level); // Initialise the inherited Projectile state.
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

    // Returns the vector from the tip to the owner's eyes; it is not normalised.
    public Vec3 getRetractionDirection() {
        Player player = getPlayerOwner();
        if (player == null) return Vec3.ZERO; // Without a player owner, no return direction is available.
        return player.getEyePosition().subtract(this.position());
    }

    // Gives a retracting tip velocity toward its player's eyes; discards it if no player owns it.
    public void retractWhip() {

        Player player = getPlayerOwner();

        if (player == null) {
            this.discardWhip();
            return;
        }

        if (!checkRETRACTING()) {
            return;
        }

        Vec3 toPlayerDirection = getRetractionDirection();

        // Discard the tip when it is already within one block of the player's eyes.
        if (toPlayerDirection.lengthSqr() < 1.0) {
            this.discardWhip();
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
        Vec3 lookDirection = player.getLookAngle(); // The player's normalised viewing direction.
        Vec3 lookPosition = player.getEyePosition().add(lookDirection.scale(0.5)); // Start half a block ahead of the player's eyes.
        this.moveTo( // Set the whip tip's starting position and orientation.
                lookPosition.x,
                lookPosition.y - 0.5,
                lookPosition.z ,
                playerYRot,
                playerXRot
        );
        this.spawnPosition = this.position(); // Save the launch point for the reach calculation.
        this.setDeltaMovement(lookDirection.scale(2.5));
        // Launch the tip at 1.5 blocks per tick along the player's look direction.

    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_STATE, WhipEntityState.EXTENDING.ordinal());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // Save the projectile's normal data first, such as its position and owner.
        super.addAdditionalSaveData(tag);
        // Save the movement state by name, for example "EXTENDING" or "RETRACTING".
        tag.putString(TAG_STATE, getCurrentState().name());
        // Save the launch position so the whip can still measure its travel distance after loading.
        tag.putDouble(TAG_SPAWN_X, spawnPosition.x);
        tag.putDouble(TAG_SPAWN_Y, spawnPosition.y);
        tag.putDouble(TAG_SPAWN_Z, spawnPosition.z);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // Let the parent projectile restore its own saved data first.
        super.readAdditionalSaveData(tag);
        // Only try to restore the movement state if the save contains it.
        if (tag.contains(TAG_STATE)) {
            try {
                // Convert the saved state name back into a WhipEntityState value.
                setCurrentState(WhipEntityState.valueOf(tag.getString(TAG_STATE)));
            } catch (IllegalArgumentException ignored) {
                // If the saved name is unknown, start in the safe default state instead of failing to load.
                setCurrentState(WhipEntityState.EXTENDING);
            }
        }
        // Restore the launch position only when all three coordinates are present in the save.
        if (tag.contains(TAG_SPAWN_X) && tag.contains(TAG_SPAWN_Y) && tag.contains(TAG_SPAWN_Z)) {
            // Combine the saved X, Y, and Z numbers into one 3D position.
            spawnPosition = new Vec3(tag.getDouble(TAG_SPAWN_X), tag.getDouble(TAG_SPAWN_Y), tag.getDouble(TAG_SPAWN_Z));
        } else {
            // Older or incomplete saves have no launch position, so use the entity's current position.
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

    // Immediately recalls the tip, preserving the original second-use behavior.
    public int retrieve() {
        this.discardWhip();
        return 1;
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
        this.discardWhip(); // Remove the tip when the owner is invalid or has put the whip away.
        return true;

    }

    @Override
    public void tick() {
        super.tick();

        Player player = this.getPlayerOwner(); // Get the player that owns this tip.

        if (player == null) {
            this.discardWhip();
            return;
        }

        // Collision callbacks can change state or damage entities, so run them
        // only on the authoritative side and only while the tip is outbound.
        if (!this.level().isClientSide && getCurrentState() == WhipEntityState.EXTENDING && !sweepHeld) {
            this.checkCollision();
        }
        if (!this.level().isClientSide && sweepHeld && getCurrentState() == WhipEntityState.EXTENDING) {
            updateSweep(player);
        } else {
            this.move(MoverType.SELF, this.getDeltaMovement()); // Move the tip using its current velocity.
        }

        if (!this.level().isClientSide) {
            Vec3 playerEyePosition = player.getEyePosition();
            double distanceTravelled = this.position().distanceTo(spawnPosition);

            if (!sweepHeld && getCurrentState() == WhipEntityState.EXTENDING && (distanceTravelled >= MAX_REACH
                    || this.distanceToSqr(playerEyePosition) >= MAX_REACH * MAX_REACH))
            {
                setCurrentState(WhipEntityState.RETRACTING);
            }

            // Discard the tip if its owner is invalid or has switched away from the whip.
            if (this.mustRetractWhip(player)) {
                return;
            }

            // Retraction can be requested at any time, including after a collision
            // or before the tip reaches its maximum range.
            if (getCurrentState() == WhipEntityState.RETRACTING) {
                if (this.distanceToSqr(playerEyePosition) < 1) {
                    this.discardWhip();
                } else {
                    retractWhip();
                }
            }
        }
    }

    private void checkCollision() {
        HitResult hitResult =
                ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        this.onHit(hitResult);
    }

    /** Follows the player's aim at exactly MAX_REACH and damages entities intersected by this tick's path. */
    private void updateSweep(Player player) {
        Vec3 previousPosition = position();
        Vec3 targetPosition = player.getEyePosition().add(player.getLookAngle().scale(MAX_REACH));
        Vec3 movement = targetPosition.subtract(previousPosition);
        moveTo(targetPosition.x, targetPosition.y, targetPosition.z);
        setDeltaMovement(Vec3.ZERO); // Aim tracking is positional; displacement must not become projectile velocity.

        long now = level().getGameTime();
        var searchBox = new net.minecraft.world.phys.AABB(previousPosition, targetPosition).inflate(1.0);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> canHitEntity(entity))) {
            boolean onPath = target.getBoundingBox().inflate(0.3).clip(previousPosition, targetPosition).isPresent();
            boolean atTip = target.getBoundingBox().intersects(getBoundingBox());
            if (!onPath && !atTip) continue;
            Long lastHit = lastSweepHitTick.get(target.getUUID());
            if (lastHit != null && now - lastHit < SWEEP_HIT_COOLDOWN) continue;
            Player owner = getPlayerOwner();
            if (owner != null && target.hurt(damageSources().playerAttack(owner), sweepDamage)) {
                lastSweepHitTick.put(target.getUUID(), now);
                damageWhip(owner);
            }
        }
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
        if (sweepHeld) return;
        this.setCurrentState(WhipEntityState.RETRACTING);
        Entity target = hit.getEntity();
        float damage = lashDamage;
        // Keep the owner in a variable so we can check that it really is a player before using it.
        Player playerOwner = getPlayerOwner();
        // A player is needed to create this type of damage source, so only apply damage when one exists.
        if (playerOwner != null) {
            DamageSource damageSource = this.damageSources().playerAttack(playerOwner);
            if (target.hurt(damageSource, damage)) {
                damageWhip(playerOwner);
            }
        } else {
            // This whip has no player owner, so remove it instead of attempting player-sourced damage.
            this.discardWhip();
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

    public boolean fireImmune() {
        return true;
    }

    private void damageWhip(Player player) {
        ItemStack stack = player.getMainHandItem();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (!stack.is(ModItems.WHIP.get())) {
            stack = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
        }
        if (stack.is(ModItems.WHIP.get())) {
            InteractionHand breakHand = hand;
            stack.hurtAndBreak(1, player, brokenPlayer -> brokenPlayer.broadcastBreakEvent(breakHand));
        }
    }
    
    public final void discardWhip() {
        this.remove(Entity.RemovalReason.DISCARDED);
        isDiscarded = true;
    }

    public void startSweep() {
        lastSweepHitTick.clear();
        sweepHeld = true; // Remember that the server accepted the sweep behaviour.
        setCurrentState(WhipEntityState.EXTENDING); // Keep the whip in its outbound state while sweeping.
    }

    public void releaseSweep() {
        sweepHeld = false; // Stop the held sweep behaviour after a release request.
        setCurrentState(WhipEntityState.RETRACTING); // Tell the server-side entity to return to its owner.
    }

    public enum WhipEntityState { // The three movement states used by the whip tip.
        // The tip is travelling away from the player.
        EXTENDING,
        // The tip is travelling back toward the player.
        RETRACTING,
        // Reserved state for a tip that is not extending or retracting.
        ONCOOLDOWN,
        //
        HELD
    }

}
