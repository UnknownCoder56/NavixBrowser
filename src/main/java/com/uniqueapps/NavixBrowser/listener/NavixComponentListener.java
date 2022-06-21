package com.uniqueapps.NavixBrowser.listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class NavixComponentListener extends ComponentAdapter {

    JFrame windowFrame;
    JTabbedPane tabbedPane;

    public NavixComponentListener(JFrame windowFrame, JTabbedPane tabbedPane) {
        this.windowFrame = windowFrame;
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        windowFrame.add(tabbedPane, BorderLayout.CENTER);
    }
}
