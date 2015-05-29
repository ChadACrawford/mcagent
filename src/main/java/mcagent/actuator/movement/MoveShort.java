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
import tools.WorldTools;

import java.util.*;

/**
 * Created by Chad on 5/25/2015.
 */
public class MoveShort extends Move {
    LinkedList<BlockPos> path;

    BlockPos to = null;

    public MoveShort(double toX, double toY, double toZ) {
        super(toX, toY, toZ);
    }

    @Override
    public void move() {
        Debugger debug = Debugger.getInstance();
        PlayerController pc = PlayerController.getInstance();
        EntityPlayerSP player = pc.getPlayer();
        World w = pc.getWorld();
        if(path.isEmpty()) {
            debug.reset(this);
            status = ControllerStatus.FINISHED;
            return;
        }
        BlockPos ppos = player.getPosition();
        if(player.getPositionVector().distanceTo(new Vec3(toX, toY, toZ)) < 0.5) {
            while(!path.removeFirst().equals(to));
            setDelay(10);
            //pc.stopMoving();
            //to = null;
            for(BlockPos p: path) {
                if (WorldTools.isValidPath(w, ppos, p) || to == null) {
                    to = p;
                }
                else break;
            }
            System.out.format("from: %s | to: %s | g: %s\n", ppos, to, path.getLast());
            debug.debugBlock(this, to, Block.getStateById(138));
        }
        Vec3 loc = new Vec3(to.getX()+0.5,to.getY()+1,to.getZ()+0.5);
        moveTo(loc.xCoord, loc.yCoord, loc.zCoord);
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

    //PlayerController pc, World w, BlockPos from, final BlockPos to
    @Override
    public boolean calculate() {
        PlayerController pc = PlayerController.getInstance();
        EntityPlayerSP player = pc.getPlayer();
        World w = pc.getWorld();

        BlockPos from = new BlockPos(player.getPosition());
        class Tuple implements Comparable<Tuple> {
            LinkedList<BlockPos> path;
            double f,h;
            public Tuple(LinkedList<BlockPos> path, double f) {
                this.path = path;
                this.f = f;
                BlockPos b = path.getLast();
                this.h = b.distanceSqToCenter(toX,toY,toZ);
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
            public Tuple add(BlockPos p, double c) {
                LinkedList<BlockPos> npath = new LinkedList<BlockPos>(path);
                npath.add(p);
                return new Tuple(npath, f+c);
            }
        }
        List<Tuple> items = new ArrayList<Tuple>();
        List<BlockPos> hist = new ArrayList<BlockPos>();
        LinkedList<BlockPos> q = new LinkedList<BlockPos>(); q.add(from);
        items.add(new Tuple(q, 0));

        int[][] search = new int[][] {
                new int[] {1,0},
                new int[] {0,1},
                new int[] {-1,0},
                new int[] {0,-1},
        };
        int T = 0;
        while(true) {
            T++;
            if(items.isEmpty() || T > 5000) {
                //search[24][2]++;
                return false;
            }
            Tuple t = Collections.min(items);
            //System.out.format("T: %5d | N: %10d | t.f: %8.2f | t.h: %8.2f | b: %s | g: %s\n", T, items.size(), t.f, t.h, t.path.getLast(), to);
            items.remove(t);
            BlockPos b1 = t.path.getLast();
            if(b1.distanceSqToCenter(toX, toY, toZ) < 0.5) {
                path = t.path;
                path.pollLast();
                return true;
            }

            for(int[] s: search) {
                int toX = b1.getX()+s[0], toZ = b1.getZ()+s[1];
                BlockPos b2 = WorldTools.getAccessibleBlock(w, b1, toX, toZ);
                if(b2 == null) break;
                Tuple t2 = t.add(b2, 1+Math.abs(b1.getY()-b2.getY())/2.);
                if(!items.contains(t2) && !hist.contains(b2)) {
                    hist.add(b2);
                    items.add(t2);
                }
                else if(items.contains(t2)) {
                    Tuple t3 = items.get(items.indexOf(t2));
                    if(t2.f < t3.f) {
                        items.remove(t3);
                        items.add(t2);
                    }
                }
            }
        }
    }

    @Override
    public Vec3 getCurrentGoal() {
        Vec3 loc = new Vec3(to.getX()+0.5,to.getY()+1,to.getZ()+0.5);
        return loc;
    }
}
