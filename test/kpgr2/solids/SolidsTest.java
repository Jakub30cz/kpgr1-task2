package kpgr2.solids;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import kpgr2.model.Solid;
import org.junit.jupiter.api.Test;

class SolidsTest {
    private static void check(Solid s, int vertices, int edges) {
        assertEquals(vertices, s.getVertices().size(), s.getName() + " vertices");
        assertEquals(2 * edges, s.getIndices().size(), s.getName() + " edges");
        Set<Long> seen = new HashSet<>();
        for (int e = 0; e < edges; e++) {
            int a = s.getIndices().get(2 * e);
            int b = s.getIndices().get(2 * e + 1);
            assertTrue(a >= 0 && a < vertices && b >= 0 && b < vertices, "index range");
            assertNotEquals(a, b, "degenerate edge");
            long key = (long) Math.min(a, b) * vertices + Math.max(a, b);
            assertTrue(seen.add(key), "duplicate edge " + a + "-" + b);
        }
    }

    @Test void tetrahedron() { check(new Tetrahedron(), 4, 6); }
    @Test void pyramid() { check(new Pyramid(), 5, 8); }
    @Test void octahedron() { check(new Octahedron(), 6, 12); }
    @Test void cylinder() { check(new Cylinder(8), 16, 24); }
    @Test void cone() { check(new Cone(8), 9, 16); }
    @Test void torus() { check(new Torus(12, 6), 72, 144); }

    @Test void colorsDistinctAndAllowed() {
        Solid[] all = {new Tetrahedron(), new Pyramid(), new Octahedron(),
                new Cylinder(8), new Cone(8), new Torus(12, 6)};
        Set<Integer> colors = new HashSet<>();
        for (Solid s : all) {
            int c = s.getColor() & 0xFFFFFF;
            assertTrue(colors.add(c), "duplicate color");
            assertNotEquals(0xFFFF00, c);
            assertNotEquals(0xFF0000, c);
            assertNotEquals(0x00FF00, c);
            assertNotEquals(0x0000FF, c);
        }
    }
}
