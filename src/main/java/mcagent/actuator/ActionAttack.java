package mcagent.actuator;

import mcagent.ControllerStatus;

/**
 * Created by Chad on 5/27/2015.
 */
public class ActionAttack extends PlayerControllerAction {

    protected double atX,atY,atZ;
    protected ActionMoveLook mlAction;

    public ActionAttack(double atX, double atY, double atZ) {
        this.atX = atX;
        this.atY = atY;
        this.atZ = atZ;
        mlAction = new ActionMoveLook(atX,atY,atZ);
        if(mlAction.getStatus() == ControllerStatus.FAILURE) {
            this.status = ControllerStatus.FAILURE;
        }
        else {
            this.status = ControllerStatus.BUSY;
        }
    }

    private int stage = 0;
    @Override
    protected void performAction() {
        if(stage == 0) {
            mlAction.act();
            if(mlAction.getStatus() == ControllerStatus.FINISHED) stage++;
        }
        else if (stage == 1) {
            //click
            status = ControllerStatus.FINISHED;
            stage++;
        }
    }
}