package mcagent.actuator.movement;

import java.util.LinkedList;

/**
 * Created by chad on 4/25/16.
 */
public class CaveGrid {
    private WorldGrid world;
    private LinkedList<GridNode> nodes;
    private LinkedList<GridNode> surfaceEntrances;

    public void mergeWith(CaveGrid otherCave) {
        for(GridNode n: otherCave.nodes) {
            n.inCave = this;
        }
        for(GridNode n: otherCave.surfaceEntrances) {
            if(!n.caves.contains(this)) n.caves.add(this);
            n.caves.remove(otherCave);
        }
    }

    public void delete() {
        for(GridNode n: nodes) {
            world.delete(n);
        }
    }
}
