package net.michael.nocturne.world.entity.projectile;

import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import static net.minecraft.core.registries.Registries.ENTITY_TYPE;

public class WhipEntity extends Projectile {
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level plevel) {
        super(entityType, plevel);
    }

    @Override
    protected void defineSynchedData() {

    }

    public WhipEntity(Player player, Level level) {
        this(ModEntities.WHIP_TIP, level);
        this.setOwner(player);
        double d = player.getX();
        double e = player.getEyeY();
        double n = player.getZ();
        this.moveTo(d, e, n);
    }

    private boolean retractWhip(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        ItemStack itemStack2 = player.getOffhandItem();
        boolean bl = itemStack.is(ModItems.WHIP);
        boolean bl2 = itemStack2.is(ModItems.WHIP);
        if (!player.isRemoved() && player.isAlive() && (bl || bl2) && !(this.distanceToSqr(player) > 1024.0)) {
            return false;
        }




    }
}
