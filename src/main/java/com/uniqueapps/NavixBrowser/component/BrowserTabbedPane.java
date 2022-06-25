package com.uniqueapps.NavixBrowser.component;

import com.uniqueapps.NavixBrowser.handler.*;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BrowserTabbedPane extends JTabbedPane {

    BrowserWindow windowFrame;
    JButton forwardNav, backwardNav;
    RoundedTextField browserField;
    public boolean workingOnTabs = false;
    public Map<Component, CefBrowser> browserComponentMap = new HashMap<>();

    public BrowserTabbedPane(BrowserWindow windowFrame, JButton forwardNav, JButton backwardNav, RoundedTextField browserField) throws IOException {
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
        workingOnTabs = true;
        var cefClient = cefApp.createClient();
        cefClient.addDialogHandler(new NavixDialogHandler(windowFrame));
        cefClient.addDisplayHandler(new NavixDisplayHandler(windowFrame, this, browserField, cefApp));
        cefClient.addDownloadHandler(new NavixDownloadHandler());
        cefClient.addFocusHandler(new NavixFocusHandler(windowFrame));
        cefClient.addLoadHandler(new NavixLoadHandler(forwardNav, backwardNav, windowFrame));
        var cefBrowser = cefClient.createBrowser(startURL, useOSR, isTransparent);
        browserComponentMap.put(cefBrowser.getUIComponent(), cefBrowser);
        addTab("New Tab", null, cefBrowser.getUIComponent(), cefBrowser.getURL());
        workingOnTabs = false;
    }

    public void removeBrowserTab(CefBrowser browser) {
        workingOnTabs = true;
        browserComponentMap.remove(browser.getUIComponent());
        removeTabAt(indexOfComponent(browser.getUIComponent()));
        workingOnTabs = false;
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
}
