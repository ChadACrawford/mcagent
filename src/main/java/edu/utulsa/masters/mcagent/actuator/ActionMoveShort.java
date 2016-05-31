package edu.utulsa.masters.mcagent.actuator;

import edu.utulsa.masters.mcagent.ControllerStatus;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.DateUtils;

import javax.vecmath.Vector2d;

/**
 * Internal class. Should be used for direct movement, since I want
 */
public class ActionMoveShort extends PlayerControllerAction {
    public enum Speed {
        WALK, SPRINT, SNEAK
    }
    Speed speed = Speed.WALK;
    double x, y, z;
    public ActionMoveShort(PlayerController pc) {
        super(pc);
    }

    protected static boolean isValid(Vec3 start, Vec3 stop) {
        return true;
    }

    @Override
    protected void performAction() {
        if(pc.dist(x,y,z) < 0.1) {
            status = ControllerStatus.FINISHED;
            return;
        }


    }

    private final double HALF_DIST = 0.70710678;
    protected void goStraight() {
        DirectionInfo info = getDirection();
        pc.look(x, y, z);

        pc.unpressAll();
        if(info.projF > HALF_DIST) {
            pc.forward();
            if(Math.abs(info.projB) > info.projF) {
                if(info.projL > 0.1) {
                    pc.left();
                }
                else if(info.projL < -0.1) {
                    pc.right();
                }
            }
        }
    }

    class DirectionInfo {
        double projF, projL, projB;
    }
    DirectionInfo getDirection() {
        Vec3 playerPos = pc.getPlayer().getPositionVector();
        Vec3 o = pc.getPlayer().getLookVec();

        Vector2d toVec = new Vector2d(x - playerPos.xCoord, z - playerPos.zCoord);
        Vector2d forwardVec = new Vector2d(o.xCoord, o.zCoord);
        Vector2d leftVec = new Vector2d(o.zCoord, -o.xCoord);
        Vector2d bothVec = new Vector2d(forwardVec.getX() + leftVec.getX(), forwardVec.getY() + leftVec.getY());
        toVec.normalize();
        forwardVec.normalize();
        leftVec.normalize();
        bothVec.normalize();

        DirectionInfo info = new DirectionInfo();

        info.projF = forwardVec.dot(toVec);
        info.projL = leftVec.dot(toVec);
        info.projB = bothVec.dot(toVec);

        return info;
    }
}
