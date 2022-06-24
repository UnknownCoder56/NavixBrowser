package com.uniqueapps.NavixBrowser.handler;

import com.uniqueapps.NavixBrowser.BrowserTabbedPane;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class NavixDisplayHandler extends CefDisplayHandlerAdapter {

    JFrame windowFrame;
    BrowserTabbedPane tabbedPane;
    CefApp cefApp;
    private final ImageIcon closeImage;

    public NavixDisplayHandler(JFrame windowFrame, BrowserTabbedPane tabbedPane, CefApp cefApp) {
        this.windowFrame = windowFrame;
        this.tabbedPane = tabbedPane;
        this.cefApp = cefApp;
        try {
            closeImage = new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/delete.png"))).getScaledInstance(20, 20, BufferedImage.SCALE_SMOOTH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onTitleChange(CefBrowser cefBrowser, String newTitle) {
        super.onTitleChange(cefBrowser, newTitle);
        JPanel tabPanel = new JPanel();
        if (newTitle.length() > 15) {
            newTitle = newTitle.substring(0, 12) + "...";
        } else if (newTitle.length() < 15) {
            StringBuilder newTitleBuilder = new StringBuilder(newTitle);
            if ((15 - newTitle.length()) % 2 == 0) {
                for (int i = 0; i == (15 - newTitleBuilder.length()) / 2; i++) {
                    newTitleBuilder.append(" ");
                    newTitleBuilder.insert(0, " ");
                }
            } else {
                for (int i = 0; i == (15 - newTitleBuilder.length()) / 2; i++) {
                    newTitleBuilder.append(" ");
                }
                for (int i = 0; i == ((15 - newTitleBuilder.length()) / 2) + 1; i++) {
                    newTitleBuilder.insert(0, " ");
                }
            }
            newTitle = newTitleBuilder.toString();
        }
        JLabel tabInfoLabel = new JLabel(newTitle);
        try {
            tabInfoLabel.setIcon(new ImageIcon(ImageIO.read(new URL("https://www.google.com/s2/favicons?domain=" + cefBrowser.getURL()))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabPanel.add(tabInfoLabel);
        JButton closeTabButton = new JButton();
        closeTabButton.setBorder(new EmptyBorder(0,0,0,0));
        closeTabButton.setBackground(tabPanel.getBackground());
        closeTabButton.setIcon(closeImage);
        closeTabButton.addActionListener(l -> {
            if (tabbedPane.getTabCount() > 1) {
                tabbedPane.removeBrowserTab(cefBrowser);
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