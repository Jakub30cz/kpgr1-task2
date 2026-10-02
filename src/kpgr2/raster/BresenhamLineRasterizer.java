package kpgr2.raster;

/**
 * Integer-only Bresenham line, valid for all 8 octants. Both endpoints are drawn, so the line has
 * max(|dx|, |dy|) + 1 pixels. The line is walked along its major axis; the error term decides
 * when the minor axis advances.
 */
public final class BresenhamLineRasterizer implements LineRasterizer {
    private final Raster raster;

    public BresenhamLineRasterizer(Raster raster) {
        this.raster = raster;
    }

    @Override
    public void rasterize(int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = Integer.compare(x2, x1);
        int sy = Integer.compare(y2, y1);
        int x = x1;
        int y = y1;
        if (dx >= dy) {
            int err = 2 * dy - dx;
            for (int i = 0; i <= dx; i++) {
                raster.setPixel(x, y, color);
                if (err > 0) {
                    y += sy;
                    err -= 2 * dx;
                }
                err += 2 * dy;
                x += sx;
            }
        } else {
            int err = 2 * dx - dy;
            for (int i = 0; i <= dy; i++) {
                raster.setPixel(x, y, color);
                if (err > 0) {
                    x += sx;
                    err -= 2 * dy;
                }
                err += 2 * dx;
                y += sy;
            }
        }
    }
}
