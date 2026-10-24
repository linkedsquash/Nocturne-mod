package net.michael.nocturne.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.michael.nocturne.block.ModBlocks;
import net.michael.nocturne.world.entity.projectile.WhipEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class WhipEntityRenderer extends EntityRenderer<WhipEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public WhipEntityRenderer(EntityRendererProvider.Context context) {
        super(context);

        // Minecraft's block renderer is used below to draw the temporary tip marker.
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(
            WhipEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        // Find the player who launched this whip so the renderer can draw the line back to them.
        Player player = entity.getPlayerOwner();

        // The renderer's pose is already positioned at the whip entity.
        // Draw the connecting line only if the owner still exists.
        if (player != null) {
            // Save the current drawing position so our temporary changes can be undone afterward.
            poseStack.pushPose();

            //starts at the entity's hitbox centre
            // offset so the tip is centre
            Vec3 tipLocal = Vec3.ZERO;
            poseStack.translate(-0.5, 0.5, -0.5);

            // Convert the player's hand position from world coordinates into
            // coordinates relative to the whip entity.
            Vec3 playerHandLocal = player.getRopeHoldPosition(partialTick)
                    .subtract(entity.position());

            // Ask for a buffer that interprets each pair of vertices as a line.
            VertexConsumer line = buffer.getBuffer(RenderType.lines());

            // Submit the first endpoint: the whip tip.
            addLineVertex(line, poseStack, tipLocal);

            // Submit the second endpoint: the player's hand position.
            // Together, these two vertices describe one line segment.
            addLineVertex(line, poseStack, playerHandLocal);

            poseStack.popPose();
        }

        // Draw the blood block centered on the whip entity as a temporary
        // visual marker for the tip.
        // Save the drawing position before moving and resizing the marker.
        poseStack.pushPose();
        // Shift the cube so scaling it makes it centered on the entity.
        poseStack.translate(-0.5, 0, -0.5);
        // Shrink the full block model to half size for the whip tip marker.
        poseStack.scale(1F, 1F, 1F);


        blockRenderer.renderSingleBlock(
                ModBlocks.BLOOD_BLOCK.get().defaultBlockState(),
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        // Restore the drawing position so the marker's shift and scale do not affect later rendering.
        poseStack.popPose();

        // Let the base renderer perform any standard entity-renderer work.
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    /**
     * Adds one vertex to the line buffer.
     * A vertex is one 3D point. A line needs two vertices, so this helper
     * is called once for each endpoint.
     */
    private void addLineVertex(VertexConsumer line, PoseStack poseStack, Vec3 position) {
        line.vertex(
                        poseStack.last().pose(),
                        (float) position.x,
                        (float) position.y,
                        (float) position.z
                )
                .color(120, 0, 0, 255) // dark red, fully opaque
                .normal(0.0F, 1.0F, 0.0F) // placeholder normal for the line vertex
                .endVertex(); // finish submitting this vertex
    }

    @Override
    public ResourceLocation getTextureLocation(WhipEntity entity) {
        // The entity itself is drawn with the block renderer above,
        // so this texture is not used for that marker.
        return ResourceLocation.withDefaultNamespace("textures/block/blood_block.png");
    }
}
