package mcagent.util;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chad on 3/30/2015.
 */
public class WorldTools {
    public static double PLAYER_HEIGHT = 2.0;
    public static double PLAYER_WIDTH = 0.5;

    public static BlockPos findGroundBlock(World w, BlockPos p) {
        for(int i = 0; p.getY()+i>=0; i--) {
            BlockPos r = p.add(0,i,0);
            if(isSolid(w, r)) return r;
        }
        return null;
    }

    public static double distance(Vec3 v1, Vec3 v2) {
        return Math.sqrt(Math.pow(v1.xCoord-v2.xCoord,2)+Math.pow(v1.yCoord-v2.yCoord,2)+Math.pow(v1.zCoord-v2.zCoord,2));
    }
    public static double distance(double[] x1, double[] x2) {
        double dx = x1[0]-x2[0], dy = x1[1]-x2[1], dz = x1[2]-x2[2];
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }

    public static boolean open(World w, BlockPos p, int d) {
        for(int i = 1; i <= d && p.getY()+i<w.getHeight(); i++) {
            if(isSolid(w,p.add(0,i,0))) return false;
        }
        return true;
    }

    /**
     * Finds the first surface block (block with an air block above it) directly above p, assuming p is solid
     * @param w The world
     * @param p The block to begin searching at
     * @return The nearest surface block directly above p
     */
    public static BlockPos findAboveSurfaceBlock(World w, BlockPos p) {
        for(int i = 1; p.getY()+i <= w.getHeight(); i++) {
            if(!isSolid(w,p.add(0,i,0))
                    || isWaterBlock(w, p.add(0,i,0)) && !isSolid(w, p.add(0,i+1,0)))
                return p.add(0,i-1,0);
        }
        return null;
    }

    /**
     * Finds the first surface block (block with an air block above it) directly below p, assuming p is an air block
     * @param w The world
     * @param p The block to begin searching at
     * @return The first surface block directly below p
     */
    public static BlockPos findBelowSurfaceBlock(World w, BlockPos p) {
        for(int i = -1; p.getY()+i>=0; i--) {
            BlockPos n = p.add(0,i,0);
            if(isWaterBlock(w, n) && isWaterBlock(w, n.add(0,-1,0))) return n.add(0,-1,0);
            if(isSolid(w,n)) return n;
        }
        return null;
    }

    public static BlockPos getAccessibleBlock(World w, BlockPos from, double toX, double toZ) {
        BlockPos p = new BlockPos(toX, from.getY(), toZ);
        if(w.isAirBlock(p) && open(w, from, 2) && open(w, p, 2)) {
            for(int i = 1; i <= 3; i++) {
                BlockPos np = p.add(0,-i,0);
                if (!w.isAirBlock(np)) return np;
            }
        }
        else if(!w.isAirBlock(p)) {
            BlockPos pup;
            if(open(w, from, 2) && open(w, p, 2)) return p;
            else if (!w.isAirBlock((pup = p.add(0,1,0))) && open(w, from, 3) && open(w, pup,2)) return pup;
        }
        return null;
    }

    static final HashSet<Integer> nonSolidBlockIds = new HashSet<Integer>(Arrays.asList(new Integer[] {
            0,6,8,9,27,28,31,32,37,38,39,40,50,51,55,59,63,65,66,68,69,70,72,75,
            76,77,83,104,105,106,140,141,142,143,144,147,148,149,150,157,175,176,177,
    }));
    public static boolean isSolid(World w, BlockPos p) {
        int id = Block.getIdFromBlock(w.getBlockState(p).getBlock());
        if(!nonSolidBlockIds.contains(id)) return true;
        return false;
    }

    public static boolean isWaterBlock(World w, BlockPos p) {
        int id = Block.getIdFromBlock(w.getBlockState(p).getBlock());
        return id == 7 || id == 8;
    }

    /**
     * Checks square region around a point to see if it is open
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     * @param r square radius
     * @return
     */
    private static boolean openSpace(World w, double x, double y, double z, double r) {
        BlockPos[] blocks = {
                new BlockPos(x,y,z),
                new BlockPos(x+r,y,z),
                new BlockPos(x-r,y,z),
                new BlockPos(x,y,z+r),
                new BlockPos(x,y,z-r),
                new BlockPos(x+r,y,z+r),
                new BlockPos(x+r,y,z-r),
                new BlockPos(x-r,y,z+r),
                new BlockPos(x-r,y,z-r),
        };
        for(BlockPos b: blocks) {
            if(isSolid(w, b)) return false;
        }
        return true;
    }

    public static boolean openRegion(World w, BlockPos p1, BlockPos p2) {
        for(int x = p1.getX(); x <= p2.getX(); x++) {
            for(int y = p1.getY(); y <= p2.getY(); y++) {
                for(int z = p1.getZ(); z <= p2.getZ(); z++) {
                    if(!isSolid(w, new BlockPos(x, y, z))) return false;
                }
            }
        }
        return true;
    }

    /**
     * Retrieves all blocks that intersect with a line.
     * @param w World object
     * @param from Line beginning endpoint
     * @param to Line finishing endpoint
     * @return List of blocks that intersect line from "from" to "to"
     */
    public static List<BlockPos> intersectingBlocks(World w, final Vec3 from, final Vec3 to) {
        double d = Math.sqrt(
                Math.pow(from.xCoord - to.xCoord, 2)
                + Math.pow(from.yCoord - to.yCoord, 2)
                + Math.pow(from.zCoord - to.zCoord, 2)
        );

        final List<BlockPos> blocks = new LinkedList<BlockPos>();
        blocks.add(new BlockPos(from));
        blocks.add(new BlockPos(to));

//        You can't do anything to fix this
//        I don't even care
        class F {
            F(double m, double b) {this.m = m; this.b = b;}
            double m,b;
            double e(double t) { return m*t + b; }
            double solve(double f) { return (f - b)/m; }
        }
        F fx = new F(to.xCoord - from.xCoord, from.xCoord);
        F fy = new F(to.yCoord - from.yCoord, from.yCoord);
        F fz = new F(to.zCoord - from.zCoord, from.zCoord);

//        I HATE JAVA!!!!! HAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHAHA
//        JAVA IS THE WORST LANGUAGE OF ALL AND I HATE ANYTHING THAT INVOLVES USING JAVA
//        ALL READERS SUFFER AS I CONTINUE TO ABUSE THIS LANGUAGE ***FOR MY OWN PLEASURE***
        class ILine {
            ILine(F f1, F f2, F f3, double x1, double x2, int ix, int iy, int iz) {
                int dx = x1 < x2 ? 1 : -1;
                int n = (int)Math.abs(x1 - x2);
//                if(n == 0) return;
                for(int i = 0; i < n; i++) {
                    double x = Math.ceil(x1) + dx * i;
                    double t = f1.solve(x), y = f2.e(t), z = f3.e(t);
                    double[] r1 = new double[] {x, y, z};
//                    double[] r2 = new double[] {x-1, y, z};
                    blocks.add(new BlockPos(r1[ix], r1[iy], r1[iz]));
//                    blocks.add(new BlockPos(r2[ix], r2[iy], r2[iz]));
                }
            }
        }

        ILine IDontGiveAShitAboutYourOpinionOnMyLackOfCodingStyle = new ILine(fx, fy, fz, from.xCoord, to.xCoord, 0, 1, 2);
        new ILine(fy, fx, fz, from.yCoord, to.yCoord, 1, 0, 2);
        new ILine(fz, fx, fy, from.zCoord, to.zCoord, 1, 2, 0);

        return blocks;
    }

    /**
     * Determines whether a path between two solid blocks is uninterrupted. That is, the player can walk in a direct
     * line to reach the goal.
     * @param w World object
     * @param from From path
     * @param to To path
     * @return
     */
    public static boolean isValidPath(World w, Vec3 from, Vec3 to) {
//        System.out.println(String.format("Path from %30s to %30s", from.toString(), to.toString()));
//        If the start or goal positions are not solid, then this is not a valid path.
        if(!isSolid(w, new BlockPos(from)) || !isSolid(w, new BlockPos(to)))
            return false;
//        If the distance requires at least one jump, then this is not a valid path.
        if(Math.abs(from.yCoord - to.yCoord) > 0.5) return false;

        List<BlockPos> blocks = intersectingBlocks(w, from, to);

        int dx = from.xCoord < to.xCoord ? 1 : -1, dz = from.zCoord < to.zCoord ? 1 : -1;
        boolean isDiagonal = Math.floor(from.xCoord) != Math.floor(to.xCoord)
                && Math.floor(from.zCoord) != Math.floor(to.zCoord);

        for(BlockPos b: blocks) {
            if(!isSolid(w, b) || !open(w, b, 2)) {
                return false;
            }
            if(isDiagonal && (!isSolid(w, b.add(dx, 0, 0)) || !open(w, b.add(dx, 0, 0), 2)
                    || !isSolid(w, b.add(0, 0, dz)) || !open(w, b.add(0, 0, dz), 2))) {
                return false;
            }
        }
        return true;

//        Get the distance
//        double d = Math.sqrt(Math.pow(from.xCoord-to.xCoord,2)+Math.pow(from.zCoord-to.zCoord,2));
//        if(d < 1.0) return true;
//        double dx = (to.xCoord-from.xCoord) / d / 3, dz = (to.zCoord-from.zCoord) / d / 3;
//        double ax = dx > 0 ? -1:1, az = dz > 0 ? -1:1;
//        double atX = from.xCoord, atZ = from.zCoord;
//        for(int i = 1; i <= 3*d; i++) {
//            atX += dx; atZ += dz;
//            BlockPos b1 = new BlockPos(atX, from.yCoord, atZ);
//            //if it's a straight line, don't need to check other directions
//            if(Math.floor(from.xCoord) == Math.floor(to.xCoord) || Math.floor(from.zCoord) == Math.floor(to.zCoord)) {
//                if(!isSolid(w, b1) || !open(w, b1, 2)) return false;
//            } else { //check surrounding blocks if it's diagonal at all
//                if(!isSolid(w, b1) || !open(w, b1, 2)
//                        || !isSolid(w, b1.add(ax, 0, 0)) || !open(w, b1.add(ax, 0, 0), 2)
//                        || !isSolid(w, b1.add(0, 0, az)) || !open(w, b1.add(0, 0, az), 2)
////                        !isSolid(w, b1.add(1, 0, 0)) || !open(w, b1.add(1, 0, 0), 2) ||
////                        !isSolid(w, b1.add(-1, 0, 0)) || !open(w, b1.add(-1, 0, 0), 2) ||
////                        !isSolid(w, b1.add(0, 0, 1)) || !open(w, b1.add(0, 0, 1), 2) ||
////                        !isSolid(w, b1.add(0, 0, -1)) || !open(w, b1.add(0, 0, -1), 2)
//                        )
//                    return false;
//            }
//        }
//        return true;
    }

    /**
     *
     * @param w
     * @param from
     * @param to
     * @return
     */
    public static boolean isValidSwimmingPath(World w, Vec3 from, Vec3 to) {
        if(!isSolid(w, new BlockPos(from)) || !isSolid(w, new BlockPos(to)))
            return false;

        List<BlockPos> botblocks = intersectingBlocks(w, from, to);
        List<BlockPos> topblocks = intersectingBlocks(w,
                from.addVector(0,PLAYER_HEIGHT,0),
                to.addVector(0,PLAYER_HEIGHT,0));
        for(BlockPos b: botblocks) {
            if(!isWaterBlock(w, b)) return false;
        }
        for(BlockPos b: topblocks) {
            if(!isWaterBlock(w, b) && isSolid(w, b)) return false;
        }
        return true;
    }

    /**
     * Determines whether the immediate path of length v is blocked
     * @param w World object
     * @param from From point
     * @param to To point
     * @param v Length of path to check
     * @return
     */
    public static boolean isBlocked(World w, BlockPos from, BlockPos to, double v) {
        double d = Math.sqrt(Math.pow(from.getX()-to.getX(),2)+Math.pow(from.getZ()-to.getZ(),2));
        if(d < 1.0) return false;
        double dx = (to.getX()-from.getX())/d/4, dz = (to.getZ()-from.getZ())/d/4;
        for(int i = 0; i <= 4*(v+1); i++) {
            if(!open(w, new BlockPos(from.getX()+i*dx,from.getY()-1,from.getZ()+i*dz),1)) return true;
        }
        return false;
    }
}
