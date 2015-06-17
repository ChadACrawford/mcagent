package mcagent.actuator.movement;

import mcagent.ControllerStatus;
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
        from = new BlockPos(p.getPosition());
        to = new BlockPos(toX,toY,toZ);
        status = ControllerStatus.BUSY;
    }

    private LinkedList<Target> path;
    private MoveShort current;
    @Override
    public void move() {
        if(path == null) return;
        if(path.isEmpty()) {
            status = ControllerStatus.FINISHED;
        }
        else if(current == null || current.getStatus() == ControllerStatus.FINISHED) {
            current = new MoveShort(path.getFirst().getBlock());
            current.calculate();
        }
        else if(current.status == ControllerStatus.BUSY || current.status == ControllerStatus.WAITING) {
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
        Target t1 = wg.getNearestTarget(from);
        final Target t2 = wg.getNearestTarget(to);
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
                //System.out.println("No valid target path found.");
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
        current = new MoveShort(path.getFirst().getBlock());
        current.calculate();
        return true;
    }

    @Override
    public Vec3 getCurrentGoal() {
        //Target t = path.getFirst();
        //return new Vec3(t.getX(), t.getY(), t.getZ());
        return current.getCurrentGoal();
    }
}
