package com.uniqueapps.NavixBrowser.listener;

import org.cef.OS;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class NavixComponentListener implements ComponentListener {

    JFrame windowFrame;
    JTabbedPane tabbedPane;

    public NavixComponentListener(JFrame windowFrame, JTabbedPane tabbedPane) {
        this.windowFrame = windowFrame;
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        if (OS.isLinux()) {
            windowFrame.getContentPane().remove(tabbedPane);
            windowFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        }
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }
}
