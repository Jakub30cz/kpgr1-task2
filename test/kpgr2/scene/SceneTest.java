package kpgr2.scene;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import kpgr2.model.Solid;
import kpgr2.model.Tessellated;
import kpgr2.solids.BicubicSurface;
import org.junit.jupiter.api.Test;
import transforms.Point3D;
import transforms.Vec3D;

class SceneTest {
    private static Solid byName(Scene s, String name) {
        return s.getObjects().stream().filter(o -> o.getName().equals(name)).findFirst().orElseThrow();
    }

    @Test
    void hasTwelveUniquelyNamedObjects() {
        Scene s = new Scene();
        assertEquals(12, s.getObjects().size());
        Set<String> names = new HashSet<>();
        for (Solid o : s.getObjects()) names.add(o.getName());
        assertEquals(12, names.size());
    }

    @Test
    void groupsAndForbiddenNames() {
        Scene s = new Scene();
        long solids = s.getObjects().stream().filter(o -> !(o instanceof Tessellated)).count();
        long curves = s.getObjects().stream()
                .filter(o -> o instanceof Tessellated && !(o instanceof BicubicSurface)).count();
        assertTrue(solids >= 3);
        assertTrue(curves >= 3);
        for (Solid o : s.getObjects()) {
            String n = o.getName().toLowerCase();
            assertFalse(n.contains("cube") || n.contains("arrow") || n.contains("triangle"), n);
        }
    }

    @Test
    void worldCentersAreDistinct() {
        Scene s = new Scene();
        List<Solid> o = s.getObjects();
        for (int i = 0; i < o.size(); i++)
            for (int j = i + 1; j < o.size(); j++) {
                Vec3D a = o.get(i).getWorldCenter(), b = o.get(j).getWorldCenter();
                assertTrue(a.sub(b).length() > 1e-6, o.get(i).getName() + " vs " + o.get(j).getName());
            }
    }

    @Test
    void bezierEndpointsMatchOctahedronVertices() {
        Scene s = new Scene();
        Solid oct = byName(s, "Octahedron");
        Solid bez = byName(s, "Bezier");
        List<Point3D> bv = bez.getVertices();
        Vec3D start = bv.get(0).mul(bez.getModel()).dehomog();
        Vec3D end = bv.get(bv.size() - 1).mul(bez.getModel()).dehomog();
        boolean foundStart = false, foundEnd = false;
        for (Point3D v : oct.getVertices()) {
            Vec3D w = v.mul(oct.getModel()).dehomog();
            if (w.sub(start).length() < 1e-9) foundStart = true;
            if (w.sub(end).length() < 1e-9) foundEnd = true;
        }
        assertTrue(foundStart && foundEnd);
    }

    @Test
    void setSegmentsChangesCurve() {
        Scene s = new Scene();
        Solid bez = byName(s, "Bezier");
        int before = bez.getVertices().size();
        s.setSegments(50);
        assertNotEquals(before, bez.getVertices().size());
    }

    @Test
    void tickRotatesOnlyWhenAnimating() {
        Scene s = new Scene();
        Solid torus = byName(s, "Torus");
        Solid cyl = byName(s, "Cylinder");
        var torusBefore = torus.getModel();
        var cylBefore = cyl.getModel();
        s.tick(0.5);
        assertTrue(torus.getModel().approxEquals(torusBefore, 1e-12));
        s.toggleAnimation();
        s.tick(0.5);
        assertFalse(torus.getModel().approxEquals(torusBefore, 1e-6));
        assertTrue(cyl.getModel().approxEquals(cylBefore, 1e-12));
    }

    @Test
    void activeIndexWraps() {
        Scene s = new Scene();
        s.setActiveIndex(11);
        s.selectNext();
        assertEquals(0, s.getActiveIndex());
        assertNotNull(s.getController());
    }

    private static double rgbDistance(int a, int b) {
        int dr = ((a >> 16) & 0xFF) - ((b >> 16) & 0xFF);
        int dg = ((a >> 8) & 0xFF) - ((b >> 8) & 0xFF);
        int db = (a & 0xFF) - (b & 0xFF);
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    @Test
    void objectColorsAreDistinctFromActiveYellowAndAxisColors() {
        int[] reserved = {0xFFFF00, 0xFF0000, 0x00FF00, 0x0000FF};
        for (Solid o : new Scene().getObjects()) {
            for (int r : reserved) {
                assertTrue(rgbDistance(o.getColor(), r) > 80,
                        o.getName() + " color too close to " + Integer.toHexString(r));
            }
        }
    }
}
