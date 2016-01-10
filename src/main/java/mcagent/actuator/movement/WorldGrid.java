package mcagent.actuator.movement;

import edu.wlu.cs.levy.CG.KeyDuplicateException;
import edu.wlu.cs.levy.CG.KeySizeException;
import mcagent.DebugObject;
import mcagent.Debugger;
import mcagent.MCAgent;
import mcagent.actuator.PlayerController;
import mcagent.util.render.Color;
import mcagent.util.render.Render3D;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import edu.wlu.cs.levy.CG.KDTree;
import mcagent.util.WorldTools;
import scala.collection.parallel.ParIterableLike;

import java.util.*;

/**
 * Created by Chad on 5/25/2015.
 */
public class WorldGrid {
    // Constants
    public static final int MAX_NEIGHBORS = 10;
    public static final double MAX_DISTANCE = 20.0;
    public static final int SURFACE_GRID_SIZE = 10;
    public static final int SURFACE_SEARCH_SIZE = 60;
    private Debugger debug = new Debugger(this);

    private static WorldGrid instance = null;

    private WorldGrid() {
        w = Minecraft.getMinecraft().theWorld;
    }
    public static WorldGrid getInstance() {
        if(instance == null) {
            instance = new WorldGrid();
        }
        return instance;
    }

    private KDTree<Target> tree;// = new KDTree.Euclidean<Target>(3);
    private LinkedList<Target> list;// = new LinkedList<Target>();
    private World w;


    public Target getNearestTarget(BlockPos p) {
        return getNearestTarget(new double[] {p.getX(), p.getY(), p.getZ()});
    }
    public Target getNearestTarget(double x, double y, double z) {
        return getNearestTarget(new double[] {x,y,z});
    }
    public Target getNearestTarget(double[] l) {
        try {
            //System.out.format("Searching near: %6.4f %6.4f %6.4f\n",l[0],l[1],l[2]);
            return tree.nearest(l);
        } catch (KeySizeException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Target> getNearestTargets(BlockPos p, int k) {
        return getNearestTargets(new double[]{p.getX(), p.getY(), p.getZ()}, k);
    }
    public List<Target> getNearestTargets(double x, double y, double z, int k) {
//        ArrayList<KDTree.SearchResult<Target>> results = tree.nearestNeighbours(new double[] {x,y,z}, k);
//        LinkedList<Target> rets = new LinkedList<Target>();
//        for(KDTree.SearchResult<Target> t: results) {
//            rets.add(t.payload);
//        }
//        return rets;
        return getNearestTargets(new double[] {x,y,z}, k);
    }
    public List<Target> getNearestTargets(double[] l, int k) {
        try {
            return tree.nearest(l, k);
        } catch (KeySizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Target addTarget(Target t) {
        try {
            tree.insert(t.coords(), t);
        } catch (KeySizeException e) {
            e.printStackTrace();
        } catch (KeyDuplicateException e) {
            e.printStackTrace();
            return null;
        }
        list.add(t);
        return t;
    }
    private Target addTarget(BlockPos p, boolean isCave) {
        Target t = new Target(p,isCave);
        return addTarget(t);
    }
    private Target addTarget(int x, int y, int z, boolean isCave) {
        return addTarget(new BlockPos(x,y,z), isCave);
    }

    public void debugTargets() {
        for(Target t: list) {
            debug.debugBlock(t.getBlock(), Block.getStateById(89));
        }
    }

//    HashSet<Target> targets;
//    long lastSearchTime = 0;
    public void drawEdges(Render3D render) {
        if(list == null || list.isEmpty()) return;
        //This is for checking where I can go from a certain point.
//        Target t = getNearestTarget(Minecraft.getMinecraft().thePlayer.getPosition());
//        if(targets == null || !targets.contains(t) && Minecraft.getSystemTime() > lastSearchTime + 1000) {
//            targets = new HashSet<Target>();
//            lastSearchTime = Minecraft.getSystemTime();
//            HashSet<Target> toSearch = new HashSet<Target>(); toSearch.add(t);
//            while(!toSearch.isEmpty()) {
//                Target t2 = toSearch.iterator().next();
//                targets.add(t2);
//                for(Target t3: t2.getNeighbors()) if(!targets.contains(t3)) toSearch.add(t3);
//                toSearch.remove(t2);
//            }
//        }

        for(Target t1: list) {
            for(Target t2: t1.getNeighbors()) {
                Vec3 v1 = new Vec3(t1.getX()+0.5,t1.getY()+0.5,t1.getZ()+0.5), v2 = new Vec3(t2.getX()+0.5,t2.getY()+0.5,t2.getZ()+0.5);
                if(!t2.isCave()) render.Line(v1,v2,new Color(255,0,0),true);
                else render.Line(v1,v2,new Color(0,255,0),true);
            }
        }
        //System.out.println(i);
    }

    public boolean inRange(int x, int z) {
        return !(Math.abs(x-centerX) > SURFACE_SEARCH_SIZE ||
                    Math.abs(z-centerZ) > SURFACE_SEARCH_SIZE);
    }

    public int centerX,centerZ;
    public void explore(int centerX, int y, int centerZ) {
        this.reset();
        this.centerX = centerX; this.centerZ = centerZ;

        LinkedList<Target> targets = new LinkedList<Target>();
        targets.add(new Target(centerX, y, centerZ, !WorldTools.open(w, new BlockPos(centerX, y, centerZ), w.getHeight())));
        for(int x = centerX-SURFACE_SEARCH_SIZE; x <= centerX+SURFACE_SEARCH_SIZE; x+=SURFACE_GRID_SIZE) {
            for(int z = centerZ-SURFACE_SEARCH_SIZE; z <= centerZ+SURFACE_SEARCH_SIZE; z+=SURFACE_GRID_SIZE) {
//                BlockPos p = new BlockPos(x,w.getHeight(),z);
//                p = WorldTools.findGroundBlock(w, p);
                BlockPos p = WorldTools.findBelowSurfaceBlock(w, new BlockPos(x, w.getHeight()+1, z));
                Target t = addTarget(p, false);
                targets.add(t);
                //exploreCaves(t);
            }
        }

        for(Target t: targets) {
            exploreCaves(t);
        }

        //calculate neighbors (probably will be a lengthy computation)
        for(Target t: list) {
            List<Target> results =  getNearestTargets(t.coords(), MAX_NEIGHBORS);
            for(Target s: results) {
                if(WorldTools.distance(s.coords(),t.coords()) < MAX_DISTANCE && t != s
                        && MoveShort.pathExists(t.getBlock(), s.getBlock())) {
                    t.getNeighbors().add(s);
                }
            }
        }
    }
    public void explore(BlockPos p) {
        explore(p.getX(), p.getY(), p.getZ());
    }

    public void exploreCaves(Target target) {
        class Tuple implements Comparable<Tuple> {
            public Target t;
            public double d;
            @Override
            public int compareTo(Tuple o) {
                return d<o.d ? -1:1;
            }
        }
        World w = PlayerController.getInstance().getWorld();

        WorldTools.open(w, target.getBlock(), w.getHeight());

        LinkedList<Tuple> targets = new LinkedList<Tuple>();

        //start by getting all covered points (which should be caves)
        for(double x = target.getX()-SURFACE_GRID_SIZE; x < target.getX()+SURFACE_GRID_SIZE; x++) {
            for(double z = target.getZ()-SURFACE_GRID_SIZE; z < target.getZ()+SURFACE_GRID_SIZE; z++) {
                if(x == target.getX() && z == target.getZ()) continue;
                BlockPos p = new BlockPos(x,target.getY(),z);
                BlockPos n;
                if(WorldTools.isSolid(w,p)) {
                    n = WorldTools.findAboveSurfaceBlock(w, p);
                } else {
                    n = WorldTools.findBelowSurfaceBlock(w, p);
                }
                if(n == null || !inRange(n.getX(),n.getZ()) || !WorldTools.open(w,n,2)  || WorldTools.open(w, n, w.getHeight())) continue;
                Tuple tu = new Tuple();
                Target t = new Target(n,true);
                //double d = tree.nearest(t.coords()).distance;
                double d = WorldTools.distance(getNearestTarget(t.coords()).coords(), t.coords());
                if(d < MAX_DISTANCE/2) continue;
                tu.t = t; tu.d = d;
                targets.add(tu);
            }
        }

        if(targets.isEmpty()) return;
        //now, continually add appealing targets until there are no more
        LinkedList<Target> toAdd = new LinkedList<Target>();
        while(true) {
            if(targets.isEmpty()) break;
            Tuple tu = Collections.max(targets);
            if(tu.d < MAX_DISTANCE/2) break;
            toAdd.add(tu.t);
            addTarget(tu.t);
            targets.remove(tu);
            for(Tuple tu2: targets) {
                //double d = tree.nearest(tu2.t.coords()).distance;
                double d = WorldTools.distance(getNearestTarget(tu2.t.coords()).coords(),tu2.t.coords());
                tu2.d = d;
            }
        }

        for(Target t: toAdd) {
            exploreCaves(t);
        }
    }

    public void reset() {
        debug.reset();
        tree = new KDTree<Target>(3);
        list = new LinkedList<Target>();
    }

    @Override
    public String toString() {
        return "WorldGrid";
    }
}
