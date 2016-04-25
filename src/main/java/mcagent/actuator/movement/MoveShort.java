package mcagent.actuator.movement;

import mcagent.ControllerStatus;
import mcagent.DebugObject;
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
    LinkedList<Vec3> path;

    Vec3 to = null;
    Vec3 from = null;

    Debugger debug = new Debugger(this);
    public MoveShort(double toX, double toY, double toZ) {
        super(toX, toY, toZ);
        EntityPlayerSP player = pc.getPlayer();
        this.from = player.getPositionVector();
        this.to = new Vec3(toX,toY,toZ);
    }
    public MoveShort(Vec3 from, Vec3 to) {
        super(to.xCoord, to.yCoord, to.zCoord);
        this.from = new Vec3(from.xCoord, from.yCoord, from.zCoord);
        this.to = new Vec3(to.xCoord, to.yCoord, to.zCoord);
    }
    public MoveShort(Vec3 from, Vec3 to, boolean verbose) {
        super(to.xCoord, to.yCoord, to.zCoord + 0.5);
        this.from = new Vec3(from.xCoord, from.yCoord, from.zCoord);
        this.to = new Vec3(to.xCoord, to.yCoord, to.zCoord);
        this.debug.setVerbosity(verbose);
    }

    public MoveShort(BlockPos to) {
        super(to.getX(), to.getY(), to.getZ());
        World w = Minecraft.getMinecraft().theWorld;
        this.from = WorldTools.findBelowSurface(w, getPosition());
        this.to = new Vec3(to.getX() + 0.5, to.getY(), to.getZ() + 0.5);
        this.status = ControllerStatus.WAITING;
    }


    Vec3 c = null;
    int stage = 0;
    @Override
    public void move() {
//        Debugger debug = Debugger.getInstance();
//        PlayerController pc = PlayerController.getInstance();
//        EntityPlayerSP player = pc.getPlayer();
//        World w = pc.getWorld();
//            BlockPos ppos = player.getPosition();
//            if(c == null) c = path.getFirst();
//            Vec3 loc = new Vec3(c.getX() + 0.5, c.getY() + 1, c.getZ() + 0.5);
//            if(verbose) System.out.println(player.getPositionVector().distanceTo(new Vec3(loc.xCoord, loc.yCoord, loc.zCoord)));
//            if (player.getPositionVector().distanceTo(new Vec3(loc.xCoord, loc.yCoord, loc.zCoord)) < 0.5) {
//                while (!path.removeFirst().equals(c)) ;
//                setDelay(10);
//                //pc.stopMoving();
//                //to = null;
//                if (!path.isEmpty()) {
//                    c = path.getFirst();
//                    for (BlockPos p : path) {
//                        if (WorldTools.isValidPath(w, ppos, p)) {
//                            c = p;
//                        } else break;
//                    }
//                    //System.out.format("from: %s | to: %s | g: %s\n", ppos, c, path.getLast());
//                    debug.debugBlock(this, c, Block.getStateById(138));
//                    loc = new Vec3(c.getX() + 0.5, c.getY() + 1, c.getZ() + 0.5);
//                } else {
//                    if (player.getPositionVector().distanceTo(new Vec3(toX, toY + 1, toZ)) < 0.5) {
//                        debug.reset(this);
//                        status = ControllerStatus.FINISHED;
//                        if (verbose) System.out.println("Finished!");
//                        return;o
//                    } else {
//                        loc = new Vec3(toX, toY, toZ);
//                        //moveTo(toX,toY,toZ);
//                    }
//                }
//            }
//            //Vec3 loc = new Vec3(to.getX() + 0.5, to.getY() + 1, to.getZ() + 0.5);
//            moveTo(loc.xCoord, loc.yCoord, loc.zCoord);
//        }
        EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        World w = Minecraft.getMinecraft().theWorld;
        if(isDelayed()) {
            //do nothing
        }
        else if(status == ControllerStatus.FAILURE) {
            //do nothing
        }
        else if(c == null || stage == 0) {
            stage = 1;
            Vec3 bc = path.getFirst();
            c = new Vec3(bc.xCoord, bc.yCoord, bc.zCoord);
        }
        else if(p.getPositionVector().distanceTo(c) < 0.5) {
            //if(verbose) System.out.println(p.getPositionVector().distanceTo(c));
            if(!path.isEmpty())
                debug.format("from: %s | to: %s | g: %s\n", from, c, path.getLast());
            if(stage == 1) {
                BlockPos b1 = new BlockPos(c.xCoord, c.yCoord-1, c.zCoord);
                while (!path.removeFirst().equals(b1)) ;
                setDelay(500);
                if (!path.isEmpty()) {
                    stage = 1;
                    Vec3 bc = path.getFirst();
                    Vec3 plv = p.getPositionVector();
                    //get the nearest ground block
                    Vec3 plb = WorldTools.findBelowSurface(w, plv);
                    for (Vec3 b2 : path) {
                        if (WorldTools.isValidPath(w,
                                plb,
                                bc)) {
                            bc = b2;
                        } else break;
                    }

                    debug.debugBlock(new BlockPos(bc), Block.getStateById(138));
                    c = new Vec3(bc.xCoord, bc.yCoord, bc.zCoord);
                } else {
                    stage = 2;
                    c = new Vec3(toX+0.5, toY+1, toZ+0.5);
                }
            }
            else if(stage == 2) {
                stage = 3;
                debug.info("Finished!");
                status = ControllerStatus.FINISHED;
            }
        }
        else if(stage == 1 || stage == 2) {
            //if(verbose) System.out.println(p.getPositionVector().distanceTo(c));
            moveTo(c.xCoord, c.yCoord, c.zCoord);
        }
//        else if(stage == 3) {
//            //do nothing
//        }
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

        if(angle > 45 && angle < 160) pc.left();
        else if(angle > 180 && angle < 350) pc.right();
        else {
            //do obstacle checks
            if (Minecraft.getSystemTime() % 1000 < 500) {
                pc.left();
            } else {
                pc.right();
            }
        }

        if(p.isInWater() && p.getPosition().getY() <= y) {
            pc.jump();
        }
        if(WorldTools.isBlocked(w, new BlockPos(getPosition()), new BlockPos(x,y,z), 1)) {
            pc.jump();
        }
    }

    private long delay;
    private boolean isDelayed() {
        return (Minecraft.getMinecraft().getSystemTime() < delay);
    }
    private void setDelay(long ticks) {
        delay = Minecraft.getMinecraft().getSystemTime()+ticks;
    }

    public boolean calculate() {
        debug.info("Searching");
        //PlayerController pc = PlayerController.getInstance();
        //EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
        World w = Minecraft.getMinecraft().theWorld;
        class Tuple implements Comparable<Tuple> {
            LinkedList<Vec3> path;
            double f,h;
            public Tuple(LinkedList<Vec3> path, double f) {
                this.path = path;
                this.f = f;
                Vec3 b = path.getLast();
                this.h = Math.abs(b.xCoord-toX)+Math.abs(b.yCoord-toY)+Math.abs(b.zCoord-toZ);
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
                Vec3 b = path.getLast();
                return b.hashCode();
//                return (int)b.xCoord +
//                        (int)(WorldGrid.SURFACE_GRID_SIZE * b.yCoord) +
//                        (int)(WorldGrid.SURFACE_GRID_SIZE*WorldGrid.SURFACE_GRID_SIZE*b.zCoord);
            }
        }
        PriorityQueue<Tuple> pool = new PriorityQueue<Tuple>();
        HashSet<Vec3> hist = new HashSet<Vec3>();
        LinkedList<Vec3> q = new LinkedList<Vec3>(); q.add(from);
        pool.add(new Tuple(q, 0));

        int[][] search = new int[][] {
                new int[] {1,0,0},
                new int[] {0,0,1},
                new int[] {-1,0,0},
                new int[] {0,0,-1},
        };

        int T = 0;
        //System.out.format("from: %s to: %s", from, to);
Loop1:
        while(true) {
            T++;
            if(pool.isEmpty() || T > WorldGrid.MAX_DISTANCE*WorldGrid.MAX_DISTANCE/4) {
                status = ControllerStatus.FAILURE;
                return false;
            }
            Tuple t = pool.poll();
            Vec3 p = t.path.getLast(); BlockPos b = new BlockPos(p);
            if(WorldTools.isSolid(w, b)) {
                for(int[] s : search) {
                    Vec3 p2 = p.addVector(s[0], s[1], s[2]); BlockPos b2 = new BlockPos(p2);
                    if (hist.contains(b2)) continue;
                    if (b2.equals(to)) {
                        q = new LinkedList<Vec3>(t.path);
                        break Loop1;
                    }
                    if (WorldTools.isSolid(w, b2)) {
                        p2 = WorldTools.findAboveSurface(w, p2); b2 = new BlockPos(p2);
                        if (p2.yCoord - p.yCoord > 1 ||
                                !WorldTools.open(w, b, 3) || !WorldTools.open(w, b2, 2))
                            continue;
                    }
                    else if (WorldTools.isWaterBlock(w, b2)) {
                        //TODO: Add a check for climbing up a wall of water
                        if (!WorldTools.open(w, b2, 2)) continue;
                    }
                    else {
                        p2 = WorldTools.findBelowSurface(w, p2); b2 = new BlockPos(p2);
                        double d = p.yCoord - p2.yCoord;
                        if (d > 3 || !WorldTools.open(w, b2, 2) ||
                                !WorldTools.open(w, b2, (int)(d + 2)))
                            continue;
                    }
                    LinkedList<Vec3> npath = new LinkedList<Vec3>(t.path);
                    npath.add(p2);
                    hist.add(p2);
                    //if i want to use A*, will have to also add the distance between b,b2
                    pool.add(new Tuple(npath, t.f + 0));
                }
            }
            else if (WorldTools.isWaterBlock(w, b)) {
                for(int[] s : search) {
                    Vec3 p2 = p.addVector(s[0], s[1], s[2]); BlockPos b2 = new BlockPos(p2);
                    if (b2.equals(to)) {
                        q = new LinkedList<Vec3>(t.path);
                        break Loop1;
                    }
                    if(WorldTools.isSolid(w, b2)) {
                        if(!WorldTools.open(w, b2, 2)) continue;
                    }
                    LinkedList<Vec3> npath = new LinkedList<Vec3>(t.path);
                    npath.add(p2);
                    hist.add(p2);
                    pool.add(new Tuple(npath, t.f + 0));
                }

                Vec3 pu = p.addVector(0,1,0); BlockPos bu = new BlockPos(pu);
                if (pu.equals(to)) {
                    q = new LinkedList<Vec3>(t.path);
                    break Loop1;
                }
                if(hist.contains(pu) && WorldTools.isWaterBlock(w, bu) && WorldTools.open(w, bu, 1)) {
                    LinkedList<Vec3> npath = new LinkedList<Vec3>(t.path);
                    npath.add(pu);
                    hist.add(pu);
                    pool.add(new Tuple(npath, t.f + 0));
                }
                Vec3 pd = p.addVector(0, -1, 0); BlockPos bd = new BlockPos(pd);
                if (pd.equals(to)) {
                    q = new LinkedList<Vec3>(t.path);
                    break Loop1;
                }
                if(!hist.contains(pd)) {
                    LinkedList<Vec3> npath = new LinkedList<Vec3>(t.path);
                    npath.add(pd);
                    hist.add(pd);
                    pool.add(new Tuple(npath, t.f + 0));
                }
            }
            else { //what else is there?
                debug.errorf("Invalid block evaluated at %s with property %s", b, w.getBlockState(b).toString());
            }
        }
        path = q;
        return true;
    }

    public static boolean pathExists(Vec3 from, Vec3 to) {
        return new MoveShort(from, to, false).calculate();
    }

    @Override
    public Vec3 getCurrentGoal() {
        if(c == null) return null;
        else return new Vec3(c.xCoord,c.yCoord,c.zCoord);
    }

    @Override
    public String toString() {
        return String.format("MoveShort f:(%-5.2f, %-5.2f, %-5.2f) t:(%-5.2f, %-5.2f, %-5.2f)",
                from.xCoord, from.yCoord, from.zCoord,
                to.xCoord, to.yCoord, to.zCoord);
    }
}
