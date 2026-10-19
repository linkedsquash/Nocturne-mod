package net.michael.nocturne.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.michael.nocturne.Nocturne;
import net.michael.nocturne.block.ModBlocks;
import net.michael.nocturne.item.ModItems;
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

import static java.nio.file.Files.getOwner;

public class WhipEntityRenderer extends EntityRenderer<WhipEntity>{

    private final BlockRenderDispatcher blockRenderer;


    public WhipEntityRenderer(EntityRendererProvider.Context context) { //class constructor that gets the entity data from WhipEntity
        super(context); // hands the render to WhipEntity using super(...)
        this.blockRenderer = context.getBlockRenderDispatcher(); // get the actual render
    }
    //PoseStack lets you transform what you're about to draw without permanently changing the world coordinates of the entity.
    //MultiBufferSource provides the rendering buffers Minecraft needs to draw the object.
    //WhipEntity entity	The actual whip entity being drawn. You can read its position, rotation and other properties.
    //float entityYaw	The entity's yaw rotation, in degrees.
    //float partialTick	A value used to interpolate between game ticks, helping movement appear smooth.
    @Override //overrides minecrafts render method
    public void render(
            WhipEntity entity, //The actual whip entity being drawn
            float entityYaw, //entity's Yaw value in degrees
            float partialTick, // A value used to interpolate between game ticks, helping movement appear smooth.
            PoseStack poseStack, //A stack of transformations used to position, rotate and scale the model while rendering.
            MultiBufferSource buffer, //Provides the vertex buffers used to draw geometry and textures.
            int packedLight // The lighting information used to render the entity at the appropriate brightness.

    ){
        Player player = entity.getPlayerOwner();
        if (player != null){
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);

            // Whip tip in world coordinates (replace this with your actual tip offset)
            Vec3 tip = getTipPosition(entity);

            // Approximate player hand position in world coordinates
            Vec3 hand = player.position();

            // Convert both endpoints to coordinates relative to the whip
            Vec3 start = tip.subtract(hand);
            Vec3 end = hand.subtract(tip);

        }
        int mainHand = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.is(ModItems.WHIP.get())){
            mainHand = -mainHand;
        }
        /*if ((this.entityRenderDispatcher.options == null || this.entityRenderDispatcher.options.getCameraType().isFirstPerson())
                && player == Minecraft.getInstance().player){}*/

        //centers the block on the entity's position.
        poseStack.translate(-0.5,-0.5,-0.5);
        //Moves the block's rendering origin by half a block along each axis, centring
        //the one-block-sized cube on the entity's position.
        //Without this, the block would extend from the entity's position in the positive X, Y and Z directions.

        //Draw a redstone block as a temporary visual marker.
        //tells MC to use the blocks default model and texture
        blockRenderer.renderSingleBlock(
                ModBlocks.BLOOD_BLOCK.get().defaultBlockState(),
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
        //Restores the transformation state saved earlier,
        // preventing translation from affecting subsequent rendering

        super.render(
                entity,
                entityYaw,
                partialTick,
                poseStack,
                buffer,
                packedLight
        );
    }
    private Vec3 getTipPosition(WhipEntity whipEntity) {
        return whipEntity.position();
    }

    @Override
    public  ResourceLocation getTextureLocation(WhipEntity entity){
        return ResourceLocation.withDefaultNamespace(
                "textures/block/blood_block.png"
        );
    }

}


