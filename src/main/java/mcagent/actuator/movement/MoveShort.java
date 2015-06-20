package mcagent.actuator.movement;

import mcagent.ControllerStatus;
import mcagent.Debugger;
import mcagent.actuator.PlayerController;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import mcagent.util.WorldTools;

import java.util.*;

/**
 * Created by Chad on 5/25/2015.
 */
public class MoveShort extends Move {
    LinkedList<BlockPos> path;

    BlockPos to = null;
    BlockPos from = null;

    public MoveShort(double toX, double toY, double toZ) {
        super(toX, toY, toZ);
        EntityPlayerSP player = PlayerController.getInstance().getPlayer();
        this.from = new BlockPos(player.getPosition()).add(0,-1,0);
        this.to = new BlockPos(toX,toY,toZ);
    }
    public MoveShort(BlockPos from, BlockPos to) {
        super(to.getX(), to.getY(), to.getZ());
        this.from = new BlockPos(from);
        this.to = new BlockPos(to);
    }
    public MoveShort(BlockPos to) {
        super(to.getX(), to.getY(), to.getZ());
        EntityPlayerSP player = PlayerController.getInstance().getPlayer();
        this.from = new BlockPos(player.getPosition()).add(0,-1,0);
        this.to = new BlockPos(to);
    }

    BlockPos c = null;
    @Override
    public void move() {
        Debugger debug = Debugger.getInstance();
        PlayerController pc = PlayerController.getInstance();
        EntityPlayerSP player = pc.getPlayer();
        World w = pc.getWorld();
        if(path.isEmpty()) {
            if(player.getPositionVector().distanceTo(new Vec3(toX,toY+1,toZ)) < 0.5) {
                debug.reset(this);
                status = ControllerStatus.FINISHED;
                System.out.println("Finished!");
                return;
            }
            else {
                moveTo(toX,toY,toZ);
            }
        }
        else {
            BlockPos ppos = player.getPosition();
            if(c == null) c = path.getFirst();
            Vec3 loc = new Vec3(c.getX() + 0.5, c.getY() + 1, c.getZ() + 0.5);
            System.out.println(player.getPositionVector().distanceTo(new Vec3(loc.xCoord, loc.yCoord+1, loc.zCoord)));
            if (player.getPositionVector().distanceTo(new Vec3(loc.xCoord, loc.yCoord+1, loc.zCoord)) < 0.5) {
                while (!path.removeFirst().equals(c));
                setDelay(10);
                //pc.stopMoving();
                //to = null;
                for (BlockPos p : path) {
                    if (WorldTools.isValidPath(w, ppos, p)) {
                        c = p;
                    } else break;
                }
                System.out.format("from: %s | to: %s | g: %s\n", ppos, c, path.getLast());
                debug.debugBlock(this, c, Block.getStateById(138));
                loc = new Vec3(c.getX() + 0.5, c.getY() + 1, c.getZ() + 0.5);
            }
            //Vec3 loc = new Vec3(to.getX() + 0.5, to.getY() + 1, to.getZ() + 0.5);
            moveTo(loc.xCoord, loc.yCoord, loc.zCoord);
        }
    }

    private void moveTo(double x, double y, double z) {
        PlayerController pc = PlayerController.getInstance();
        EntityPlayerSP p = pc.getPlayer();
        World w = pc.getWorld();
        Vec3 o = p.getLookVec();
        double dx = x-p.posX, dz = z-p.posZ;

        double angle = Math.atan2(dx, -dz)/(2*Math.PI)*360+180 - pc.getYaw();
        if(Math.abs(angle) > 180) {
            angle /= -2;
        }
        angle += 180;

        //System.out.format("Angle: %9.6f\n", angle);
        if(angle > 100 && angle < 260) pc.forward();
        else if(angle > 5 && angle < 85 || angle > 275 && angle < 355) pc.back();
//
        if(angle > 10 && angle < 160) pc.left();
        else if(angle > 180 && angle < 350) pc.right();
        if(p.isInWater() || WorldTools.isBlocked(w, p.getPosition(), new BlockPos(x,y,z), 1)) pc.jump();
    }

    private long delay;
    private boolean isDelayed() {
        return (Minecraft.getMinecraft().getSystemTime() < delay);
    }
    private void setDelay(long ticks) {
        delay = Minecraft.getMinecraft().getSystemTime()+ticks;
    }

    public boolean calculate() {
        //PlayerController pc = PlayerController.getInstance();
        //EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        World w = Minecraft.getMinecraft().theWorld;
        class Tuple implements Comparable<Tuple> {
            LinkedList<BlockPos> path;
            double f,h;
            public Tuple(LinkedList<BlockPos> path, double f) {
                this.path = path;
                this.f = f;
                BlockPos b = path.getLast();
                this.h = Math.abs(b.getX()-toX)+Math.abs(b.getY()-toY)+Math.abs(b.getZ()-toZ);
            }
            @Override
            public boolean equals(Object o) {
                if(o instanceof Tuple)
                    return this.path.getLast().equals(((Tuple)o).path.getLast());
                return false;
            }
            @Override
            public int compareTo(Tuple o) {
                return (this.h < o.h ? -1:1);
            }

            @Override
            public int hashCode() {
                BlockPos b = path.getLast();
                return b.getX() + (int)(WorldGrid.SURFACE_GRID_SIZE * b.getY()) + (int)(WorldGrid.SURFACE_GRID_SIZE*WorldGrid.SURFACE_GRID_SIZE*b.getZ());
            }
        }
        PriorityQueue<Tuple> pool = new PriorityQueue<Tuple>();
        HashSet<BlockPos> hist = new HashSet<BlockPos>();
        LinkedList<BlockPos> q = new LinkedList<BlockPos>(); q.add(from);
        pool.add(new Tuple(q, 0));

        int[][] search = new int[][] {
                new int[] {1,0},
                new int[] {0,1},
                new int[] {-1,0},
                new int[] {0,-1},
        };

        int T = 0;

Loop1:
        while(true) {
            T++;
            if(pool.isEmpty() || T > WorldGrid.MAX_DISTANCE*WorldGrid.MAX_DISTANCE/4) {
                return false;
            }
            Tuple t = pool.poll();
            BlockPos b = t.path.getLast();
            //System.out.format("T: %5d | N: %10d | t.f: %8.2f | t.h: %8.2f | b: %s | g: %s\n", T, pool.size(), t.f, t.h, b, to);
            for(int[] s: search) {
                BlockPos b2 = b.add(s[0],0,s[1]);
                if(WorldTools.isSolid(w, b2)) {
                    b2 = WorldTools.findAboveSurfaceBlock(w, b2);
                    if(b2.getY()-b.getY()>1 || !WorldTools.open(w, b, 3) || !WorldTools.open(w, b2, 2)) continue;
                }
                else {
                    b2 = WorldTools.findBelowSurfaceBlock(w, b2);
                    int d = b.getY()-b2.getY();
                    if(d > 3 || !WorldTools.open(w,b,2) || !WorldTools.open(w,b2,d+2)) continue;
                }
                if(hist.contains(b2)) continue;
                if(b2.equals(to)) {
                    q = new LinkedList<BlockPos>(t.path);
                    break Loop1;
                }
                LinkedList<BlockPos> npath = new LinkedList<BlockPos>(t.path);
                npath.add(b2);
                hist.add(b2);
                pool.add(new Tuple(npath, t.f + 0)); //if i want to use A*, will have to also add the distance between b,b2
            }
        }
        path = q;
        return true;
    }

    public static boolean pathExists(BlockPos from, BlockPos to) {
        return new MoveShort(from, to).calculate();
    }

    @Override
    public Vec3 getCurrentGoal() {
        Vec3 loc = new Vec3(to.getX()+0.5,to.getY()+1,to.getZ()+0.5);
        return loc;
    }
}
