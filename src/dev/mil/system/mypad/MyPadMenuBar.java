package dev.mil.system.mypad;

import javax.swing.*;

public class MyPadMenuBar extends JMenuBar {
    public MyPadMenuBar(JFrame parent) {
        JMenu helpMenu = new JMenu("Help");

        JMenuItem userGuideItem = new JMenuItem("User Guide");
        userGuideItem.addActionListener(e -> {
            new HelpDialog(parent).setVisible(true);
        });

        JMenuItem aboutItem = new JMenuItem("About MyPad");
        aboutItem.addActionListener(e -> {
            new AboutDialog(parent).setVisible(true);
        });


        helpMenu.add(userGuideItem);
        helpMenu.add(aboutItem);
        add(helpMenu);
    }
}

