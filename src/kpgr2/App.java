package kpgr2;

import javax.swing.SwingUtilities;
import kpgr2.ui.MainWindow;

/** Application entry point. */
public final class App {
    private App() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
