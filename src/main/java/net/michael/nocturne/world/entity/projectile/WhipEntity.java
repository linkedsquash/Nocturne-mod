package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.michael.nocturne.item.custom.WhipItem;

import java.util.List;

/**
 * The tip of the whip. Behaves like a fishing bobber, but:
 * - it is launched much faster and flatter,
 * - it persists (sticks to blocks, hooks onto entities) until the player retracts it,
 * - the damage it deals on impact scales with how fast the owner was swinging their view.
 *
 * All movement and hit logic runs on the server only; the client just receives position updates.
 */
public class WhipEntity extends Projectile {
    // Tuning values - change these to adjust how the whip feels.
    public static final float LAUNCH_SPEED = 2.5F;         // fishing bobber is roughly 0.6
    public static final float DRAG = 0.99F;
    public static final float GRAVITY = 0.02F;
    public static final double MAX_RANGE = 32.0D;          // blocks from the owner before it retracts itself
    public static final int MAX_AIRBORNE_TICKS = 60;       // retracts if it never lands or hooks within 3 seconds
    public static final float BASE_DAMAGE = 2.0F;          // damage with no swing at all
    public static final float DAMAGE_PER_SWING_DEGREE = 0.15F; // extra damage per degree of view turned per tick
    public static final float MAX_DAMAGE = 20.0F;
    public static final float SWING_SMOOTHING = 0.5F;      // 0..1, higher reacts faster to the latest tick

    // Server-side state.
    private boolean stuckInBlock = false;
    private Entity hookedEntity = null;

    // Swing tracking: how many degrees the owner's view turned per tick (smoothed).
    private float swingSpeed = 0.0F;
    private float lastOwnerYaw = 0.0F;
    private float lastOwnerPitch = 0.0F;
    private boolean swingPrimed = false;

    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level level) {
        super(entityType, level);
    }

    public WhipEntity(Player player, Level level) {
        this(ModEntities.WHIP_TIP.get(), level);
        this.setOwner(player);
        this.moveTo(player.getX(), player.getEyeY() - 0.1D, player.getZ(), player.getYRot(), player.getXRot());
        this.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, LAUNCH_SPEED, 0.0F);

        // Start measuring swing speed from the moment of the cast.
        this.lastOwnerYaw = player.getYRot();
        this.lastOwnerPitch = player.getXRot();
        this.swingPrimed = true;
    }

    @Override
    protected void defineSynchedData() {
    }

    /** Finds the whip tip this player currently has out, or null. Call on the server. */
    public static WhipEntity findActive(Player player) {
        List<WhipEntity> found = player.level().getEntitiesOfClass(
                WhipEntity.class,
                player.getBoundingBox().inflate(MAX_RANGE + 8.0D),
                whip -> whip.isAlive() && whip.getOwner() == player);
        return found.isEmpty() ? null : found.get(0);
    }

    private Player getPlayerOwner() {
        Entity owner = this.getOwner();
        return owner instanceof Player player ? player : null;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            return;
        }

        Player owner = this.getPlayerOwner();
        if (owner == null || this.shouldRetract(owner)) {
            this.discard();
            return;
        }

        this.sampleSwing(owner);

        if (this.hookedEntity != null) {
            if (this.hookedEntity.isRemoved() || this.hookedEntity.level() != this.level()) {
                // The target died or left - let the tip drop.
                this.hookedEntity = null;
            } else {
                this.setPos(this.hookedEntity.getX(), this.hookedEntity.getY(0.8D), this.hookedEntity.getZ());
            }
        } else if (this.stuckInBlock) {
            this.setDeltaMovement(Vec3.ZERO);
        } else {
            this.flyAndCollide();
        }

        if (this.distanceToSqr(owner) > MAX_RANGE * MAX_RANGE) {
            this.discard();
        }
    }

    private boolean shouldRetract(Player owner) {
        boolean holdingWhip = owner.getMainHandItem().getItem() instanceof WhipItem
                || owner.getOffhandItem().getItem() instanceof WhipItem;
        return owner.isRemoved() || !owner.isAlive() || !holdingWhip;
    }

    private void flyAndCollide() {
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            this.onHit(hit);
        }

        if (this.stuckInBlock || this.hookedEntity != null) {
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);

        float drag = this.isInWater() ? 0.8F : DRAG;
        this.setDeltaMovement(motion.scale(drag).add(0.0D, -GRAVITY, 0.0D));

        if (this.tickCount > MAX_AIRBORNE_TICKS) {
            this.discard();
        }
    }

    /** Measures how fast the owner is turning their view, in degrees per tick, smoothed over a few ticks. */
    private void sampleSwing(Player owner) {
        float yaw = owner.getYRot();
        float pitch = owner.getXRot();

        if (!this.swingPrimed) {
            this.lastOwnerYaw = yaw;
            this.lastOwnerPitch = pitch;
            this.swingPrimed = true;
            return;
        }

        float yawChange = Mth.wrapDegrees(yaw - this.lastOwnerYaw);
        float pitchChange = pitch - this.lastOwnerPitch;
        float instantSpeed = Mth.sqrt(yawChange * yawChange + pitchChange * pitchChange);

        this.swingSpeed = Mth.lerp(SWING_SMOOTHING, this.swingSpeed, instantSpeed);
        this.lastOwnerYaw = yaw;
        this.lastOwnerPitch = pitch;
    }

    public float getSwingSpeed() {
        return this.swingSpeed;
    }

    public float computeDamage() {
        return Math.min(BASE_DAMAGE + this.swingSpeed * DAMAGE_PER_SWING_DEGREE, MAX_DAMAGE);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Entity target = result.getEntity();
        target.hurt(this.damageSources().thrown(this, this.getOwner()), this.computeDamage());

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (target.isAlive()) {
            this.hookedEntity = target;
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        this.setPos(result.getLocation());
        this.setDeltaMovement(Vec3.ZERO);
        this.stuckInBlock = true;

        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.NEUTRAL, 0.8F, 1.2F);
    }

    /**
     * Pulls the hooked entity (if any) toward the owner and removes the tip.
     *
     * @return how much durability the item should lose
     */
    public int retrieve() {
        int durabilityCost = 1;
        Player owner = this.getPlayerOwner();

        if (this.hookedEntity != null && !this.hookedEntity.isRemoved() && owner != null) {
            Vec3 pull = new Vec3(
                    owner.getX() - this.getX(),
                    owner.getY() - this.getY(),
                    owner.getZ() - this.getZ()).scale(0.1D);
            this.hookedEntity.setDeltaMovement(this.hookedEntity.getDeltaMovement().add(pull));
            this.hookedEntity.hurtMarked = true;
            durabilityCost = 2;
        }

        this.discard();
        return durabilityCost;
    }
}
