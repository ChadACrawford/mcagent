package edu.utulsa.masters.mcagent.actuator.movement;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by chad on 4/25/16.
 */
public class GridNode {
    double x, y, z;
    CaveGrid inCave;
    WorldGrid world;
    HashMap<GridNode, Path> neighbors;
    LinkedList<CaveGrid> caves;



}
