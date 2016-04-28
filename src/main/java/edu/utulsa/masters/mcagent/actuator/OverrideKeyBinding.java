package edu.utulsa.masters.mcagent.actuator;

import net.minecraft.client.settings.KeyBinding;

/**
 * Created by Chad on 3/29/2015.
 */
public class OverrideKeyBinding extends KeyBinding {
    public OverrideKeyBinding(String description, int keyCode, String category) {
        super(description, keyCode, category);
    }

    boolean pressed = false;
    @Override
    public boolean isKeyDown() {
        return pressed;
    }

    @Override
    public boolean isPressed() {
        return pressed;
    }

    public void press() {
        pressed = true;
    }
    public void unpress() {
        pressed = false;
    }
}
