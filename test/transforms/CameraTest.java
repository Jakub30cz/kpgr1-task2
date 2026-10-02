package transforms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

class CameraTest {
    private static final double EPS = 1e-9;

    private static void assertVec(Vec3D expected, Vec3D actual) {
        assertEquals(expected.x(), actual.x(), EPS);
        assertEquals(expected.y(), actual.y(), EPS);
        assertEquals(expected.z(), actual.z(), EPS);
    }

    @Test
    void orthonormalAfterRandomRotations() {
        Camera c = new Camera(new Vec3D(1, 2, 3), new Vec3D(1, 1, 0), new Vec3D(0, 0, 1));
        Random rnd = new Random(42);
        for (int i = 0; i < 1000; i++) {
            double a = (rnd.nextDouble() - 0.5) * 2;
            switch (rnd.nextInt(3)) {
                case 0 -> c.yaw(a);
                case 1 -> c.pitch(a);
                default -> c.roll(a);
            }
        }
        Vec3D f = c.getForward(), r = c.getRight(), u = c.getUp();
        assertEquals(1, f.length(), EPS);
        assertEquals(1, r.length(), EPS);
        assertEquals(1, u.length(), EPS);
        assertEquals(0, f.dot(r), EPS);
        assertEquals(0, f.dot(u), EPS);
        assertEquals(0, r.dot(u), EPS);
    }

    @Test
    void roll90MapsUpToOldRight() {
        // Right-handed roll about forward by +90 degrees: new up == old right.
        Camera c = new Camera(new Vec3D(0, 0, 0), new Vec3D(0, 0, -1), new Vec3D(0, 1, 0));
        Vec3D oldRight = c.getRight();
        c.roll(Math.PI / 2);
        assertVec(oldRight, c.getUp());
    }

    @Test
    void moveForwardAlongForward() {
        Camera c = new Camera(new Vec3D(1, 1, 1), new Vec3D(0, 0, -1), new Vec3D(0, 1, 0));
        c.moveForward(2.5);
        assertVec(new Vec3D(1, 1, -1.5), c.getPosition());
    }

    @Test
    void moveRightAndUp() {
        Camera c = new Camera(new Vec3D(0, 0, 0), new Vec3D(0, 0, -1), new Vec3D(0, 1, 0));
        c.moveRight(2);
        c.moveUp(3);
        assertVec(new Vec3D(2, 3, 0), c.getPosition());
    }

    @Test
    void viewMapsPositionToOrigin() {
        Camera c = new Camera(new Vec3D(4, -2, 7), new Vec3D(1, 2, 3), new Vec3D(0, 1, 0));
        c.yaw(0.3);
        c.pitch(-0.2);
        Vec3D p = c.getPosition();
        Vec3D o = new Point3D(p.x(), p.y(), p.z()).mul(c.getViewMatrix()).dehomog();
        assertVec(new Vec3D(0, 0, 0), o);
    }
}
