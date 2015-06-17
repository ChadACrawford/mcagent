package autoplayer;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import mcagent.util.WorldTools;

import java.util.*;

/**
 * Created by Chad on 3/30/2015.
 * Finds a path between two positions using a greedy tree search algorithm.
 */
public class Path implements ControlModule {
    PlayerController pc;
    World w;

    Status status = Status.WAITING;
    LinkedList<BlockPos> path;

    public Path(PlayerController pc, World w, LinkedList<BlockPos> path) {
        this.pc = pc;
        this.w = w;
        this.path = path;
    }

    public static Path bestPath() {
        return null;
    }

    public static Path greedyPath(PlayerController pc, World w, BlockPos from, final BlockPos to) {
        class Tuple implements Comparable<Tuple> {
            LinkedList<BlockPos> path;
            double f,h;
            public Tuple(LinkedList<BlockPos> path, double f) {
                this.path = path;
                this.f = f;
                BlockPos b = path.getLast();
                this.h = to.distanceSqToCenter(b.getX()+0.5,b.getY()+0.5,b.getZ()+0.5);
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
                return null;
            }
            Tuple t = Collections.min(items);
            //System.out.format("T: %5d | N: %10d | t.f: %8.2f | t.h: %8.2f | b: %s | g: %s\n", T, items.size(), t.f, t.h, t.path.getLast(), to);
            items.remove(t);
            BlockPos b1 = t.path.getLast();
            if(b1.equals(to)) {
                return new Path(pc, w, t.path);
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
    public Status getStatus() {
        return status;
    }

    public void drawPath() {
        for(BlockPos p: path) w.setBlockState(p, Block.getStateById(138));
    }

    private enum Action { WAITING, MOVING };
    private Action action = Action.WAITING;
    private BlockPos to;
    @Override
    public void act() {
        status = Status.ACTIVE;
        if(path.isEmpty()) {
            status = Status.FINISHED;
            return;
        }
        BlockPos ppos = pc.p.getPosition().add(0,-1,0);
        switch(action) {
            case WAITING:
                pc.setDelay(10);
                pc.stopStrafe();
                pc.stopWalk();
                to = null;
                for(BlockPos p: path) {
                    if (WorldTools.isValidPath(w, ppos, p) || to == null) {
                        to = p;
                        action = Action.MOVING;
                    }
                    else break;
                }
                if(action != action.MOVING) status = Status.FAILED;
                System.out.format("from: %s | to: %s | g: %s\n", ppos, to, path.getLast());
                w.setBlockState(to, Block.getStateById(138));
                //for(BlockPos p: path) System.out.println(p);
                break;
            case MOVING:
                Vec3 loc = new Vec3(to.getX()+0.5,to.getY()+1,to.getZ()+0.5);
                if(pc.p.getPositionVector().distanceTo(loc) < 0.5) {
                    action = Action.WAITING;
                    while(!path.removeFirst().equals(to));
                }
                /*if(t-Minecraft.getSystemTime()<0) {
                    t = Minecraft.getSystemTime()+1000;
                    System.out.println(pc.p.getPositionVector().distanceTo(new Vec3(to.getX()+0.5,to.getY()+1,to.getZ()+0.5)));
                }//*/
                //sporadically recheck nearest path
                //if(Math.random()<0.1) action = Action.WAITING;
                pc.directMove(loc.xCoord, loc.yCoord, loc.zCoord);
                pc.lookAt(loc.xCoord, loc.yCoord + 0.12, loc.zCoord);
                break;
        }
    }
    double t = 0;
}
