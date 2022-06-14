package com.uniqueapps.NavixBrowser.listener;

import org.cef.CefApp;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Timer;
import java.util.TimerTask;

public class NavixWindowListener implements WindowListener {

    JFrame windowFrame;
    CefApp cefApp;

    public NavixWindowListener(JFrame windowFrame, CefApp cefApp) {
        this.windowFrame = windowFrame;
        this.cefApp = cefApp;
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) {

    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        cefApp.dispose();
        windowFrame.dispose();
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        System.out.println("Waiting for 5 seconds before exiting...");
        var closeTask = new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        };
        new Timer().schedule(closeTask, 5000L);
    }

    @Override
    public void windowIconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent) {

    }

    @Override
    public void windowActivated(WindowEvent windowEvent) {

    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent) {

    }
}
