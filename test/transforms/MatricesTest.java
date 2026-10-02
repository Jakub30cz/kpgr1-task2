package transforms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MatricesTest {
    private static final double EPS = 1e-9;

    private static void assertPoint(Point3D p, double x, double y, double z) {
        assertEquals(x, p.x(), EPS);
        assertEquals(y, p.y(), EPS);
        assertEquals(z, p.z(), EPS);
    }

    @Test
    void translation() {
        assertPoint(new Point3D(1, 2, 3).mul(new Mat4Transl(4, 5, 6)), 5, 7, 9);
    }

    @Test
    void rotZ90() {
        assertPoint(new Point3D(1, 0, 0).mul(new Mat4RotZ(Math.PI / 2)), 0, 1, 0);
    }

    @Test
    void rotX90() {
        assertPoint(new Point3D(0, 1, 0).mul(new Mat4RotX(Math.PI / 2)), 0, 0, 1);
    }

    @Test
    void rotY90() {
        assertPoint(new Point3D(0, 0, 1).mul(new Mat4RotY(Math.PI / 2)), 1, 0, 0);
    }

    @Test
    void scale() {
        assertPoint(new Point3D(1, 2, 3).mul(new Mat4Scale(2)), 2, 4, 6);
        assertPoint(new Point3D(1, 2, 3).mul(new Mat4Scale(2, 3, 4)), 2, 6, 12);
    }

    @Test
    void perspectiveDepthRange() {
        Mat4 p = new Mat4PerspRH(Math.PI / 2, 1.0, 1, 10);
        Point3D near = new Point3D(0, 0, -1).mul(p);
        Point3D far = new Point3D(0, 0, -10).mul(p);
        assertEquals(0, near.z() / near.w(), EPS);
        assertEquals(1, far.z() / far.w(), EPS);
        assertTrue(near.w() > 0);
    }

    @Test
    void perspectiveFovEdge() {
        double fov = Math.toRadians(60);
        Mat4 p = new Mat4PerspRH(fov, 0.5, 0.1, 100);
        double d = 5;
        Point3D top = new Point3D(0, d * Math.tan(fov / 2), -d).mul(p);
        assertEquals(1, top.y() / top.w(), EPS);
        // k = height / width: horizontal half extent is tan(fov/2) / k
        Point3D right = new Point3D(d * Math.tan(fov / 2) / 0.5, 0, -d).mul(p);
        assertEquals(1, right.x() / right.w(), EPS);
    }

    @Test
    void orthoCorners() {
        Mat4 o = new Mat4OrthoRH(20, 10, 1, 11);
        assertPoint(new Point3D(10, 5, -1).mul(o), 1, 1, 0);
        assertPoint(new Point3D(-10, -5, -11).mul(o), -1, -1, 1);
    }

    @Test
    void viewMapsEyeToOrigin() {
        Mat4 v = new Mat4ViewRH(new Vec3D(3, 4, 5), new Vec3D(1, 0, 0), new Vec3D(0, 0, 1));
        assertPoint(new Point3D(3, 4, 5).mul(v), 0, 0, 0);
    }

    @Test
    void viewPointAlongViewDirIsInFrontAndProjectable() {
        Vec3D eye = new Vec3D(1, 2, 3);
        Vec3D dir = new Vec3D(1, 1, 0);
        Mat4 v = new Mat4ViewRH(eye, dir, new Vec3D(0, 0, 1));
        Vec3D target = eye.add(dir.normalized().mul(4));
        Point3D pv = new Point3D(target.x(), target.y(), target.z()).mul(v);
        assertPoint(pv, 0, 0, -4);
        Point3D clip = pv.mul(new Mat4PerspRH(Math.PI / 3, 1, 0.1, 100));
        assertTrue(clip.w() > 0);
        assertTrue(clip.z() / clip.w() > 0 && clip.z() / clip.w() < 1);
    }
}
