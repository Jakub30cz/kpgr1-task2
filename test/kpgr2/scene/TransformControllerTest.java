package kpgr2.scene;

import static org.junit.jupiter.api.Assertions.*;

import kpgr2.model.Solid;
import org.junit.jupiter.api.Test;
import transforms.Mat4RotZ;
import transforms.Mat4Transl;
import transforms.Point3D;
import transforms.Vec3D;

class TransformControllerTest {
    private static final double EPS = 1e-9;

    private static class Box extends Solid {
        Box() {
            super("box", 0xFFFFFF);
            vertices.add(new Point3D(1, 1, 0));
            vertices.add(new Point3D(3, 1, 0));
            vertices.add(new Point3D(1, 3, 0));
            vertices.add(new Point3D(3, 3, 0));
        }
    }

    private static void assertVec(Vec3D e, Vec3D a) {
        assertEquals(e.x(), a.x(), EPS);
        assertEquals(e.y(), a.y(), EPS);
        assertEquals(e.z(), a.z(), EPS);
    }

    @Test
    void localIsDefaultAndToggles() {
        TransformController c = new TransformController();
        assertTrue(c.isLocal());
        c.toggleLocal();
        assertFalse(c.isLocal());
    }

    @Test
    void localTranslateAfterRotZ90MovesAlongWorldY() {
        Solid s = new Box();
        s.setModel(new Mat4RotZ(Math.PI / 2));
        Vec3D before = s.getWorldCenter();
        TransformController c = new TransformController();
        c.setMode(TransformController.Mode.TRANSLATE);
        c.setAxis(TransformController.Axis.X);
        c.apply(s, 1);
        assertVec(before.add(new Vec3D(0, 0.1, 0)), s.getWorldCenter());
    }

    @Test
    void globalTranslateMovesAlongWorldX() {
        Solid s = new Box();
        s.setModel(new Mat4RotZ(Math.PI / 2));
        Vec3D before = s.getWorldCenter();
        TransformController c = new TransformController();
        c.toggleLocal();
        c.setMode(TransformController.Mode.TRANSLATE);
        c.setAxis(TransformController.Axis.X);
        c.apply(s, 1);
        assertVec(before.add(new Vec3D(0.1, 0, 0)), s.getWorldCenter());
    }

    @Test
    void globalRotateKeepsCenter() {
        Solid s = new Box();
        s.setModel(new Mat4Transl(5, -2, 1));
        Vec3D before = s.getWorldCenter();
        TransformController c = new TransformController();
        c.toggleLocal();
        c.setMode(TransformController.Mode.ROTATE);
        c.setAxis(TransformController.Axis.Z);
        c.apply(s, 1);
        assertVec(before, s.getWorldCenter());
        // the geometry really rotated: vertex (3,1,0) no longer sits at x = 8
        assertNotEquals(8.0, s.getVertices().get(1).mul(s.getModel()).x(), 1e-6);
    }

    @Test
    void globalScaleKeepsCenter() {
        Solid s = new Box();
        s.setModel(new Mat4Transl(5, -2, 1));
        Vec3D before = s.getWorldCenter();
        TransformController c = new TransformController();
        c.toggleLocal();
        c.setMode(TransformController.Mode.SCALE);
        c.setAxis(TransformController.Axis.Y);
        c.apply(s, 1);
        assertVec(before, s.getWorldCenter());
    }

    @Test
    void localScaleKeepsCenter() {
        // local scale acts around the object's own origin, so use geometry centered on it
        Solid s = new Solid("c", 0) {
            {
                vertices.add(new Point3D(-1, 0, 0));
                vertices.add(new Point3D(1, 0, 0));
            }
        };
        s.setModel(new Mat4Transl(2, 3, 4));
        Vec3D before = s.getWorldCenter();
        TransformController c = new TransformController();
        c.setMode(TransformController.Mode.SCALE);
        c.setAxis(TransformController.Axis.X);
        c.apply(s, 1);
        assertVec(before, s.getWorldCenter());
    }

    @Test
    void plusThenMinusRestoresModel() {
        for (boolean local : new boolean[] {true, false}) {
            for (TransformController.Mode m : TransformController.Mode.values()) {
                for (TransformController.Axis a : TransformController.Axis.values()) {
                    Solid s = new Box();
                    s.setModel(new Mat4RotZ(0.7).mul(new Mat4Transl(1, 2, 3)));
                    var orig = s.getModel();
                    TransformController c = new TransformController();
                    if (!local) c.toggleLocal();
                    c.setMode(m);
                    c.setAxis(a);
                    c.apply(s, 1);
                    c.apply(s, -1);
                    assertTrue(orig.approxEquals(s.getModel(), EPS), m + " " + a + " local=" + local);
                }
            }
        }
    }
}
