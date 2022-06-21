package com.uniqueapps.NavixBrowser;

import org.cef.CefClient;
import org.cef.browser.CefBrowser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserTabbedPane extends JTabbedPane {

    public boolean workingOnTabs = false;
    public Map<Component, CefBrowser> browserComponentMap = new HashMap<>();
    private final BufferedImage newTabFavicon = ImageIO.read(new URL("https://www.google.com/s2/favicons?domain=google.com"));

    public BrowserTabbedPane() throws IOException {

    }

    public void addBrowserTab(CefClient cefClient, String startURL, boolean useOSR, boolean isTransparent) {
        workingOnTabs = true;
        var cefBrowser = cefClient.createBrowser(startURL, useOSR, isTransparent);
        browserComponentMap.put(cefBrowser.getUIComponent(), cefBrowser);
        addTab("New Tab", new ImageIcon(newTabFavicon), cefBrowser.getUIComponent(), cefBrowser.getURL());
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
