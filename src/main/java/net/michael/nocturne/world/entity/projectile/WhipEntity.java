package net.michael.nocturne.world.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import static net.minecraft.core.registries.Registries.ENTITY_TYPE;

public class WhipEntity extends Projectile {
    public WhipEntity(EntityType<? extends WhipEntity> entityType, Level plevel) {
        super(entityType, plevel);
    }
    @Override
    protected void defineSynchedData()
    {

    }

    public WhipEntity(Player player, Level level){
        this(ENTITY_TYPE.WHIP_TIP, level);
        this.setOwner(player);
        double d = player.getX();
        double e = player.getEyeY();
        double n = player.getZ();
        this.moveTo(d, e, n);
    }


}
