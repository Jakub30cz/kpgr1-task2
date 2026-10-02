package kpgr2.scene;

import java.util.ArrayList;
import java.util.List;
import kpgr2.model.Solid;
import kpgr2.model.Tessellated;
import kpgr2.solids.Axes;
import kpgr2.solids.BicubicSurface;
import kpgr2.solids.Cone;
import kpgr2.solids.ConicalSpiral;
import kpgr2.solids.CubicCurve;
import kpgr2.solids.Cylinder;
import kpgr2.solids.Gizmo;
import kpgr2.solids.Octahedron;
import kpgr2.solids.Pyramid;
import kpgr2.solids.Tetrahedron;
import kpgr2.solids.Torus;
import kpgr2.solids.VivianiCurve;
import transforms.Cubic;
import transforms.Mat4;
import transforms.Mat4RotZ;
import transforms.Mat4Transl;
import transforms.Point3D;

/** The twelve scene objects laid out on a grid, plus axes, gizmo and animation state. */
public class Scene {
    private static final double SPACING = 3;
    private static final int COLUMNS = 4;
    private static final double SPIN_RAD_PER_SEC = Math.toRadians(45);

    private final List<Solid> objects = new ArrayList<>();
    private final Axes axes = new Axes(5);
    private final Gizmo gizmo = new Gizmo();
    private final TransformController controller = new TransformController();
    private final Solid torus = new Torus(24, 12);
    private final Solid cone = new Cone(16);
    private final Solid tetrahedron = new Tetrahedron();
    private int activeIndex;
    private boolean animating;

    public Scene() {
        Solid octahedron = new Octahedron();
        objects.add(tetrahedron);
        objects.add(new Pyramid());
        objects.add(octahedron);
        objects.add(new Cylinder(16));
        objects.add(cone);
        objects.add(torus);

        // Cubic end points sit on two opposite Octahedron vertices; the curves share its model.
        Point3D a = octahedron.getVertices().get(0);
        Point3D b = octahedron.getVertices().get(1);
        Point3D[] bezier = {a, new Point3D(0.3, 0.9, 0.5), new Point3D(-0.3, -0.9, 0.5), b};
        Point3D[] ferguson = {a, b, new Point3D(0, 2, 1), new Point3D(0, -2, 1)};
        Point3D[] coons = {a, new Point3D(0.3, 0.9, 0.5), new Point3D(-0.3, -0.9, 0.5), b};
        objects.add(new CubicCurve("Bezier", Cubic.BEZIER, bezier, 0xFF4040));
        objects.add(new CubicCurve("Ferguson", Cubic.FERGUSON, ferguson, 0x40FF40));
        objects.add(new CubicCurve("Coons", Cubic.COONS, coons, 0x4080FF));
        objects.add(new VivianiCurve("Viviani", 0.6, 0xFF9040));
        objects.add(new ConicalSpiral("Conical spiral", 0.5, 1.0, 3, 0xFF40FF));
        objects.add(new BicubicSurface("Bicubic surface", BicubicSurface.defaultControlPoints(), 0x40FFFF));

        for (int i = 0; i < objects.size(); i++) {
            if (!(objects.get(i) instanceof CubicCurve)) objects.get(i).setModel(gridModel(i));
        }
        Mat4 octModel = octahedron.getModel();
        for (Solid o : objects) if (o instanceof CubicCurve) o.setModel(octModel);
    }

    private static Mat4 gridModel(int i) {
        double x = (i % COLUMNS - (COLUMNS - 1) / 2.0) * SPACING;
        double y = (1 - i / COLUMNS) * SPACING;
        return new Mat4Transl(x, y, 0);
    }

    public List<Solid> getObjects() { return objects; }

    public Axes getAxes() { return axes; }

    public Gizmo getGizmo() { return gizmo; }

    public TransformController getController() { return controller; }

    public int getActiveIndex() { return activeIndex; }

    public void setActiveIndex(int i) {
        if (i < 0 || i >= objects.size()) throw new IllegalArgumentException("index out of range: " + i);
        activeIndex = i;
    }

    public void selectNext() { activeIndex = (activeIndex + 1) % objects.size(); }

    public Solid getActive() { return objects.get(activeIndex); }

    /** Forwards the tessellation precision to every tessellated object. */
    public void setSegments(int n) {
        for (Solid o : objects) if (o instanceof Tessellated t) t.setSegments(n);
    }

    public boolean isAnimating() { return animating; }

    public void toggleAnimation() { animating = !animating; }

    /** Advances the animation by dt seconds: Torus, Cone and Tetrahedron spin locally about Z. */
    public void tick(double dt) {
        if (!animating) return;
        Mat4 rot = new Mat4RotZ(SPIN_RAD_PER_SEC * dt);
        for (Solid s : new Solid[] {torus, cone, tetrahedron}) s.setModel(rot.mul(s.getModel()));
    }
}
