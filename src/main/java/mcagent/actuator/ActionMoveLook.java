package mcagent.actuator;

import mcagent.ControllerStatus;
import net.minecraft.util.Vec3;

/**
 * Created by Chad on 5/25/2015.
 */
public class ActionMoveLook extends PlayerControllerAction {
    private ActionLook lookAction;
    private ActionMove moveAction;
    protected double goalX, goalY, goalZ;

    public ActionMoveLook(double goalX, double goalY, double goalZ) {
        this.goalX = goalX;
        this.goalY = goalY;
        this.goalZ = goalZ;
        moveAction = new ActionMove(goalX, goalY, goalZ);
        if(moveAction.getStatus() == ControllerStatus.FAILURE) this.status = ControllerStatus.FAILURE;
        else this.status = ControllerStatus.BUSY;
    }


    Vec3 goal = null;
    @Override
    protected void performAction() {
        if(moveAction.getStatus() == ControllerStatus.BUSY) moveAction.act();
        if(goal != moveAction.getCurrentGoal()) {
            goal = moveAction.getCurrentGoal();
            lookAction = new ActionLook(goal.xCoord, goal.yCoord, goal.zCoord);
        }
        lookAction.act();
        if(moveAction.getStatus() == ControllerStatus.FINISHED && lookAction.getStatus() == ControllerStatus.FINISHED) this.status = ControllerStatus.FINISHED;
    }

    public void update(double x, double y, double z) {
    }
}
