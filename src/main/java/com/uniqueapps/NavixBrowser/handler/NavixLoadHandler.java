package com.uniqueapps.NavixBrowser.handler;

import com.formdev.flatlaf.FlatLightLaf;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;

public class NavixLoadHandler extends CefLoadHandlerAdapter {

    JButton forwardNav, backwardNav;
    JFrame windowFrame;

    public NavixLoadHandler(JButton forwardNav, JButton backwardNav, JFrame windowFrame) {
        this.forwardNav = forwardNav;
        this.backwardNav = backwardNav;
        this.windowFrame = windowFrame;
    }

    @Override
    public void onLoadingStateChange(CefBrowser cefBrowser, boolean b, boolean b1, boolean b2) {
        super.onLoadingStateChange(cefBrowser, b, b1, b2);
        forwardNav.setEnabled(cefBrowser.canGoForward());
        backwardNav.setEnabled(cefBrowser.canGoBack());
    }

    @Override
    public void onLoadError(CefBrowser cefBrowser, CefFrame cefFrame, ErrorCode errorCode, String s, String s1) {
        super.onLoadError(cefBrowser, cefFrame, errorCode, s, s1);
        try {
            if (errorCode.getCode() != -3) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                JOptionPane.showMessageDialog(cefBrowser.getUIComponent(), "Failed to load " + cefBrowser.getURL() + " with error code " + errorCode.getCode() + "!", JOptionPane.MESSAGE_PROPERTY, JOptionPane.ERROR_MESSAGE);
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
