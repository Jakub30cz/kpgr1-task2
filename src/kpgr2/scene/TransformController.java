package kpgr2.scene;

import kpgr2.model.Solid;
import transforms.Mat4;
import transforms.Mat4RotX;
import transforms.Mat4RotY;
import transforms.Mat4RotZ;
import transforms.Mat4Scale;
import transforms.Mat4Transl;
import transforms.Vec3D;

/** Applies translate / rotate / scale steps to a solid, in its local or the global frame. */
public class TransformController {
    public enum Mode { TRANSLATE, ROTATE, SCALE }

    public enum Axis { X, Y, Z }

    private static final double MOVE_STEP = 0.1;
    private static final double ROT_STEP = Math.toRadians(5);
    private static final double SCALE_STEP = 1.05;

    private Mode mode = Mode.TRANSLATE;
    private Axis axis = Axis.X;
    private boolean local = true;

    public Mode getMode() { return mode; }

    public void setMode(Mode mode) { this.mode = mode; }

    public Axis getAxis() { return axis; }

    public void setAxis(Axis axis) { this.axis = axis; }

    public boolean isLocal() { return local; }

    public void toggleLocal() { local = !local; }

    /** Applies one step; sign +1 forward, -1 is the exact inverse step. */
    public void apply(Solid s, int sign) {
        Mat4 t = step(sign);
        if (local) {
            s.setModel(t.mul(s.getModel()));
        } else if (mode == Mode.TRANSLATE) {
            s.setModel(s.getModel().mul(t));
        } else {
            Vec3D c = s.getWorldCenter();
            Mat4 pivoted = new Mat4Transl(-c.x(), -c.y(), -c.z()).mul(t).mul(new Mat4Transl(c));
            s.setModel(s.getModel().mul(pivoted));
        }
    }

    private Mat4 step(int sign) {
        double dir = sign >= 0 ? 1 : -1;
        switch (mode) {
            case TRANSLATE: {
                double d = dir * MOVE_STEP;
                return new Mat4Transl(axis == Axis.X ? d : 0, axis == Axis.Y ? d : 0, axis == Axis.Z ? d : 0);
            }
            case ROTATE: {
                double a = dir * ROT_STEP;
                return axis == Axis.X ? new Mat4RotX(a) : axis == Axis.Y ? new Mat4RotY(a) : new Mat4RotZ(a);
            }
            default: {
                double k = dir > 0 ? SCALE_STEP : 1.0 / SCALE_STEP;
                return new Mat4Scale(axis == Axis.X ? k : 1, axis == Axis.Y ? k : 1, axis == Axis.Z ? k : 1);
            }
        }
    }
}
