package kpgr2.solids;

import transforms.Point3D;

/** Viviani's curve on a sphere of radius r: (r cos^2 t, r cos t sin t, r sin t), t in [0, 2 pi]. */
public class VivianiCurve extends CurvePolyline {
    private final double r;

    public VivianiCurve(String name, double r, int color) {
        super(name, color, 20);
        this.r = r;
        rebuild();
    }

    @Override
    protected Point3D at(double u) {
        double t = u * 2 * Math.PI;
        return new Point3D(r * Math.cos(t) * Math.cos(t), r * Math.cos(t) * Math.sin(t), r * Math.sin(t));
    }
}
