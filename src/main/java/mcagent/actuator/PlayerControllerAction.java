package mcagent.actuator;

import mcagent.Controller;
import mcagent.ControllerStatus;

/**
 * Created by Chad on 5/24/2015.
 */
public abstract class PlayerControllerAction implements Controller {
    protected ControllerStatus status;
    private int ticks = 0;

    public void act() {
        performAction();
        ticks++;
    }

    protected abstract void performAction();

    public ControllerStatus getStatus() {
        return status;
    }

    public int getTicks() {
        return ticks;
    }
}
