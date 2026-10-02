package kpgr2.ui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import kpgr2.scene.Scene;
import kpgr2.scene.TransformController;

/** Main application window: view panel, curve precision slider and status bar. */
public class MainWindow extends JFrame {
    private static final int FRAME_MS = 16;
    private static final int SEGMENTS_MIN = 2;
    private static final int SEGMENTS_MAX = 100;
    private static final int SEGMENTS_DEFAULT = 20;

    private final ViewPanel view = new ViewPanel();
    private final JLabel status = new JLabel();

    public MainWindow() {
        super("KPGR2 - drátový model 3D scény");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Scene scene = view.getScene();

        JSlider slider = new JSlider(SEGMENTS_MIN, SEGMENTS_MAX, SEGMENTS_DEFAULT);
        slider.setFocusable(false); // keyboard focus always stays on the view, even after a plain click
        scene.setSegments(SEGMENTS_DEFAULT);
        slider.addChangeListener(e -> {
            scene.setSegments(slider.getValue());
            view.changed();
            view.requestFocusInWindow();
        });
        JPanel sliderRow = new JPanel(new BorderLayout(8, 0));
        sliderRow.add(new JLabel("Přesnost křivek"), BorderLayout.WEST);
        sliderRow.add(slider, BorderLayout.CENTER);

        status.setBorder(new EmptyBorder(2, 6, 2, 6));
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(2, 6, 2, 6));
        bottom.add(sliderRow, BorderLayout.NORTH);
        bottom.add(status, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(view, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        view.onStateChanged = this::updateStatus;
        updateStatus();

        Timer animation = new Timer(FRAME_MS, e -> {
            if (scene.isAnimating()) {
                scene.tick(FRAME_MS / 1000.0);
                view.changed();
            }
        });
        animation.start();

        setSize(1024, 768);
        setLocationRelativeTo(null);
        view.requestFocusInWindow();
    }

    private void updateStatus() {
        Scene scene = view.getScene();
        TransformController c = scene.getController();
        String mode = switch (c.getMode()) {
            case TRANSLATE -> "posun";
            case ROTATE -> "rotace";
            case SCALE -> "měřítko";
        };
        status.setText("Aktivní: " + scene.getActive().getName()
                + " | " + (view.isPerspective() ? "perspektiva" : "ortogonální")
                + " | režim: " + mode
                + " | osa: " + c.getAxis()
                + " | " + (c.isLocal() ? "lokální" : "globální")
                + " | animace: " + (scene.isAnimating() ? "zapnuta" : "vypnuta")
                + " | WSAD/QE/Shift/Ctrl kamera, myš rozhlížení, klik výběr, Tab další, T/R/F režim,"
                + " X/Y/Z osa, šipky transformace, L lokální, P projekce, Mezerník animace");
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) view.requestFocusInWindow();
    }
}
