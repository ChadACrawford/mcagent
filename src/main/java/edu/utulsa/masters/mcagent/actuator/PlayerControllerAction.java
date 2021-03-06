package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.Controller;
import edu.utulsa.masters.mcagent.ControllerStatus;

/**
 * Created by Chad on 5/24/2015.
 */
public abstract class PlayerControllerAction implements Controller {
    protected ControllerStatus status;
    protected PlayerController pc;
    private int ticks = 0;
    protected PlayerControllerAction(PlayerController pc) {
        this.pc = pc;
        this.status = ControllerStatus.WAITING;
    }

    public void act() {
        if(status == ControllerStatus.FAILURE) return;
        if(status == ControllerStatus.WAITING) status = ControllerStatus.BUSY;
        try {
            performAction();
            ticks++;
        } catch(Exception e) {
            e.printStackTrace();
            status = ControllerStatus.FAILURE;
        }
    }

    protected abstract void performAction();

    public boolean isFinished() {
        return status == ControllerStatus.FINISHED || status == ControllerStatus.FAILURE;
    }

    public ControllerStatus getStatus() {
        return status;
    }

    public int getTicks() {
        return ticks;
    }
}
