package kpgr2.solids;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

public class CubicCurve extends CurvePolyline {
    private final Cubic cubic;

    public CubicCurve(String name, Mat4 basis, Point3D[] p4, int color) {
        super(name, color, 20);
        if (p4.length != 4) throw new IllegalArgumentException("4 control points required");
        this.cubic = new Cubic(basis, p4[0], p4[1], p4[2], p4[3]);
        rebuild();
    }

    @Override
    protected Point3D at(double u) { return cubic.compute(u); }
}
