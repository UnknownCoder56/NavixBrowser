package com.uniqueapps.NavixBrowser.handler;

import com.uniqueapps.NavixBrowser.component.BrowserTabbedPane;
import com.uniqueapps.NavixBrowser.component.BrowserWindow;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.uniqueapps.NavixBrowser.component.BrowserTabbedPane.generateTabPanel;

public class NavixDisplayHandler extends CefDisplayHandlerAdapter {

    BrowserWindow windowFrame;
    BrowserTabbedPane tabbedPane;
    JTextField browserField;
    CefApp cefApp;
    public static final ImageIcon closeImage;

    static {
        try {
            closeImage = new ImageIcon(ImageIO.read(Objects.requireNonNull(NavixDisplayHandler.class.getResourceAsStream("/images/cross.png"))).getScaledInstance(20, 20, BufferedImage.SCALE_SMOOTH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NavixDisplayHandler(BrowserWindow windowFrame, BrowserTabbedPane tabbedPane, JTextField browserField, CefApp cefApp) {
        this.windowFrame = windowFrame;
        this.tabbedPane = tabbedPane;
        this.browserField = browserField;
        this.cefApp = cefApp;
    }

    @Override
    public void onTitleChange(CefBrowser cefBrowser, String newTitle) {
        super.onTitleChange(cefBrowser, newTitle);

        try {
            tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(cefBrowser.getUIComponent()), generateTabPanel(windowFrame, tabbedPane, cefApp, cefBrowser, newTitle));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cefBrowser == tabbedPane.getSelectedBrowser()) {
            windowFrame.setTitle(newTitle + " - Navix");
        }
    }

    @Override
    public void onAddressChange(CefBrowser cefBrowser, CefFrame cefFrame, String newUrl) {
        super.onAddressChange(cefBrowser, cefFrame, newUrl);
        if (cefBrowser == tabbedPane.getSelectedBrowser()) {
            if (!newUrl.contains("newtab.html")) {
                browserField.setText(newUrl);
            } else {
                browserField.setText("navix://home");
            }
        }
    }
}
