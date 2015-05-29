package mcagent.actuator.movement;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chad on 5/25/2015.
 */
public class Target {
    private WorldGrid w = WorldGrid.getInstance();
    private double x,y,z;
    private LinkedList<Target> neighbors;
    private int lastUse;
    private boolean isCave;

    public LinkedList<Target> getNeighbors() {
        return neighbors;
    }

    public void collectNeighbors() {
    }

}
