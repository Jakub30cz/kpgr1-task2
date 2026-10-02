package kpgr2.raster;

public interface Raster {
    int getWidth();

    int getHeight();

    /** Sets a pixel; out-of-range coordinates are ignored. */
    void setPixel(int x, int y, int color);

    void clear(int color);
}
