package transforms;

/** First-person camera holding an orthonormal basis (forward, right, up) in a right-handed system. */
public class Camera {
    private Vec3D position;
    private Vec3D forward;
    private Vec3D right;
    private Vec3D up;

    public Camera(Vec3D pos, Vec3D forward, Vec3D up) {
        this.position = pos;
        this.forward = forward.normalized();
        this.right = this.forward.cross(up).normalized();
        this.up = this.right.cross(this.forward).normalized();
    }

    public Vec3D getPosition() {
        return position;
    }

    public Vec3D getForward() {
        return forward;
    }

    public Vec3D getRight() {
        return right;
    }

    public Vec3D getUp() {
        return up;
    }

    /** Rodrigues rotation of v about unit axis k by angle (counter-clockwise looking against the axis). */
    private static Vec3D rotate(Vec3D v, Vec3D k, double angle) {
        double c = Math.cos(angle), s = Math.sin(angle);
        return v.mul(c).add(k.cross(v).mul(s)).add(k.mul(k.dot(v) * (1 - c)));
    }

    private void orthonormalize() {
        forward = forward.normalized();
        right = forward.cross(up).normalized();
        up = right.cross(forward).normalized();
    }

    /** Rotates about the up axis; positive angle turns forward towards -right (left turn). */
    public void yaw(double angle) {
        forward = rotate(forward, up, angle);
        right = rotate(right, up, angle);
        orthonormalize();
    }

    /** Rotates about the right axis; positive angle tilts forward towards up. */
    public void pitch(double angle) {
        forward = rotate(forward, right, angle);
        up = rotate(up, right, angle);
        orthonormalize();
    }

    /** Rotates about the forward axis; positive angle maps up to right (90 degrees: new up == old right). */
    public void roll(double angle) {
        up = rotate(up, forward, angle);
        right = rotate(right, forward, angle);
        orthonormalize();
    }

    public void moveForward(double d) {
        position = position.add(forward.mul(d));
    }

    public void moveRight(double d) {
        position = position.add(right.mul(d));
    }

    public void moveUp(double d) {
        position = position.add(up.mul(d));
    }

    public Mat4 getViewMatrix() {
        return new Mat4ViewRH(position, forward, up);
    }
}
