package dev.mil.system.mypad;

import javax.swing.SwingUtilities;

public class MyPad {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MyPadFrame());
    }
}
