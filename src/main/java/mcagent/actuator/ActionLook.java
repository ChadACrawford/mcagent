package mcagent.actuator;

import net.minecraft.client.entity.EntityPlayerSP;

/**
 * Created by Chad on 5/25/2015.
 */
public class ActionLook extends PlayerControllerAction {
    public static final double ACCEL = 5.0;
    public static final double MAX_SPEED = 5.0;

    private double lookX, lookY, lookZ;

    public ActionLook(PlayerController pc, double lookX, double lookY, double lookZ) {
        super(pc);
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
    }

    @Override
    public void performAction() {
    }

}
