package kpgr2.render;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class PickerTest {
    private static ScreenSegment seg(int x1, int y1, int x2, int y2) {
        return new ScreenSegment(x1, y1, x2, y2, 0);
    }

    @Test
    void hitsSegment() {
        assertEquals(0, Picker.pick(List.of(List.of(seg(0, 0, 10, 0))), 5, 2, 3));
    }

    @Test
    void nearerObjectWins() {
        var far = List.of(seg(0, 5, 10, 5));
        var near = List.of(seg(0, 1, 10, 1));
        assertEquals(1, Picker.pick(List.of(far, near), 5, 0, 10));
    }

    @Test
    void beyondEndpointUsesEndpointDistance() {
        // distance to (10,0) is 5, not 0
        assertEquals(-1, Picker.pick(List.of(List.of(seg(0, 0, 10, 0))), 15, 0, 3));
        assertEquals(0, Picker.pick(List.of(List.of(seg(0, 0, 10, 0))), 15, 0, 5));
    }

    @Test
    void emptyOrTooFarReturnsMinusOne() {
        assertEquals(-1, Picker.pick(List.of(), 0, 0, 10));
        assertEquals(-1, Picker.pick(List.of(List.of()), 0, 0, 10));
        assertEquals(-1, Picker.pick(List.of(List.of(seg(0, 0, 10, 0))), 5, 50, 10));
    }

    @Test
    void zeroLengthSegment() {
        assertEquals(0, Picker.pick(List.of(List.of(seg(4, 4, 4, 4))), 5, 4, 2));
        assertEquals(-1, Picker.pick(List.of(List.of(seg(4, 4, 4, 4))), 9, 4, 2));
    }
}
