package kpgr2.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;
import kpgr2.model.Solid;
import kpgr2.raster.BresenhamLineRasterizer;
import kpgr2.raster.RasterBufferedImage;
import kpgr2.render.Pipeline;
import kpgr2.render.Picker;
import kpgr2.render.ScreenSegment;
import kpgr2.render.WireRenderer;
import kpgr2.scene.Scene;
import kpgr2.scene.TransformController.Axis;
import kpgr2.scene.TransformController.Mode;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4OrthoRH;
import transforms.Mat4PerspRH;
import transforms.Mat4Transl;
import transforms.Vec3D;

/** Panel that renders the scene as wireframe and handles camera, selection and transform input. */
public class ViewPanel extends JPanel {
    private static final int BACKGROUND = 0x000000;
    private static final int ACTIVE_COLOR = 0xFFFF00;
    private static final double FOV_Y = Math.toRadians(60);
    private static final double Z_NEAR = 0.1;
    private static final double Z_FAR = 100;
    private static final double ORTHO_WIDTH = 20;
    private static final double CLICK_THRESHOLD_PX = 3;
    private static final double PICK_RADIUS_PX = 6;
    private static final double MOUSE_SENSITIVITY = 0.005;
    private static final double MOVE_SPEED = 0.15;
    private static final double ROLL_SPEED = Math.toRadians(1.5);
    private static final int FRAME_MS = 16;

    private final Scene scene = new Scene();
    private final Camera camera = new Camera(new Vec3D(-8, -8, 6), new Vec3D(8, 8, -6), new Vec3D(0, 0, 1));
    private final Set<Integer> held = new HashSet<>();
    private RasterBufferedImage raster = new RasterBufferedImage(1, 1);
    private boolean perspective = true;

    /** Invoked whenever scene, camera or mode state changes (for the status bar). */
    public Runnable onStateChanged = () -> {};

    private int pressX;
    private int pressY;
    private int lastX;
    private int lastY;
    private boolean dragging;

    public ViewPanel() {
        setPreferredSize(new Dimension(900, 650));
        setFocusable(true);
        setFocusTraversalKeysEnabled(false); // let Tab reach keyPressed instead of moving focus
        setBackground(Color.BLACK);
        resizeRaster();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeRaster();
                repaint();
            }
        });
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                pressX = lastX = e.getX();
                pressY = lastY = e.getY();
                dragging = false;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!dragging && Math.hypot(e.getX() - pressX, e.getY() - pressY) >= CLICK_THRESHOLD_PX) {
                    dragging = true;
                }
                if (dragging) {
                    camera.yaw(-(e.getX() - lastX) * MOUSE_SENSITIVITY);
                    camera.pitch(-(e.getY() - lastY) * MOUSE_SENSITIVITY);
                    changed();
                }
                lastX = e.getX();
                lastY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!dragging) pickAt(e.getX(), e.getY());
                dragging = false;
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                held.add(e.getKeyCode());
                handleKey(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                held.remove(e.getKeyCode());
            }
        });
        // a key released while another component/window has focus never reaches us, so drop held keys
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                held.clear();
            }
        });
        Timer timer = new Timer(FRAME_MS, e -> applyHeldKeys());
        timer.start();
    }

    public Scene getScene() { return scene; }

    public boolean isPerspective() { return perspective; }

    /** Number of movement keys currently considered held. */
    int heldKeyCount() { return held.size(); }

    public Camera getCamera() { return camera; }

    /** Repaints and notifies the listener; call after any external state change. */
    public void changed() {
        repaint();
        onStateChanged.run();
    }

    private void resizeRaster() {
        int w = Math.max(1, getWidth() > 0 ? getWidth() : getPreferredSize().width);
        int h = Math.max(1, getHeight() > 0 ? getHeight() : getPreferredSize().height);
        if (raster.getWidth() != w || raster.getHeight() != h) raster = new RasterBufferedImage(w, h);
    }

    private Mat4 projection() {
        int w = raster.getWidth();
        int h = raster.getHeight();
        double aspect = (double) h / w;
        return perspective
                ? new Mat4PerspRH(FOV_Y, aspect, Z_NEAR, Z_FAR)
                : new Mat4OrthoRH(ORTHO_WIDTH, ORTHO_WIDTH * aspect, Z_NEAR, Z_FAR);
    }

    /** Projected segments per scene object (active one in yellow), shared by drawing and picking. */
    private List<List<ScreenSegment>> projectObjects(Mat4 viewProj) {
        List<List<ScreenSegment>> result = new ArrayList<>();
        List<Solid> objects = scene.getObjects();
        Solid active = scene.getActive();
        for (Solid s : objects) {
            Integer override = s == active ? ACTIVE_COLOR : null;
            result.add(Pipeline.project(s, s.getModel().mul(viewProj), raster.getWidth(), raster.getHeight(), override));
        }
        return result;
    }

    private void redraw() {
        raster.clear(BACKGROUND);
        int w = raster.getWidth();
        int h = raster.getHeight();
        WireRenderer renderer = new WireRenderer(new BresenhamLineRasterizer(raster));
        Mat4 viewProj = camera.getViewMatrix().mul(projection());
        renderer.render(Pipeline.project(scene.getAxes(), viewProj, w, h, null));
        for (List<ScreenSegment> segs : projectObjects(viewProj)) renderer.render(segs);
        Mat4 gizmoModel = new Mat4Transl(scene.getActive().getWorldCenter());
        renderer.render(Pipeline.project(scene.getGizmo(), gizmoModel.mul(viewProj), w, h, null));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        redraw();
        g.drawImage(raster.getImage(), 0, 0, null);
    }

    private void pickAt(int x, int y) {
        Mat4 viewProj = camera.getViewMatrix().mul(projection());
        int hit = Picker.pick(projectObjects(viewProj), x, y, PICK_RADIUS_PX);
        if (hit >= 0) {
            scene.setActiveIndex(hit);
            changed();
        }
    }

    private void handleKey(int code) {
        var controller = scene.getController();
        switch (code) {
            case KeyEvent.VK_TAB -> scene.selectNext();
            case KeyEvent.VK_T -> controller.setMode(Mode.TRANSLATE);
            case KeyEvent.VK_R -> controller.setMode(Mode.ROTATE);
            case KeyEvent.VK_F -> controller.setMode(Mode.SCALE);
            case KeyEvent.VK_X -> controller.setAxis(Axis.X);
            case KeyEvent.VK_Y -> controller.setAxis(Axis.Y);
            case KeyEvent.VK_Z -> controller.setAxis(Axis.Z);
            case KeyEvent.VK_UP, KeyEvent.VK_RIGHT -> controller.apply(scene.getActive(), 1);
            case KeyEvent.VK_DOWN, KeyEvent.VK_LEFT -> controller.apply(scene.getActive(), -1);
            case KeyEvent.VK_L -> controller.toggleLocal();
            case KeyEvent.VK_P -> perspective = !perspective;
            case KeyEvent.VK_SPACE -> scene.toggleAnimation();
            default -> { return; }
        }
        changed();
    }

    private void applyHeldKeys() {
        boolean moved = false;
        if (held.contains(KeyEvent.VK_W)) { camera.moveForward(MOVE_SPEED); moved = true; }
        if (held.contains(KeyEvent.VK_S)) { camera.moveForward(-MOVE_SPEED); moved = true; }
        if (held.contains(KeyEvent.VK_D)) { camera.moveRight(MOVE_SPEED); moved = true; }
        if (held.contains(KeyEvent.VK_A)) { camera.moveRight(-MOVE_SPEED); moved = true; }
        if (held.contains(KeyEvent.VK_SHIFT)) { camera.moveUp(MOVE_SPEED); moved = true; }
        if (held.contains(KeyEvent.VK_CONTROL)) { camera.moveUp(-MOVE_SPEED); moved = true; }
        if (held.contains(KeyEvent.VK_E)) { camera.roll(ROLL_SPEED); moved = true; }
        if (held.contains(KeyEvent.VK_Q)) { camera.roll(-ROLL_SPEED); moved = true; }
        if (moved) changed();
    }
}
