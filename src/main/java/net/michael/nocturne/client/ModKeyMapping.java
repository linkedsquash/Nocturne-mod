package net.michael.nocturne.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class ModKeyMapping {

    public static final KeyMapping SWEEP_KEY = new KeyMapping(
            "key.nocturne.sweep_key",
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            "key.categories.nocturne"
    );
        @SubscribeEvent
        public static void register(RegisterKeyMappingsEvent event) {
            event.register(SWEEP_KEY);
        }

}

