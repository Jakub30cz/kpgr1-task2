package transforms;

public record Vec3D(double x, double y, double z) {
    public Vec3D add(Vec3D o) {
        return new Vec3D(x + o.x, y + o.y, z + o.z);
    }

    public Vec3D sub(Vec3D o) {
        return new Vec3D(x - o.x, y - o.y, z - o.z);
    }

    public Vec3D mul(double k) {
        return new Vec3D(x * k, y * k, z * k);
    }

    public double dot(Vec3D o) {
        return x * o.x + y * o.y + z * o.z;
    }

    public Vec3D cross(Vec3D o) {
        return new Vec3D(y * o.z - z * o.y, z * o.x - x * o.z, x * o.y - y * o.x);
    }

    public double length() {
        return Math.sqrt(dot(this));
    }

    public Vec3D normalized() {
        double l = length();
        return l == 0 ? this : mul(1.0 / l);
    }
}
