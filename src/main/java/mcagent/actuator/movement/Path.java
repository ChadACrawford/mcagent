package mcagent.actuator.movement;

import jdk.nashorn.internal.ir.Block;
import mcagent.Debugger;
import mcagent.actuator.PlayerController;
import mcagent.util.WorldTools;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import javax.vecmath.Point3i;
import java.util.*;

/**
 * Created by chad on 4/25/16.
 */
public class Path {
    protected World w;
    protected LinkedList<BlockPos> path;
    protected Vec3[] refinedPath;

    private Path(World w, LinkedList<BlockPos> path) {
        this.w = w;
        this.path = path;
    }

    public boolean isRefined() {
        return refinedPath != null;
    }

    public void refine() {
        BlockPos start = path.getFirst();
        refinedPath = new Vec3[path.size()];

        ListIterator<BlockPos> iterator = path.listIterator(path.size());

        int i = path.size()-1;
        BlockPos b1 = iterator.previous();
        refinedPath[i] = new Vec3(b1.getX() + 0.5, b1.getY(), b1.getZ() + 0.5);
        while(iterator.hasPrevious()) {
            i--;
            Vec3 p = refinedPath[i+1];
            BlockPos b2 = iterator.previous();

            double xLeft = b2.getX() + 0.2, xRight = b2.getX() + 0.8,
                    zLeft = b2.getZ() + 0.2, zRight = b2.getZ() + 0.8;

            double xCoord = 0, zCoord = 0;

            if(p.xCoord < xLeft) xCoord = xLeft;
            else if(p.xCoord > xRight) xCoord = xRight;
            else xCoord = p.xCoord;

            if(p.zCoord < zLeft) zCoord = zLeft;
            else if(p.zCoord > zRight) zCoord = zRight;
            else zCoord = p.zCoord;

            refinedPath[i] = new Vec3(xCoord, b2.getY(), zCoord);

            b1 = b2;
        }
        refinedPath[0] = new Vec3(start.getX() + 0.5, start.getY(), start.getZ() + 0.5);
    }

    public boolean isValidPath() {
        Iterator<BlockPos> iterator = path.iterator();
        BlockPos b1 = iterator.next();
        if(!isValidBlock(w, b1)) return false;

        while(iterator.hasNext()) {
            BlockPos b2 = iterator.next();
            if(!isValidStep(w, b1, b2)) return false;
            b1 = b2;
        }

        return true;
    }

    int currentPosition = 0;
    public boolean control(PlayerController pc) throws Exception {
        if(!isRefined()) {
            refine();
        }

        if(currentPosition >= refinedPath.length) return true;

        Vec3 p = refinedPath[currentPosition];
        double d = pc.moveTo(p.xCoord, p.yCoord, p.zCoord);
        if(d < 0.1) {
            currentPosition++;
        }
        return false;
    }

    public void reset() {
        currentPosition = 0;
    }

    public static Path computePath(World w, BlockPos start, final BlockPos end) {
        if(!isValidBlock(w, start) || !isValidBlock(w, end)) return null;

        class Node implements Comparable<Node> {
            LinkedList<BlockPos> path;
            BlockPos b;
            double f;
            double h;
            int hashCode;
            Node(LinkedList<BlockPos> path, BlockPos b, double f) {
                this.path = path;
                this.f = f; this.b = b;
                this.h = calcDistance();
                hashCode = (new Point3i(b.getX(), b.getY(), b.getZ())).hashCode();
            }
            double calcDistance() {
                return b.distanceSq(end);
            }

            @Override
            public int compareTo(Node o) {
                return (this.f + this.h < o.h + o.f) ? -1 : 1;
            }
        }

        HashSet<BlockPos> visited = new HashSet<BlockPos>();
        visited.add(start);

        Node first = new Node(new LinkedList<BlockPos>(Arrays.asList(new BlockPos[]{start})), start, 0);
        PriorityQueue<Node> open = new PriorityQueue<Node>();
        open.add(first);

        int numLoops = 0;
        while(true) {
            if(numLoops > 5000 || open.isEmpty())
                return null;

            Node bestNode = open.poll();

            List<BlockPos> next = nextSteps(w, bestNode.b);

            for(BlockPos b2: next) {
                if(visited.contains(b2)) continue;
                LinkedList<BlockPos> npath = new LinkedList<BlockPos>(bestNode.path);
                npath.addLast(b2);
                if(end.equals(b2)) {
                    return new Path(w, npath);
                }
                else {
                    Node n = new Node(npath, b2, bestNode.f + 1);
                    visited.add(b2);
                    open.add(n);
                }
            }

            numLoops++;
        }
    }

    /**
     * Returns all accessible blocks from a source block.
     * @param w The world object.
     * @param b1 The source block.
     * @return A list of all blocks accessible from this point.
     */
    public static List<BlockPos> nextSteps(World w, BlockPos b1) {
        int[][] dxzs = new int[][]{
                new int[]{1, 0},
                new int[]{-1, 0},
                new int[]{0, 1},
                new int[]{0, -1}
        };
        List<BlockPos> blocks = new LinkedList<BlockPos>();
        for(int[] dxz: dxzs) {
            BlockPos b2 = b1.add(dxz[0], 0, dxz[1]);
            if(WorldTools.isSolid(w, b2) || WorldTools.isWaterBlock(w, b2)) {
                if(isValidStep(w, b1, b2)) blocks.add(b2);
            }
            else {
                for(int i = 0; i < 4; i++) {
                    b2 = b2.add(0, -1, 0);
                    if(WorldTools.isSolid(w, b2) || WorldTools.isWaterBlock(w, b2)) {
                        blocks.add(b2);
                        break;
                    }
                }
            }
        }

        if(WorldTools.isWaterBlock(w, b1)) {
            BlockPos b2 = b1.add(0, 1, 0);
            if(isValidStep(w, b1, b2)) blocks.add(b2);

            BlockPos b3 = new BlockPos(b1);
            while(WorldTools.isPassable(w, b3) && b3.getY() >= 0)
                b3.add(0, -1, 0);
            if(WorldTools.isSolid(w, b3) || WorldTools.isPassable(w, b3)) {
               if(isValidStep(w, b1, b3)) blocks.add(b3);
            }
        }

        return blocks;
    }

    /**
     * Returns whether the path from b1 to b2 is valid (this is true if and only if we can stand at the position
     * specified at b2)
     * @param w The world object.
     * @param b1 The source block.
     * @param b2 The destination block.
     * @return Whether b1 -> b2 is a valid path.
     */
    public static boolean isValidStep(World w, BlockPos b1, BlockPos b2) {
        if(WorldTools.isSolid(w, b1)) {
            if(b1.getY() == b2.getY()) {
                return WorldTools.isSolid(w, b2) || WorldTools.isWaterBlock(w, b2);
            }
            else if(b1.getY() < b2.getY()) {
                return b2.getY() - b1.getY() == 1 && WorldTools.open(w, b1, 3) && WorldTools.open(w, b2, 2);
            }
            else { // b1.getY() > b2.getY()
                int yDiff = b1.getY() - b2.getY();
                return yDiff <= 4 && WorldTools.open(w, b2, 2 + yDiff);
            }
        }
        else if(WorldTools.isWaterBlock(w, b1)) {
            // Restrict our movement options to up, down, left, right, forward, backwards
            int xDiff = b1.getX() - b2.getX(), yDiff = b1.getY() - b2.getY(), zDiff = b1.getZ() - b2.getZ();
            if(Math.abs(xDiff) == 1 && yDiff == 0 && zDiff == 0
                    || Math.abs(zDiff) == 1 && yDiff == 0 && xDiff == 0) {
                if(WorldTools.isSolid(w, b2) || WorldTools.isWaterBlock(w, b2)) {
                    return WorldTools.open(w, b2, 2);
                }
                else {
                    return false;
                }
            }
            else if(yDiff == -1 && xDiff == 0 && zDiff == 0) {
                return WorldTools.open(w, b2, 2);
            }
            else if(yDiff >= 1 && xDiff == 0 && zDiff == 0) {
                if(!WorldTools.open(w, b2, yDiff)) return false;
                return WorldTools.isWaterBlock(w, b2) || (WorldTools.isSolid(w, b2) && yDiff <= 4);
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    /**
     * Returns whether this block can be occupied by the player.
     * @param w The world object.
     * @param b The block position.
     * @return True if the player can stand on this block.
     */
    public static boolean isValidBlock(World w, BlockPos b) {
        return (WorldTools.isSolid(w, b) || WorldTools.isWaterBlock(w, b)) && WorldTools.open(w, b, 2);
    }
}
