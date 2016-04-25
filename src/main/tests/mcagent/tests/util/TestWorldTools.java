package mcagent.tests.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;
import mcagent.util.WorldTools;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * Created by chad on 2/21/16.
 */
public class TestWorldTools {
    @Before
    public void setUp() {
    }

    @Test
    public void testIntersectingBlocks() {
        Vec3 b1 = new Vec3(0, 0, 0);

        Vec3 b2 = new Vec3(3, 0, 0);
        List<BlockPos> path1 = WorldTools.intersectingBlocks(b1, b2);
        assertThat(path1, hasItems(
                new BlockPos(0, 0, 0),
                new BlockPos(1, 0, 0),
                new BlockPos(2, 0, 0),
                new BlockPos(3, 0, 0)
        ));

        Vec3 b3 = new Vec3(0, 3, 0);
        List<BlockPos> path2 = WorldTools.intersectingBlocks(b1, b3);
        assertThat(path2, hasItems(
                new BlockPos(0, 0, 0),
                new BlockPos(0, 1, 0),
                new BlockPos(0, 2, 0),
                new BlockPos(0, 3, 0)
        ));

        Vec3 b4 = new Vec3(0, 0, 3);
        List<BlockPos> path3 = WorldTools.intersectingBlocks(b1, b4);
        assertThat(path3, hasItems(
                new BlockPos(0, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, 2),
                new BlockPos(0, 0, 3)
        ));

        Vec3 b5 = new Vec3(3, 3, 0);
        List<BlockPos> path4 = WorldTools.intersectingBlocks(b1, b5);
        assertThat(path4, hasItems(
                new BlockPos(0, 0, 0),
                new BlockPos(1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(1, 1, 0),
                new BlockPos(2, 1, 0), new BlockPos(1, 2, 0), new BlockPos(2, 2, 0),
                new BlockPos(3, 2, 0), new BlockPos(2, 3, 0), new BlockPos(3, 3, 0)
        ));

        Vec3 b6 = new Vec3(3, 3, 3);
        List<BlockPos> path5 = WorldTools.intersectingBlocks(b1, b6);
        assertThat(path5, hasItems(
                new BlockPos(0, 0, 0),
                new BlockPos(1, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, 0, 1), new BlockPos(1, 1, 1),
                new BlockPos(1, 1, 0), new BlockPos(1, 0, 1), new BlockPos(0, 1, 1),
                new BlockPos(2, 1, 1), new BlockPos(1, 2, 1), new BlockPos(1, 1, 2), new BlockPos(2, 2, 2),
                new BlockPos(2, 2, 1), new BlockPos(2, 1, 2), new BlockPos(1, 2, 2),
                new BlockPos(3, 2, 2), new BlockPos(2, 3, 2), new BlockPos(2, 2, 3), new BlockPos(3, 3, 3),
                new BlockPos(3, 3, 2), new BlockPos(3, 2, 3), new BlockPos(3, 3, 2)
        ));

        Vec3 b7 = new Vec3(-3, -3, -3);
        List<BlockPos> path6 = WorldTools.intersectingBlocks(b1, b7);
        assertThat(path6, hasItems(
                new BlockPos(0, 0, 0),
                new BlockPos(-1, 0, 0), new BlockPos(0, -1, 0), new BlockPos(0, 0, -1), new BlockPos(-1, -1, -1),
                new BlockPos(-1, -1, 0), new BlockPos(-1, 0, -1), new BlockPos(0, -1, -1),
                new BlockPos(-2, -1, -1), new BlockPos(-1, -2, -1), new BlockPos(-1, -1, -2), new BlockPos(-2, -2, -2),
                new BlockPos(-2, -2, -1), new BlockPos(-2, -1, -2), new BlockPos(-1, -2, -2),
                new BlockPos(-3, -2, -2), new BlockPos(-2, -3, -2), new BlockPos(-2, -2, -3), new BlockPos(-3, -3, -3),
                new BlockPos(-3, -3, -2), new BlockPos(-3, -2, -3), new BlockPos(-3, -3, -2)
        ));
    }
}
