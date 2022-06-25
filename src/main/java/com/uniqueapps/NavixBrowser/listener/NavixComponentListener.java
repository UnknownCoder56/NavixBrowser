package com.uniqueapps.NavixBrowser.listener;

import com.uniqueapps.NavixBrowser.component.BrowserTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class NavixComponentListener extends ComponentAdapter {

    JFrame windowFrame;
    BrowserTabbedPane tabbedPane;

    public NavixComponentListener(JFrame windowFrame, BrowserTabbedPane tabbedPane) {
        this.windowFrame = windowFrame;
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        windowFrame.add(tabbedPane, BorderLayout.CENTER);
    }
}
