package kpgr2.solids;

import static org.junit.jupiter.api.Assertions.*;

import kpgr2.model.Solid;
import kpgr2.model.Tessellated;
import org.junit.jupiter.api.Test;
import transforms.Cubic;
import transforms.Point3D;

class CurvesTest {
    private static final Point3D[] P4 = {
        new Point3D(0, 0, 0), new Point3D(1, 2, 0), new Point3D(2, 2, 1), new Point3D(3, 0, 1)
    };

    private static void assertPoint(Point3D e, Point3D a) {
        assertEquals(e.x(), a.x(), 1e-9);
        assertEquals(e.y(), a.y(), 1e-9);
        assertEquals(e.z(), a.z(), 1e-9);
    }

    @Test
    void curveCountsFollowSegments() {
        Solid[] curves = {
            new CubicCurve("b", Cubic.BEZIER, P4, 0xff0000),
            new VivianiCurve("v", 1, 0x00ff00),
            new ConicalSpiral("s", 1, 2, 3, 0x0000ff)
        };
        for (Solid s : curves)
            for (int n : new int[] {2, 7, 100}) {
                ((Tessellated) s).setSegments(n);
                assertEquals(n + 1, s.getVertices().size());
                assertEquals(2 * n, s.getIndices().size());
            }
    }

    @Test
    void setSegmentsChangesCounts() {
        CubicCurve c = new CubicCurve("b", Cubic.BEZIER, P4, 0);
        int before = c.getVertices().size();
        c.setSegments(before + 5);
        assertNotEquals(before, c.getVertices().size());
    }

    @Test
    void bezierEndpointsAreControlPoints() {
        CubicCurve c = new CubicCurve("b", Cubic.BEZIER, P4, 0);
        assertPoint(P4[0], c.getVertices().get(0));
        assertPoint(P4[3], c.getVertices().get(c.getVertices().size() - 1));
    }

    @Test
    void vivianiLiesOnSphere() {
        VivianiCurve v = new VivianiCurve("v", 1.7, 0);
        v.setSegments(50);
        for (Point3D p : v.getVertices())
            assertEquals(1.7, Math.sqrt(p.x() * p.x() + p.y() * p.y() + p.z() * p.z()), 1e-9);
    }

    @Test
    void surfaceCountsAndCorners() {
        Point3D[] cp = BicubicSurface.defaultControlPoints();
        BicubicSurface s = new BicubicSurface("s", cp, 0);
        for (int n : new int[] {2, 9}) {
            s.setSegments(n);
            int m = n + 1;
            assertEquals(m * m, s.getVertices().size());
            assertEquals(4 * n * m, s.getIndices().size());
            assertPoint(cp[0], s.getVertices().get(0));
            assertPoint(cp[3], s.getVertices().get(n));
            assertPoint(cp[12], s.getVertices().get(n * m));
            assertPoint(cp[15], s.getVertices().get(m * m - 1));
        }
    }
}
