package edu.utulsa.masters.mcagent.overrides;

import net.minecraft.util.MouseHelper;

/**
 * Created by chad on 4/26/16.
 */
public class OverrideMouseHelper extends MouseHelper {
    public static boolean override = true;
    public void grabMouseCursor() {
        if(!override) {
            super.grabMouseCursor();
        }
    }
    public void ungrabMouseCursor() {
        if(!override) {
            super.ungrabMouseCursor();
        }
    }
}
