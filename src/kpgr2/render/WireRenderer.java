package kpgr2.render;

import java.util.List;
import kpgr2.raster.LineRasterizer;

public class WireRenderer {
    private final LineRasterizer rasterizer;

    public WireRenderer(LineRasterizer rasterizer) {
        this.rasterizer = rasterizer;
    }

    public void render(List<ScreenSegment> segments) {
        for (ScreenSegment s : segments) rasterizer.rasterize(s.x1(), s.y1(), s.x2(), s.y2(), s.color());
    }
}
