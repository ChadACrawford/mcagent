package mcagent.actuator.movement;

import mcagent.ControllerStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Chad on 5/25/2015.
 */
public class MoveVeryLong extends Move {
    public MoveVeryLong(double toX, double toY, double toZ) {
        super(toX, toY, toZ);
    }

    MoveLong current;
    @Override
    public void move() {
        if(current == null || current.getStatus() == ControllerStatus.FAILURE || current.getStatus() == ControllerStatus.FINISHED) {
            current = findNextAvailableTarget();
            if(current == null) this.status = ControllerStatus.FAILURE;
        }
        else if(current.status == ControllerStatus.BUSY) {
            current.move();
        }
    }

    @Override
    public boolean calculate() {
        return true;
    }

    private MoveLong findNextAvailableTarget() {
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        World w = Minecraft.getMinecraft().theWorld;
        WorldGrid wg = WorldGrid.getInstance();
        wg.explore(p.getPosition());
        List<Target> targets = wg.getNearestTargets(toX, w.getHeight(), toZ, 10);
        for(Target t: targets) {
            MoveLong m = new MoveLong(t.getX(), t.getY(), t.getZ());
            if(m.calculate()) {
                return m;
            }
        }
        return null;
    }

    @Override
    public Vec3 getCurrentGoal() {
        return current.getCurrentGoal();
    }
}
