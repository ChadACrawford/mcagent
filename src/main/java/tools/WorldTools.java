package tools;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Chad on 3/30/2015.
 */
public class WorldTools {
    public static double PLAYER_HEIGHT = 2.0;
    public static double PLAYER_WIDTH = 0.5;

    public static BlockPos findGroundBlock(World w, BlockPos p) {
        BlockPos r = new BlockPos(p);
        while(r.getY()>0 && w.isAirBlock(r)) {
            r = r.add(0, -1, 0);
            //System.out.println(r);
        }
        if(!w.isAirBlock(r))
            return r;
        else
            return null;
    }

    public static double distance(Vec3 v1, Vec3 v2) {
        return Math.sqrt(Math.pow(v1.xCoord-v2.xCoord,2)+Math.pow(v1.yCoord-v2.yCoord,2)+Math.pow(v1.zCoord-v2.zCoord,2));
    }

    public static boolean open(World w, BlockPos p, int d) {
        for(int i = 1; i <= d; i++) {
            if(!w.isAirBlock(p.add(0,i,0))) return false;
        }
        return true;
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

    public static boolean isSolid(World w, BlockPos p) {
        if(!w.isAirBlock(p)) return true;
        return false;
    }

    /**
     * Checks square region around a point to see if it is open
     * @param x
     * @param y
     * @param z
     * @param r
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

    public static boolean isValidPath(World w, BlockPos from, BlockPos to) {
        if(from.getY() != to.getY()) return false;
        double d = Math.sqrt(Math.pow(from.getX()-to.getX(),2)+Math.pow(from.getZ()-to.getZ(),2));
        if(d < 1.0) return true;
        double dx = (to.getX()-from.getX())/d/5, dz = (to.getZ()-from.getZ())/d/5;
        BlockPos b1 = from;
        double atX = b1.getX(), atZ = b1.getZ();
        for(int i = 1; i <= 5*(d+1); i++) {
            atX += dx; atZ += dz;
            //BlockPos b2 = getAccessibleBlock(w, b1, atX, atZ);
            BlockPos b2 = new BlockPos(atX, b1.getY(), atZ);
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
