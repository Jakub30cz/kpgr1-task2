package transforms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CubicTest {
    private static final double EPS = 1e-9;
    private static final Point3D P0 = new Point3D(0, 0, 0);
    private static final Point3D P1 = new Point3D(1, 2, 0.5);
    private static final Point3D P2 = new Point3D(3, 2, -1);
    private static final Point3D P3 = new Point3D(4, 0, 2);

    private static void assertPoint(Point3D e, Point3D a) {
        assertEquals(e.x(), a.x(), EPS);
        assertEquals(e.y(), a.y(), EPS);
        assertEquals(e.z(), a.z(), EPS);
    }

    @Test
    void bezierEndpoints() {
        Cubic c = new Cubic(Cubic.BEZIER, P0, P1, P2, P3);
        assertPoint(P0, c.compute(0));
        assertPoint(P3, c.compute(1));
    }

    @Test
    void fergusonEndsAndTangent() {
        Point3D r0 = new Point3D(2, 1, 3);
        Point3D r1 = new Point3D(-1, 4, 0);
        Cubic c = new Cubic(Cubic.FERGUSON, P0, P1, r0, r1);
        assertPoint(P0, c.compute(0));
        assertPoint(P1, c.compute(1));
        double h = 1e-6;
        Point3D a = c.compute(0);
        Point3D b = c.compute(h);
        assertEquals(r0.x(), (b.x() - a.x()) / h, 1e-4);
        assertEquals(r0.y(), (b.y() - a.y()) / h, 1e-4);
        assertEquals(r0.z(), (b.z() - a.z()) / h, 1e-4);
    }

    @Test
    void coonsStart() {
        Cubic c = new Cubic(Cubic.COONS, P0, P1, P2, P3);
        Point3D e = new Point3D((P0.x() + 4 * P1.x() + P2.x()) / 6, (P0.y() + 4 * P1.y() + P2.y()) / 6,
                (P0.z() + 4 * P1.z() + P2.z()) / 6);
        assertPoint(e, c.compute(0));
    }

    @Test
    void bicubicBezierCorners() {
        Point3D[] g = new Point3D[16];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) g[i * 4 + j] = new Point3D(i, j, i * j * 0.3 + 1);
        Bicubic b = new Bicubic(Cubic.BEZIER, g);
        assertPoint(g[0], b.compute(0, 0));
        assertPoint(g[3], b.compute(0, 1));
        assertPoint(g[12], b.compute(1, 0));
        assertPoint(g[15], b.compute(1, 1));
    }

    @Test
    void planarGridStaysFlat() {
        Point3D[] g = new Point3D[16];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) g[i * 4 + j] = new Point3D(i, j, 0);
        for (Mat4 basis : new Mat4[] {Cubic.BEZIER, Cubic.FERGUSON, Cubic.COONS}) {
            Bicubic b = new Bicubic(basis, g);
            for (double u = 0; u <= 1; u += 0.25)
                for (double v = 0; v <= 1; v += 0.25) assertEquals(0, b.compute(u, v).z(), EPS);
        }
    }
}
