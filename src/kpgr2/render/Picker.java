package kpgr2.render;

import java.util.List;

/** Mouse picking over projected screen segments. */
public final class Picker {
    private Picker() {}

    /** Returns the index of the object owning the nearest segment within maxDistPx, or -1. */
    public static int pick(List<List<ScreenSegment>> perObject, int mx, int my, double maxDistPx) {
        int best = -1;
        double bestDist = Double.MAX_VALUE;
        for (int i = 0; i < perObject.size(); i++) {
            for (ScreenSegment s : perObject.get(i)) {
                double d = distance(s, mx, my);
                if (d <= maxDistPx && d < bestDist) {
                    bestDist = d;
                    best = i;
                }
            }
        }
        return best;
    }

    private static double distance(ScreenSegment s, int px, int py) {
        double dx = s.x2() - s.x1();
        double dy = s.y2() - s.y1();
        double len2 = dx * dx + dy * dy;
        double t = len2 == 0 ? 0 : ((px - s.x1()) * dx + (py - s.y1()) * dy) / len2;
        t = Math.max(0, Math.min(1, t));
        return Math.hypot(px - (s.x1() + t * dx), py - (s.y1() + t * dy));
    }
}
