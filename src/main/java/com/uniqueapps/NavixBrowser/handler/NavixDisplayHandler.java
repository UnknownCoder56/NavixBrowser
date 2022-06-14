package com.uniqueapps.NavixBrowser.handler;

import com.uniqueapps.NavixBrowser.BrowserTabbedPane;
import org.cef.CefApp;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class NavixDisplayHandler implements CefDisplayHandler {

    JFrame windowFrame;
    BrowserTabbedPane tabbedPane;
    CefApp cefApp;

    public NavixDisplayHandler(JFrame windowFrame, BrowserTabbedPane tabbedPane, CefApp cefApp) {
        this.windowFrame = windowFrame;
        this.tabbedPane = tabbedPane;
        this.cefApp = cefApp;
    }

    @Override
    public void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String s) {

    }

    @Override
    public void onTitleChange(CefBrowser cefBrowser, String newTitle) {
        JPanel tabPanel = new JPanel();
        JLabel tabInfoLabel = new JLabel(newTitle.length() > 10 ? newTitle.substring(0, 7) + "..." : newTitle);
        try {
            tabInfoLabel.setIcon(new ImageIcon(ImageIO.read(new URL("https://www.google.com/s2/favicons?domain=" + cefBrowser.getURL()))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabPanel.add(tabInfoLabel);
        JButton closeTabButton = new JButton("X");
        closeTabButton.addActionListener(l -> {
            if (tabbedPane.getTabCount() > 2) {
                tabbedPane.removeBrowserTab(tabbedPane.indexOfComponent(cefBrowser.getUIComponent()));
            } else {
                windowFrame.setVisible(false);
                cefApp.dispose();
                windowFrame.dispose();
                System.out.println("Waiting for 5 seconds before exiting...");
                var closeTask = new TimerTask() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                };
                new Timer().schedule(closeTask, 5000L);
            }
        });
        tabPanel.add(closeTabButton);

        try {
            tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(cefBrowser.getUIComponent()), tabPanel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTooltip(CefBrowser cefBrowser, String s) {
        return false;
    }

    @Override
    public void onStatusMessage(CefBrowser cefBrowser, String s) {

    }

    @Override
    public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity, String s, String s1, int i) {
        return false;
    }

    @Override
    public boolean onCursorChange(CefBrowser cefBrowser, int i) {
        return false;
    }
}
