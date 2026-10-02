package transforms;

/** Right-handed view matrix: eye goes to the origin, viewDir becomes -Z, up becomes +Y. */
public class Mat4ViewRH extends Mat4 {
    public Mat4ViewRH(Vec3D eye, Vec3D viewDir, Vec3D up) {
        super();
        Vec3D z = viewDir.mul(-1).normalized();
        Vec3D x = up.cross(z).normalized();
        Vec3D y = z.cross(x);
        mat[0][0] = x.x();
        mat[1][0] = x.y();
        mat[2][0] = x.z();
        mat[3][0] = -eye.dot(x);
        mat[0][1] = y.x();
        mat[1][1] = y.y();
        mat[2][1] = y.z();
        mat[3][1] = -eye.dot(y);
        mat[0][2] = z.x();
        mat[1][2] = z.y();
        mat[2][2] = z.z();
        mat[3][2] = -eye.dot(z);
        mat[3][3] = 1;
    }
}
