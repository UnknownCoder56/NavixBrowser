package com.uniqueapps.NavixBrowser;

import net.sf.image4j.codec.ico.ICODecoder;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserTabbedPane extends JTabbedPane {

    public boolean workingOnTabs = false;
    protected Map<Component, CefBrowser> browserComponentMap = new HashMap<>();
    private final BufferedImage newTabFavicon = ICODecoder.read(new URL("https://google.com/favicon.ico").openStream()).get(0);
    private final BrowserWindow window;

    public BrowserTabbedPane(BrowserWindow window) throws IOException {
        this.window = window;
    }

    public void addBrowserTab(CefClient cefClient, String startURL, boolean useOSR, boolean isTransparent) {
        workingOnTabs = true;
        var cefBrowser = cefClient.createBrowser(startURL, useOSR, isTransparent);
        browserComponentMap.put(cefBrowser.getUIComponent(), cefBrowser);
        try {
            if (getTabCount() > 1) {
                removeTabAt(getTabCount() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addTab("New Tab", new ImageIcon(newTabFavicon), cefBrowser.getUIComponent(), cefBrowser.getURL());
        addTab("+", null);
        JPanel tabPanel = new JPanel();
        tabPanel.add(new JLabel("New Tab"));
        JButton closeTabButton = new JButton("X");
        int thisIndex = getTabCount() - 2;
        closeTabButton.addActionListener(l -> removeTabAt(thisIndex));
        tabPanel.add(closeTabButton);
        setTabComponentAt(getTabCount() - 2, tabPanel);
        setSelectedIndex(getTabCount() - 2);
        workingOnTabs = false;
    }

    public void removeBrowserTab(CefBrowser browser) {
        workingOnTabs = true;
        browserComponentMap.remove(browser.getUIComponent());
        removeTabAt(indexOfComponent(browser.getUIComponent()));
        workingOnTabs = false;
    }

    public void removeBrowserTab(int index) {
        workingOnTabs = true;
        browserComponentMap.remove(browserComponentMap.keySet().stream().toList().get(index));
        removeTabAt(index);
        setSelectedIndex(getTabCount() - 2);
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
