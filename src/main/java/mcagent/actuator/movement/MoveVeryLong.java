package mcagent.actuator.movement;

import mcagent.ControllerStatus;
import mcagent.util.WorldTools;
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
        if(current == null || current.getStatus() == ControllerStatus.FINISHED) {
            System.out.println("Finding next long-distance target...");
            current = findNextAvailableTarget();
            System.out.println("Finished search.");
            if(current == null) this.status = ControllerStatus.FAILURE;
        }
        else if(current.getStatus() == ControllerStatus.FAILURE) {
            status = ControllerStatus.FAILURE;
        }
        else if(current.status == ControllerStatus.WAITING) {
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
        List<Target> targets = null;
        if(WorldTools.distance(p.getPositionVector(), new Vec3(toX,toY,toZ)) < WorldGrid.SURFACE_GRID_SIZE) {
            targets = wg.getNearestTargets(toX, toY, toZ, 10);
        } else {
            targets = wg.getNearestTargets(toX, w.getHeight(), toZ, 10);
        }
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
        if(current == null) return null;
        return current.getCurrentGoal();
    }
}
