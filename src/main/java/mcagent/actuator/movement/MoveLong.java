package mcagent.actuator.movement;

import mcagent.ControllerStatus;
import mcagent.Debugger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.*;

/**
 * Created by Chad on 5/25/2015.
 */
public class MoveLong extends Move {

    private BlockPos from,to;
    public MoveLong(double toX, double toY, double toZ) {
        super(toX, toY, toZ);
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        from = getPosition();
        to = new BlockPos(toX, toY, toZ);
        status = ControllerStatus.WAITING;
    }

    private LinkedList<Target> path;
    private MoveShort current;
    private Debugger debug = new Debugger(this);
    int stage = 1;
    @Override
    public void move() {
        if(path == null) {
            status = ControllerStatus.FAILURE;
        }
        if(path.isEmpty() && current.getStatus() == ControllerStatus.FINISHED && stage == 3) {
            status = ControllerStatus.FINISHED;
        }
        else if(current.getStatus() == ControllerStatus.FINISHED && path.isEmpty()) {
            stage = 3;
            current = new MoveShort(toX,toY,toZ);
            current.calculate();
        }
        else if((current == null || current.getStatus() == ControllerStatus.FINISHED) && !path.isEmpty()) {
            stage = 2;
            current = new MoveShort(path.poll().getBlock().add(0,0,0));
            current.calculate();
        }
        else if(current != null && current.getStatus() == ControllerStatus.WAITING) {
            current.move();
        }
        else {
            //in case it messes up in-between, need to recover
            status = ControllerStatus.FAILURE;
        }
    }

    @Override
    public boolean calculate() {
        WorldGrid wg = WorldGrid.getInstance();
        LinkedList<Target> fromPool = new LinkedList<Target>(wg.getNearestTargets(from, 10));
        LinkedList<Target> toPool = new LinkedList<Target>(wg.getNearestTargets(to, 10));
        Target t1 = null;
        Target t2temp = null;
        while(!fromPool.isEmpty() && !MoveShort.pathExists(from,(t1 = fromPool.poll()).getBlock()));
        while(!toPool.isEmpty() && !MoveShort.pathExists(to,(t2temp = toPool.poll()).getBlock()));
        final Target t2 = t2temp;
        if(t1 == null) {
            debug.info("FAILURE: Unable to find acceptable starting point.");
            status = ControllerStatus.FAILURE;
            return false;
        }

        class Tuple implements Comparable<Tuple> {
            double h; LinkedList<Target> path;
            public Tuple(LinkedList<Target> path) {
                this.path = path;
                Target t = path.getLast();
                this.h = Math.abs(t2.getX()-t.getX()) + Math.abs(t2.getY()-t.getY()) + Math.abs(t2.getZ()-t.getZ());
            }
            @Override
            public int compareTo(Tuple o) {
                return this.h<o.h ? -1:1;
            }
        }

        PriorityQueue<Tuple> pool = new PriorityQueue<Tuple>();
        HashSet<Target> hist = new HashSet<Target>();
        LinkedList<Target> path = new LinkedList<Target>(); path.add(t1);
        pool.add(new Tuple(path));

Loop1:  while(true) {
            if(pool.isEmpty()) {
                debug.errorf("No valid target path found to target %s with %d neighbors.", t2.getBlock(), t2.getNeighbors().size());
                status = ControllerStatus.FAILURE;
                return false;
            }
            Tuple t = pool.poll();
            //System.out.format("Examining target %s, dist %8.4f\n", t.path.getLast(), t.h);
            for(Target n: t.path.getLast().getNeighbors()) {
                if(hist.contains(n)) continue;
                if(n == t2) {
                    path = t.path;
                    path.add(n);
                    break Loop1;
                }
                else {
                    LinkedList<Target> npath = new LinkedList<Target>(t.path);
                    npath.add(n);
                    hist.add(n);
                    Tuple tn = new Tuple(npath);
                    pool.add(tn);
                }
            }
        }
        this.path = path;
        current = new MoveShort(path.poll().getBlock());
        current.calculate();
        return true;
    }

    @Override
    public Vec3 getCurrentGoal() {
        //Target t = path.getFirst();
        //return new Vec3(t.getX(), t.getY(), t.getZ());
        if(current == null) return null;
        return current.getCurrentGoal();
    }

    @Override
    public String toString() {
        return String.format("MoveLong{f=(%d,%d,%d), t=(%d,%d,%d)}", from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }
}
