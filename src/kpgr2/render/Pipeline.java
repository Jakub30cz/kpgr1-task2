package kpgr2.render;

import java.util.ArrayList;
import java.util.List;
import kpgr2.model.Solid;
import transforms.Mat4;
import transforms.Point3D;

public final class Pipeline {
    private Pipeline() {}

    /** Projects edges with the full mvp; edges with an endpoint outside clip space are dropped. */
    public static List<ScreenSegment> project(Solid s, Mat4 mvp, int width, int height, Integer colorOverride) {
        List<ScreenSegment> out = new ArrayList<>();
        List<Point3D> verts = s.getVertices();
        List<Integer> idx = s.getIndices();
        for (int e = 0; e + 1 < idx.size(); e += 2) {
            Point3D a = verts.get(idx.get(e)).mul(mvp);
            Point3D b = verts.get(idx.get(e + 1)).mul(mvp);
            if (!inside(a) || !inside(b)) continue;
            int color = colorOverride != null ? colorOverride : s.getEdgeColor(e / 2);
            out.add(new ScreenSegment(sx(a, width), sy(a, height), sx(b, width), sy(b, height), color));
        }
        return out;
    }

    private static boolean inside(Point3D p) {
        double w = p.w();
        return w > 0 && p.x() >= -w && p.x() <= w && p.y() >= -w && p.y() <= w && p.z() >= 0 && p.z() <= w;
    }

    private static int sx(Point3D p, int width) {
        return (int) Math.round((p.x() / p.w() + 1) * (width - 1) / 2.0);
    }

    private static int sy(Point3D p, int height) {
        return (int) Math.round((1 - p.y() / p.w()) * (height - 1) / 2.0);
    }
}
