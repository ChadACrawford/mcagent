package mcagent.actuator.movement;

/**
 * Created by Chad on 5/25/2015.
 */
public class WorldGrid {
    private static WorldGrid instance = null;
    private WorldGrid() {}
    public static WorldGrid getInstance() {
        if(instance == null) {
            instance = new WorldGrid();
        }
        return instance;
    }

    public Target getNearestTarget(double x, double y, double z) {
        return null;
    }

    private Target addTarget(double x, double y, double z) {
        return null;
    }

    public void explore() {
    }

    public void exploreCave(Target target) {

    }


}
