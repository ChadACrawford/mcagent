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
        super();
        this.goalX = goalX;
        this.goalY = goalY;
        this.goalZ = goalZ;
        moveAction = new ActionMove(goalX, goalY, goalZ);
        if(moveAction.getStatus() == ControllerStatus.FAILURE) {
            this.status = ControllerStatus.FAILURE;
        }
    }


    Vec3 goal = null;
    @Override
    protected void performAction() {
        if(moveAction.getStatus() == ControllerStatus.WAITING) moveAction.act();
        if(goal != moveAction.getCurrentGoal()) {
            goal = moveAction.getCurrentGoal();
            if(goal != null)
                lookAction = new ActionLook(goal.xCoord, goal.yCoord, goal.zCoord);
            else
                lookAction = null;
        }
        if(goal != null && lookAction != null && lookAction.getStatus() == ControllerStatus.WAITING)
            lookAction.act();

        if(moveAction.getStatus() == ControllerStatus.FINISHED)
            this.status = ControllerStatus.FINISHED;

        if(moveAction.getStatus() == ControllerStatus.FAILURE)
            this.status = ControllerStatus.FAILURE;
    }

    public void update(double x, double y, double z) {
    }
}
