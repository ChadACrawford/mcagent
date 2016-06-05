package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.ControllerStatus;

/**
 * Created by Chad on 5/27/2015.
 */
public class ActionAttack extends PlayerControllerAction {

    protected double atX,atY,atZ;

    public ActionAttack(PlayerController pc, double atX, double atY, double atZ) {
        super(pc);
        this.atX = atX;
        this.atY = atY;
        this.atZ = atZ;
    }

    private int stage = 0;
    @Override
    protected void performAction() {
        if(stage == 0) {
        }
        else if (stage == 1) {
            //click
            pc.attack();
            stage++;
        }
        else if (stage == 2) {
            pc.unattack();
            status = ControllerStatus.FINISHED;
            stage++;
        }
    }
}
