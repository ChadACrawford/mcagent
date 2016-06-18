package edu.utulsa.masters.mcagent.actuator.movement;

import edu.utulsa.masters.mcagent.ControllerStatus;
import edu.utulsa.masters.mcagent.GameInfo;
import edu.utulsa.masters.mcagent.actuator.PlayerController;
import edu.utulsa.masters.mcagent.util.WorldTools;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.lang3.time.DateUtils;

import javax.vecmath.Vector2d;
import java.util.LinkedList;

/**
 * Internal class. Should be used for direct movement, since I want
 */
public class MoveControl {
    public enum Speed {
        WALK, SPRINT, SNEAK
    }
    final PlayerController pc;
    Speed speed = Speed.WALK;
    double x, y, z;
    double lookX, lookY, lookZ;
    boolean needsJump;
    boolean needsFall;
    public MoveControl(PlayerController pc, Vec3 pos) {
        this.pc = pc;
        x = pos.xCoord;
        y = pos.yCoord;
        z = pos.zCoord;
    }

    protected static Vec3[] makeDaSquare(Vec3 a, Vec3 b) {
        //imma make a square k

        double dx = a.xCoord - b.xCoord,
                dz = a.zCoord - b.zCoord,
                d = Math.sqrt(dx*dx + dz*dz);

        dx = dx / d * 0.5;
        dz = dz / d * 0.5;

        return new Vec3[] {
                new Vec3(a.xCoord + dx, a.yCoord + 0.1, a.zCoord - dz),
                new Vec3(a.xCoord - dx, a.yCoord + 0.1, a.zCoord + dz),
                new Vec3(a.xCoord + dx, a.yCoord + 2., a.zCoord - dz),
                new Vec3(a.xCoord - dx, a.yCoord + 2., a.zCoord + dz)
        };
    }

    public double dist() {
        return pc.getPlayer().getPositionVector().distanceTo(new Vec3(x, y, z));
    }

    public boolean isValid() {
        Vec3 start = pc.getPlayer().getPositionVector(),
                stop = new Vec3(x,y,z);
        Vec3[] sq1 = makeDaSquare(start, stop);
        Vec3[] sq2 = makeDaSquare(stop, start);

        LinkedList<BlockPos> blocks = new LinkedList<BlockPos>();

        blocks.addAll(WorldTools.intersectingBlocks(sq1[0],sq2[1]));
        blocks.addAll(WorldTools.intersectingBlocks(sq1[1],sq2[0]));
        blocks.addAll(WorldTools.intersectingBlocks(sq1[2],sq2[3]));
        blocks.addAll(WorldTools.intersectingBlocks(sq1[3],sq2[2]));

        for(BlockPos block: blocks) {
            if(WorldTools.isSolid(pc.getWorld(), block)) return false;
        }

        return true;
    }

    double dX, dZ;

    boolean started = false;
    public void start() {
        Vec3 playerPos = pc.getPlayer().getPositionVector();
        dX = x - playerPos.xCoord;
        dZ = z - playerPos.zCoord;
        double d = Math.sqrt(dX*dX+dZ*dZ);
        lookX = x + dX / d * 0.3;
        lookY = y;
        lookZ = z + dX / d * 0.3;
        needsJump = y > playerPos.yCoord;
        needsFall = y < playerPos.yCoord;
    }

    public void run() {
        if(!started) {
            start();
            started = true;
        }
        pc.look(lookX, lookY, lookZ);
        goStraight();
        boolean jumped = checkJump();
        if(!jumped && pc.getPlayer().onGround && !needsFall) {
            checkFall();
        }
    }

    private int ticksFix = 0;
    private final double HALF_DIST = 0.70710678;
    protected void goStraight() {
        DirectionInfo info = getDirection(x, y, z);

        pc.stopMoving();
        if(info.projF >= HALF_DIST) {
            pc.forward();
            if(Math.abs(info.projB) > info.projF) {
                checkStrafe(info);
            }
        }

        if(pc.getCurrentVelocity() == 0) ticksFix = 5;
        if(ticksFix > 0) {
            checkStrafe(info);
            ticksFix--;
        }
//        if(info.projF > 0.9) {
//            pc.forward();
//        }
//        if(Math.abs(info.projL) > 0.9) {
//            if(info.projL > 0) {
//                pc.left();
//            }
//            else if(info.projL < 0) {
//                pc.right();
//            }
//        }
    }

    public boolean done() {
        Vec3 playerPos = pc.getPlayer().getPositionVector();

        double proj = dX * (playerPos.xCoord - x) + dZ * (playerPos.zCoord - z);

        //we've already passed the point
        //System.out.println(proj);

        return proj >= -0.3;
    }

    protected double getJumpDist() {
        return 0.31;
    }

    protected double forwardDir() {
        return PlayerController.keyUp.isPressed() ? 1 :
                PlayerController.keyDown.isPressed() ? -1 : 0;
    }

    protected double strafeDir() {
        return PlayerController.keyLeft.isPressed() ? 1 :
                PlayerController.keyRight.isPressed() ? -1 : 0;
    }

    protected Vector2d kbVec() {
        Vec3 o = pc.getPlayer().getLookVec();
        double f = forwardDir(), s = strafeDir();

        Vector2d v = new Vector2d(
                f * o.xCoord + s * o.zCoord,
                f * o.zCoord - s * o.xCoord
        );
        v.normalize();

        return v;
    }

    protected Vector2d getCurrentMoveVec(double m) {
        double dx = pc.dx,
                dz = pc.dz;
        if(pc.getCurrentVelocity() <= 1e-4) {
            Vector2d mv = kbVec();
            dx = mv.x;
            dz = mv.y;
        }
        double d = Math.sqrt(dx*dx + dz*dz);
        dx = (dx / d) * m;
        dz = (dz / d) * m;
        return new Vector2d(dx, dz);
    }

    protected Vector2d getLookVec(double m) {
        Vec3 lookVec = pc.getPlayer().getLookVec();
        double dx = lookVec.xCoord,
                dz = lookVec.zCoord;
        double d = Math.sqrt(dx*dx + dz*dz);
        dx = (dx / d) * m;
        dz = (dz / d) * m;
        return new Vector2d(dx, dz);
    }

    protected boolean checkJump() {
        if(!pc.getPlayer().onGround) return false;

        Vec3 pos = pc.getPlayer().getPositionVector();

        Vector2d mvVec = getLookVec(getJumpDist());
        double dx = mvVec.x,
                dz = mvVec.y;

        BlockPos b = new BlockPos(pos.addVector(dx, 0, dz));
        if(WorldTools.isSolid(pc.getWorld(), b) && b.equals(new BlockPos(x,y-1,z))) {
            DirectionInfo info = getDirection(b.getX() + 0.5, b.getY(), b.getZ());
            checkStrafe(info);
            pc.jump();
            return true;
        }
        return false;
    }

    protected boolean checkFall() {
        Vec3 pos = pc.getPlayer().getPositionVector();
        BlockPos b = pc.getPlayer().getPosition();

        Vector2d mvVec = getCurrentMoveVec(0.2);
        double dx = mvVec.x,
                dz = mvVec.y;

        System.out.format("dx %6.3f dz %6.3f\n", dx, dz);
        BlockPos b2 = new BlockPos(pos.xCoord + dx, pos.yCoord - 1, pos.zCoord + dz);
        if(!WorldTools.isSolid(pc.getWorld(), b2)) {
            System.out.println("AVOID FALLING IDIOT");
            //System.out.format("%6.3f %6.3f %6.3f\n", pos.xCoord, pos.yCoord, pos.zCoord);
            //moveTo(b.getX() + 0.5, b.getY(), b.getZ() + 0.5);
            pc.back();
            return true;
        }
        return false;
    }

    public void moveTo(double x, double y, double z) {
        DirectionInfo info = getDirection(x, y, z);

        pc.stopMoving();
        if(info.maxType == 1) {
            checkForward(info);
        }
        else if(info.maxType == -1) {
            checkStrafe(info);
        }
        else {
            checkForward(info);
            checkStrafe(info);
        }
    }

    void checkForward(DirectionInfo info) {
        if(info.projF > 0) pc.forward();
        else if(info.projF < 0) pc.back();
    }

    void checkStrafe(DirectionInfo info) {
        if(info.projL > 0) pc.left();
        else if(info.projL < 0) pc.right();
    }

    public void print() {
        System.out.format("%6.3f %6.3f %6.3f\n", x, y, z);
    }

    class DirectionInfo {
        double projF, projL, projB;
        int maxType;
    }
    DirectionInfo getDirection(double x, double y, double z) {
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

        if(Math.abs(info.projB) > Math.abs(info.projF) && Math.abs(info.projB) > Math.abs(info.projL)) {
            info.maxType = 0;
        }
        else if(Math.abs(info.projF) > Math.abs(info.projL)) {
            info.maxType = 1;
        }
        else {
            info.maxType = -1;
        }

        return info;
    }
}
