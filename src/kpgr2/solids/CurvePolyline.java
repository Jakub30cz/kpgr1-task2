package kpgr2.solids;

import kpgr2.model.Solid;
import kpgr2.model.Tessellated;
import transforms.Point3D;

/** Base for open curves sampled at n segments: n + 1 vertices, n edges. */
abstract class CurvePolyline extends Solid implements Tessellated {
    private int segments;

    protected CurvePolyline(String name, int color, int segments) {
        super(name, color);
        this.segments = segments;
    }

    /** Point of the curve for parameter u in [0, 1]. */
    protected abstract Point3D at(double u);

    protected final void rebuild() {
        vertices.clear();
        indices.clear();
        for (int i = 0; i <= segments; i++) vertices.add(at((double) i / segments));
        for (int i = 0; i < segments; i++) {
            indices.add(i);
            indices.add(i + 1);
        }
    }

    @Override
    public void setSegments(int n) {
        if (n < 1) throw new IllegalArgumentException("segments must be >= 1");
        this.segments = n;
        rebuild();
    }
}
