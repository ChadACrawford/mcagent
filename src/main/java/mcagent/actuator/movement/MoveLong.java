package mcagent.actuator.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Chad on 5/25/2015.
 */
public class MoveLong extends Move {

    private BlockPos from,to;
    private LinkedList<Target> path;
    public MoveLong(double toX, double toY, double toZ) {
        super(toX, toY, toZ);
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        from = new BlockPos(p.getPosition());
        to = new BlockPos(toX,toY,toZ);
    }

    @Override
    public void move() {

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
        LinkedList<Target> path = new LinkedList<Target>(); path.add(t1);
        pool.add(new Tuple(path));

Loop1:  while(true) {
            if(pool.isEmpty()) return false;
            Tuple t = pool.poll();
            for(Target n: t.path.getLast().getNeighbors()) {
                if(n == t2) {
                    path = t.path;
                    path.add(n);
                    break Loop1;
                }
            }
        }
        return true;
    }

    @Override
    public Vec3 getCurrentGoal() {
        return null;
    }
}
