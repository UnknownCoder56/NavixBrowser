package com.uniqueapps.NavixBrowser.component;

import com.uniqueapps.NavixBrowser.handler.*;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.uniqueapps.NavixBrowser.handler.NavixDisplayHandler.closeImage;

public class BrowserTabbedPane extends JTabbedPane {

    BrowserWindow windowFrame;
    JButton forwardNav, backwardNav;
    JTextField browserField;
    public Map<Component, CefBrowser> browserComponentMap = new HashMap<>();

    public BrowserTabbedPane(BrowserWindow windowFrame, JButton forwardNav, JButton backwardNav, JTextField browserField) throws IOException {
        this.windowFrame = windowFrame;
        this.forwardNav = forwardNav;
        this.backwardNav = backwardNav;
        this.browserField = browserField;
        addChangeListener(l -> {
            windowFrame.setTitle("Navix");
            if (!getSelectedBrowser().getURL().contains("newtab.html")) {
                browserField.setText(getSelectedBrowser().getURL());
            } else {
                browserField.setText("navix://home");
            }
        });
    }

    public void addBrowserTab(CefApp cefApp, String startURL, boolean useOSR, boolean isTransparent) {
        var cefClient = cefApp.createClient();
        cefClient.addDialogHandler(new NavixDialogHandler(windowFrame));
        cefClient.addDisplayHandler(new NavixDisplayHandler(windowFrame, this, browserField, cefApp));
        cefClient.addDownloadHandler(new NavixDownloadHandler());
        cefClient.addFocusHandler(new NavixFocusHandler(windowFrame));
        cefClient.addLoadHandler(new NavixLoadHandler(forwardNav, backwardNav, windowFrame));
        var cefBrowser = cefClient.createBrowser(startURL, useOSR, isTransparent);
        browserComponentMap.put(cefBrowser.getUIComponent(), cefBrowser);
        addTab("New Tab", null, cefBrowser.getUIComponent(), cefBrowser.getURL());
        setTabComponentAt(getTabCount() - 1, generateTabPanel(windowFrame, this, cefApp, cefBrowser, "New Tab"));
    }

    public void removeBrowserTab(CefBrowser browser) {
        browserComponentMap.remove(browser.getUIComponent());
        removeTabAt(indexOfComponent(browser.getUIComponent()));
    }

    public CefBrowser getSelectedBrowser() {
        return browserComponentMap.get(getSelectedComponent());
    }

    @Override
    public void paint(Graphics g) {
        Map<RenderingHints.Key, Object> rh = new HashMap<>();
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHints(rh);
        super.paint(g2d);
        g2d.dispose();
    }

    public static JPanel generateTabPanel(BrowserWindow windowFrame, BrowserTabbedPane tabbedPane, CefApp cefApp, CefBrowser cefBrowser, String newTitle) {
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
        JPanel tabPanel = new JPanel(new BorderLayout(4, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Area panelArea = new Area(g2d.getClip());
                Area roundedArea = new Area(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() + 8, 20, 20));
                panelArea.subtract(roundedArea);
                g2d.setColor(windowFrame.getBackground());
                g2d.fill(panelArea);
            }
        };
        Color cornflowerBlue = new Color(100, 149, 237).darker();
        tabPanel.setBackground(cornflowerBlue);
        JLabel tabInfoLabel = new JLabel(newTitle);
        tabInfoLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        try {
            tabInfoLabel.setIcon(new ImageIcon(ImageIO.read(new URL("https://www.google.com/s2/favicons?domain=" + cefBrowser.getURL()))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabPanel.add(tabInfoLabel, BorderLayout.CENTER);
        JButton closeTabButton = new JButton();
        closeTabButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        closeTabButton.setBackground(new Color(0x0, true));
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
        tabPanel.add(closeTabButton, BorderLayout.EAST);
        return tabPanel;
    }
}
