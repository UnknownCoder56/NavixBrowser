package com.uniqueapps.NavixBrowser.handler;

import com.uniqueapps.NavixBrowser.BrowserTabbedPane;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;

public class NavixDisplayHandler extends CefDisplayHandlerAdapter {

    JFrame windowFrame;
    BrowserTabbedPane tabbedPane;
    CefApp cefApp;

    public NavixDisplayHandler(JFrame windowFrame, BrowserTabbedPane tabbedPane, CefApp cefApp) {
        this.windowFrame = windowFrame;
        this.tabbedPane = tabbedPane;
        this.cefApp = cefApp;
    }

    @Override
    public void onTitleChange(CefBrowser cefBrowser, String newTitle) {
        super.onTitleChange(cefBrowser, newTitle);
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
            }
        });
        tabPanel.add(closeTabButton);

        try {
            tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(cefBrowser.getUIComponent()), tabPanel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
