package kpgr2.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import kpgr2.model.Solid;
import org.junit.jupiter.api.Test;
import transforms.Mat4Identity;
import transforms.Point3D;

class PipelineTest {
    private static class Stub extends Solid {
        Stub(Point3D a, Point3D b) {
            super("stub", 0x123456);
            vertices.add(a);
            vertices.add(b);
            indices.add(0);
            indices.add(1);
        }

        @Override
        public int getEdgeColor(int edge) { return 0xABCDEF; }
    }

    private static List<ScreenSegment> run(Point3D a, Point3D b, Integer override) {
        return Pipeline.project(new Stub(a, b), new Mat4Identity(), 101, 51, override);
    }

    @Test
    void insideEdgeMapsToViewport() {
        List<ScreenSegment> r = run(new Point3D(-1, 1, 0), new Point3D(1, -1, 1), null);
        assertEquals(1, r.size());
        ScreenSegment s = r.get(0);
        assertEquals(0, s.x1());
        assertEquals(0, s.y1());
        assertEquals(100, s.x2());
        assertEquals(50, s.y2());
        assertEquals(0xABCDEF, s.color());
    }

    @Test
    void dropsXBeyondW() {
        assertTrue(run(new Point3D(1.5, 0, 0.5), new Point3D(0, 0, 0.5), null).isEmpty());
    }

    @Test
    void dropsNegativeZ() {
        assertTrue(run(new Point3D(0, 0, -0.1), new Point3D(0, 0, 0.5), null).isEmpty());
    }

    @Test
    void dropsZBeyondW() {
        assertTrue(run(new Point3D(0, 0, 1.1), new Point3D(0, 0, 0.5), null).isEmpty());
    }

    @Test
    void dropsNonPositiveW() {
        assertTrue(run(new Point3D(0, 0, 0, 0), new Point3D(0, 0, 0.5), null).isEmpty());
        assertTrue(run(new Point3D(0, 0, 0, -1), new Point3D(0, 0, 0.5), null).isEmpty());
    }

    @Test
    void overrideColorWins() {
        List<ScreenSegment> r = run(new Point3D(0, 0, 0.5), new Point3D(0.5, 0.5, 0.5), 0xFFFF00);
        assertEquals(0xFFFF00, r.get(0).color());
    }

    @Test
    void rendererForwardsSegmentsToRasterizer() {
        List<int[]> calls = new ArrayList<>();
        WireRenderer wr = new WireRenderer((x1, y1, x2, y2, c) -> calls.add(new int[] {x1, y1, x2, y2, c}));
        wr.render(List.of(new ScreenSegment(1, 2, 3, 4, 5)));
        assertEquals(1, calls.size());
        assertEquals(5, calls.get(0)[4]);
        assertEquals(4, calls.get(0)[3]);
    }
}
