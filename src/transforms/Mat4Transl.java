package transforms;

public class Mat4Transl extends Mat4Identity {
    public Mat4Transl(double x, double y, double z) {
        mat[3][0] = x;
        mat[3][1] = y;
        mat[3][2] = z;
    }

    public Mat4Transl(Vec3D v) {
        this(v.x(), v.y(), v.z());
    }
}
