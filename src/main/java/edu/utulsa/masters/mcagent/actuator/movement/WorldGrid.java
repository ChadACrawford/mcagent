package edu.utulsa.masters.mcagent.actuator.movement;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;

/**
 * Created by chad on 4/25/16.
 */
public class WorldGrid {
    public final static int NUM_NODES = 1000;

    private RTree<GridNode, Geometry> tree = RTree.create();

    public void delete(GridNode n) {
    }
}
