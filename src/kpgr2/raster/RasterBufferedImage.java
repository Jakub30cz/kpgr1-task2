package kpgr2.raster;

import java.awt.image.BufferedImage;

/** Raster backed by a TYPE_INT_RGB BufferedImage. */
public final class RasterBufferedImage implements Raster {
    private final BufferedImage image;

    public RasterBufferedImage(int width, int height) {
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return;
        }
        image.setRGB(x, y, color);
    }

    @Override
    public void clear(int color) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, color);
            }
        }
    }
}
