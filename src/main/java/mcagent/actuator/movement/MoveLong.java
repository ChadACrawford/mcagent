package mcagent.actuator.movement;

import net.minecraft.util.Vec3;

/**
 * Created by Chad on 5/25/2015.
 */
public class MoveLong extends Move {
    public MoveLong(double toX, double toY, double toZ) {
        super(toX, toY, toZ);
    }

    @Override
    public void move() {

    }

    @Override
    public boolean calculate() {
        return false;
    }

    @Override
    public Vec3 getCurrentGoal() {
        return null;
    }
}
