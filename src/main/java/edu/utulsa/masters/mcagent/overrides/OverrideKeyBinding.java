package edu.utulsa.masters.mcagent.overrides;

import edu.utulsa.masters.mcagent.actuator.PlayerController;
import net.minecraft.client.Minecraft;
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
        if(PlayerController.doOverrideKeys) {
            return pressed;
        } else {
            return super.isKeyDown();
        }
    }

    @Override
    public boolean isPressed() {
        if(PlayerController.doOverrideKeys) {
            return pressed;
        } else {
            return super.isPressed();
        }
    }

    public void press() {
        pressed = true;
    }
    public void unpress() {
        pressed = false;
    }

    int delay = 0;
    public void pressFor(int ticks) {
        press();
        //this.timePressed = Minecraft.getSystemTime() + ticks;
        //this.timed = true;
    }

    public void tick() {
        if(delay > 0) {
            delay--;
            if(delay == 0) unpress();
        }
    }
}
