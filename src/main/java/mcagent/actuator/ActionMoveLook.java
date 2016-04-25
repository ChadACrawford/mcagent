package mcagent.actuator;

import mcagent.ControllerStatus;
import net.minecraft.util.Vec3;

/**
 * Created by Chad on 5/25/2015.
 */
public class ActionMoveLook extends PlayerControllerAction {
    private ActionLook lookAction;
    protected double goalX, goalY, goalZ;

    public ActionMoveLook(PlayerController pc, double goalX, double goalY, double goalZ) {
        super(pc);
        this.goalX = goalX;
        this.goalY = goalY;
        this.goalZ = goalZ;
    }


    Vec3 goal = null;
    @Override
    protected void performAction() {
    }

    public void update(double x, double y, double z) {
    }
}
