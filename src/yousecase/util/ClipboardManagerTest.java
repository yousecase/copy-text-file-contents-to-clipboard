package yousecase.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ClipboardManagerTest {
    @Test
    public void setStringGetStringTest() throws IOException {
        for (int i = '\u0000'; i <= '\uffff'; i++) {
            String set = String.valueOf((char) i);
            ClipboardManager.setString(set);
            String get = ClipboardManager.getString();
            assertEquals(set, get);
        }
    }
}
