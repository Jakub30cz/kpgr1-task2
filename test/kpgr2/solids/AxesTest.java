package kpgr2.solids;

import static org.junit.jupiter.api.Assertions.assertEquals;

import kpgr2.model.Solid;
import org.junit.jupiter.api.Test;
import transforms.Point3D;

class AxesTest {
    @Test
    void countsAndColors() {
        Axes a = new Axes(2);
        assertEquals(4, a.getVertices().size());
        assertEquals(6, a.getIndices().size());
        assertEquals(0xFF0000, a.getEdgeColor(0));
        assertEquals(0x00FF00, a.getEdgeColor(1));
        assertEquals(0x0000FF, a.getEdgeColor(2));
    }

    @Test
    void edgeIAlongAxisI() {
        Axes a = new Axes(2);
        for (int i = 0; i < 3; i++) {
            Point3D p = a.getVertices().get(a.getIndices().get(2 * i));
            Point3D q = a.getVertices().get(a.getIndices().get(2 * i + 1));
            double[] d = {q.x() - p.x(), q.y() - p.y(), q.z() - p.z()};
            for (int k = 0; k < 3; k++) assertEquals(k == i ? 2.0 : 0.0, d[k], 1e-9);
        }
    }

    @Test
    void gizmo() {
        Solid g = new Gizmo();
        assertEquals("gizmo", g.getName());
        assertEquals(4, g.getVertices().size());
        assertEquals(0.7, g.getVertices().get(1).x(), 1e-9);
    }
}
