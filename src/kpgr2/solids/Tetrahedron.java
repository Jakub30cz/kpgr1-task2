package kpgr2.solids;

import kpgr2.model.Solid;
import transforms.Point3D;

public class Tetrahedron extends Solid {
    public Tetrahedron() {
        super("Tetrahedron", 0xFF8C00);
        double a = 0.5;
        vertices.add(new Point3D(a, a, a));
        vertices.add(new Point3D(a, -a, -a));
        vertices.add(new Point3D(-a, a, -a));
        vertices.add(new Point3D(-a, -a, a));
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                indices.add(i);
                indices.add(j);
            }
        }
    }
}
