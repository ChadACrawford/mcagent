package edu.utulsa.masters.mcagent.actuator.movement;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;
import edu.utulsa.masters.mcagent.util.WorldTools;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by chad on 4/25/16.
 */
public class WorldGrid {
    public final static int NUM_SURFACE_NODES = 100;
    public final static int NUM_SOMETHING = 1;

    public final static int X_RANGE = 100;
    public final static int Z_RANGE = 100;

    private final Random rand = new Random();
    private EntityPlayerSP player;
    private World world;

    private LinkedList<GridNode> nodes = new LinkedList<GridNode>();

    protected BlockPos[] genPoints(int n) {
        int px = (int)player.posX;
        int xa = px - X_RANGE, xb = px + X_RANGE;

        int zp = (int)player.posZ;
        int za = zp - Z_RANGE, zb = zp + Z_RANGE;

        BlockPos blocks[] = new BlockPos[n];
        for(int i = 0; i < n; i++) {
            blocks[i] = WorldTools.findSurface(world, xa + rand.nextInt(xb-xa), za + rand.nextInt(zb-za));
        }

        return blocks;
    }

    public void add() {

    }

    public void delete(GridNode n) {
    }
}
