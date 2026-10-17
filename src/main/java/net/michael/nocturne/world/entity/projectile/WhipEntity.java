package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class WhipEntity extends Projectile {
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level plevel) {
        super(entityType, plevel);
    }

    @Override
    protected void defineSynchedData() {

    }

    public int retrieve() {
        this.discard();
        return 1;
    }

    public WhipEntity(Player player, Level level) {
        this(ModEntities.WHIP_TIPS.get(), level);
        this.setOwner(player);
        double d = player.getX();
        double e = player.getEyeY();
        double n = player.getZ();
        this.moveTo(d, e, n);
    }
     public static WhipEntity findActive(Player player){
         List<WhipEntity>  tips = player.level().getEntitiesOfClass(
                 WhipEntity.class,
                 player.getBoundingBox().inflate(40.0D),
                 tip -> tip.getOwner() == player);
                 return tips.isEmpty() ? null : tips.get(0);


     }

    private boolean retractWhip(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        ItemStack itemStack2 = player.getOffhandItem();
        boolean bl = itemStack.is(ModItems.WHIP.get());
        boolean bl2 = itemStack2.is(ModItems.WHIP.get());
        if (!player.isRemoved() && player.isAlive() && (bl || bl2) && !(this.distanceToSqr(player) > 1024.0)) {
            return false;
        }
        this.discard();
        return true;

    }
    public enum WhipEntityState{

    }
}
