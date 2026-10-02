package kpgr2.ui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.KeyEvent;
import org.junit.jupiter.api.Test;

class ViewPanelTest {
    @Test
    void tabReachesKeyListenerAndSelectsNextObject() {
        ViewPanel panel = new ViewPanel();
        assertFalse(panel.getFocusTraversalKeysEnabled());
        int before = panel.getScene().getActiveIndex();
        KeyEvent tab = new KeyEvent(panel, KeyEvent.KEY_PRESSED, 0, 0, KeyEvent.VK_TAB, '\t');
        for (var l : panel.getKeyListeners()) l.keyPressed(tab);
        int n = panel.getScene().getObjects().size();
        assertEquals((before + 1) % n, panel.getScene().getActiveIndex());
    }
}
