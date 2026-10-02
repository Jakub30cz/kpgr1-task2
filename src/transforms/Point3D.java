package transforms;

public record Point3D(double x, double y, double z, double w) {
    public Point3D(double x, double y, double z) {
        this(x, y, z, 1);
    }

    /** Row vector times matrix. */
    public Point3D mul(Mat4 m) {
        return new Point3D(
                x * m.get(0, 0) + y * m.get(1, 0) + z * m.get(2, 0) + w * m.get(3, 0),
                x * m.get(0, 1) + y * m.get(1, 1) + z * m.get(2, 1) + w * m.get(3, 1),
                x * m.get(0, 2) + y * m.get(1, 2) + z * m.get(2, 2) + w * m.get(3, 2),
                x * m.get(0, 3) + y * m.get(1, 3) + z * m.get(2, 3) + w * m.get(3, 3));
    }

    public Vec3D dehomog() {
        return new Vec3D(x / w, y / w, z / w);
    }

    public Vec3D toVec3D() {
        return new Vec3D(x, y, z);
    }
}
