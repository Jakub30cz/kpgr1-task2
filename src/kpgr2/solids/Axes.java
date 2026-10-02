package kpgr2.solids;

import kpgr2.model.Solid;
import transforms.Point3D;

/** Three coordinate axes from the origin; edge i runs along axis i (X, Y, Z). */
public class Axes extends Solid {
    private static final int[] EDGE_COLORS = {0xFF0000, 0x00FF00, 0x0000FF};

    public Axes(double length) {
        this("axes", length);
    }

    protected Axes(String name, double length) {
        super(name, 0xFFFFFF);
        vertices.add(new Point3D(0, 0, 0));
        vertices.add(new Point3D(length, 0, 0));
        vertices.add(new Point3D(0, length, 0));
        vertices.add(new Point3D(0, 0, length));
        for (int i = 1; i <= 3; i++) {
            indices.add(0);
            indices.add(i);
        }
    }

    @Override
    public int getEdgeColor(int edge) {
        return EDGE_COLORS[edge];
    }
}
