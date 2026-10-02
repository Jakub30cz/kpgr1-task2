package transforms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kpgr2.model.Solid;
import org.junit.jupiter.api.Test;

class CoreMathTest {
    @Test
    void knownProduct() {
        double[][] a = new double[4][4];
        double[][] b = new double[4][4];
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++) {
                a[r][c] = r + c;
                b[r][c] = r * 4 + c + 1;
            }
        Mat4 p = new Mat4(a).mul(new Mat4(b));
        // row 0 of a = [0,1,2,3]; column 0 of b = [1,5,9,13] -> 0+5+18+39 = 62
        assertEquals(62, p.get(0, 0), 1e-12);
        // row 3 of a = [3,4,5,6]; column 3 of b = [4,8,12,16] -> 12+32+60+96 = 200
        assertEquals(200, p.get(3, 3), 1e-12);
    }

    @Test
    void identityIsNeutral() {
        Mat4 m = new Mat4(new double[][] {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}});
        assertTrue(m.mul(new Mat4Identity()).approxEquals(m, 1e-12));
        assertTrue(new Mat4Identity().mul(m).approxEquals(m, 1e-12));
        assertEquals(m.get(0, 3), m.transpose().get(3, 0), 0);
    }

    @Test
    void pointTimesIdentity() {
        Point3D p = new Point3D(1, 2, 3).mul(new Mat4Identity());
        assertEquals(1, p.x(), 1e-12);
        assertEquals(2, p.y(), 1e-12);
        assertEquals(3, p.z(), 1e-12);
        assertEquals(1, p.w(), 1e-12);
    }

    @Test
    void dehomogDividesByW() {
        Vec3D v = new Point3D(2, 4, 6, 2).dehomog();
        assertEquals(1, v.x(), 1e-12);
        assertEquals(2, v.y(), 1e-12);
        assertEquals(3, v.z(), 1e-12);
    }

    @Test
    void crossXYisZ() {
        Vec3D z = new Vec3D(1, 0, 0).cross(new Vec3D(0, 1, 0));
        assertEquals(0, z.x(), 1e-12);
        assertEquals(0, z.y(), 1e-12);
        assertEquals(1, z.z(), 1e-12);
    }

    @Test
    void solidWorldCenter() {
        Solid s = new Solid("stub", 0xFF0000) {
            {
                vertices.add(new Point3D(0, 0, 0));
                vertices.add(new Point3D(2, 4, 6));
            }
        };
        Vec3D c = s.getWorldCenter();
        assertEquals(1, c.x(), 1e-12);
        assertEquals(2, c.y(), 1e-12);
        assertEquals(3, c.z(), 1e-12);
        assertEquals(0xFF0000, s.getEdgeColor(0));
        assertEquals(List.of(), s.getIndices());
    }
}
