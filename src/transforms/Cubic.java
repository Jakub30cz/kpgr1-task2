package transforms;

/** Cubic curve segment defined by a basis matrix and four control points: [t^3 t^2 t 1] * basis * [P]. */
public class Cubic {
    public static final Mat4 BEZIER = new Mat4(new double[][] {
        {-1, 3, -3, 1},
        {3, -6, 3, 0},
        {-3, 3, 0, 0},
        {1, 0, 0, 0}
    });

    /** Control order: P0, P1 (end point), R0 (start tangent), R1 (end tangent). */
    public static final Mat4 FERGUSON = new Mat4(new double[][] {
        {2, -2, 1, 1},
        {-3, 3, -2, -1},
        {0, 0, 1, 0},
        {1, 0, 0, 0}
    });

    /** Uniform cubic B-spline. */
    public static final Mat4 COONS = new Mat4(new double[][] {
        {-1 / 6.0, 3 / 6.0, -3 / 6.0, 1 / 6.0},
        {3 / 6.0, -6 / 6.0, 3 / 6.0, 0},
        {-3 / 6.0, 0, 3 / 6.0, 0},
        {1 / 6.0, 4 / 6.0, 1 / 6.0, 0}
    });

    private final Mat4 basis;
    private final Point3D[] p;

    public Cubic(Mat4 basis, Point3D p0, Point3D p1, Point3D p2, Point3D p3) {
        this.basis = basis;
        this.p = new Point3D[] {p0, p1, p2, p3};
    }

    /** Weights [t^3 t^2 t 1] * basis, one per control point. */
    static double[] weights(Mat4 basis, double t) {
        double[] tv = {t * t * t, t * t, t, 1};
        double[] w = new double[4];
        for (int j = 0; j < 4; j++)
            for (int k = 0; k < 4; k++) w[j] += tv[k] * basis.get(k, j);
        return w;
    }

    public Point3D compute(double t) {
        double[] w = weights(basis, t);
        double x = 0, y = 0, z = 0;
        for (int j = 0; j < 4; j++) {
            x += w[j] * p[j].x();
            y += w[j] * p[j].y();
            z += w[j] * p[j].z();
        }
        return new Point3D(x, y, z);
    }
}
