package mcagent.util;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

import java.util.HashSet;

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
            if(!isSolid(w,p.add(0,i,0))) return p.add(0,i-1,0);
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
            0,6,27,28,31,32,37,38,39,40,50,51,55,59,63,65,66,68,69,70,72,75,76,77,83,104,105,106,140,141,142,143,144,147,148,149,150,157,175,176,177,
    }));
    public static boolean isSolid(World w, BlockPos p) {
        int id = Block.getIdFromBlock(w.getBlockState(p).getBlock());
        if(!nonSolidBlockIds.contains(id)) return true;
        return false;
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

    public static boolean isValidPath(World w, Vec3 from, Vec3 to) {
        if(Math.abs(from.yCoord - to.yCoord) < 0.1) return false;
        double d = Math.sqrt(Math.pow(from.xCoord-to.xCoord,2)+Math.pow(from.zCoord-to.zCoord,2));
        if(d < 1.0) return true;
        double dx = (to.xCoord-from.xCoord)/d/5, dz = (to.zCoord-from.zCoord)/d/5;
        //BlockPos b1 = from;
        double atX = from.xCoord, atZ = from.zCoord;
        for(int i = 1; i <= 5*(d+1); i++) {
            atX += dx; atZ += dz;
            BlockPos b1 = new BlockPos(atX, from.yCoord, atZ);
            BlockPos b2 = new BlockPos(atX, from.yCoord, atZ);
            if(b2 == null) {
                return false;
            }
            if(!open(w, b2, 2) && open(w, b1, 3) && open(w, b2.add(0,1,0), 2)) {
                b2 = b2.add(0,1,0);
            }
            if(!openSpace(w,atX,b2.getY(),atZ,PLAYER_WIDTH/2)) {
                return false;
            }
            b1 = b2;
        }
        return true;
        //if(to.distanceSq(b1.getX(), b1.getY(), b1.getZ())<1.) return true;
        //System.out.format("x1: %4d y1: %4d z1: %4d x2: %8.2f z2: %8.2f", b1.getX(), b1.getY(), b1.getZ(), atX, atZ);
        //return false;
    }

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
