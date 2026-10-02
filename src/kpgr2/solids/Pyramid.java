package kpgr2.solids;

import kpgr2.model.Solid;
import transforms.Point3D;

public class Pyramid extends Solid {
    public Pyramid() {
        super("Pyramid", 0x00CED1);
        vertices.add(new Point3D(-0.5, -0.5, -0.5));
        vertices.add(new Point3D(0.5, -0.5, -0.5));
        vertices.add(new Point3D(0.5, 0.5, -0.5));
        vertices.add(new Point3D(-0.5, 0.5, -0.5));
        vertices.add(new Point3D(0, 0, 0.5));
        for (int i = 0; i < 4; i++) {
            indices.add(i);
            indices.add((i + 1) % 4);
            indices.add(i);
            indices.add(4);
        }
    }
}
