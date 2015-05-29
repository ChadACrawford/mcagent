package mcagent.actuator.movement;

import mcagent.Controller;
import mcagent.ControllerStatus;
import mcagent.actuator.ActionMove;
import net.minecraft.util.Vec3;

/**
 * Created by Chad on 5/25/2015.
 */
public abstract class Move implements Controller {
    protected double toX, toY, toZ;
    protected ActionMove action;
    protected ControllerStatus status = ControllerStatus.WAITING;

    public Move(double toX, double toY, double toZ) {
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
    }

    @Override
    public void act() {
        move();
    }
    @Override
    public ControllerStatus getStatus() {
        return status;
    }

    public abstract void move();
    public abstract boolean calculate();

    public abstract Vec3 getCurrentGoal();
}
