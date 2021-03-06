package com.uniqueapps.NavixBrowser.component;

import com.uniqueapps.NavixBrowser.handler.*;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BrowserTabbedPane extends JTabbedPane {

    BrowserWindow windowFrame;
    JButton forwardNav, backwardNav;
    JTextField browserField;
    public static Map<Component, CefBrowser> browserComponentMap = new HashMap<>();
    private static final ImageIcon closeImage;
    private static final Color cornflowerBlue = new Color(100, 149, 237);

    static {
        try {
            closeImage = new ImageIcon(ImageIO.read(Objects.requireNonNull(BrowserTabbedPane.class.getResourceAsStream("/images/cross.png"))).getScaledInstance(20, 20, BufferedImage.SCALE_SMOOTH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BrowserTabbedPane(BrowserWindow windowFrame, JButton forwardNav, JButton backwardNav, JTextField browserField) throws IOException {
        this.windowFrame = windowFrame;
        this.forwardNav = forwardNav;
        this.backwardNav = backwardNav;
        this.browserField = browserField;
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setUI(new BasicTabbedPaneUI() {
            final Insets insets = new Insets(0, 0, 0, 0);
            @Override
            protected Insets getTabInsets(int tabPlacement, int tabIndex) {
                return insets;
            }
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            }
        });
        var tabbedPane = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = new JPopupMenu() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2d = (Graphics2D) g;
                            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                            super.paintComponent(g2d);
                        }
                    };

                    JMenuItem copyLinkItem = new JMenuItem("Copy URL");
                    copyLinkItem.addActionListener(l -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(browserComponentMap.get(tabbedPane.getComponentAt(tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY()))).getURL()), null));
                    popupMenu.add(copyLinkItem);

                    JMenuItem closeTabItem = new JMenuItem("Close tab");
                    closeTabItem.addActionListener(l -> {
                        if (tabbedPane.getTabCount() > 1) {
                            tabbedPane.removeBrowserTab(browserComponentMap.get(tabbedPane.getComponentAt(tabbedPane.getUI().tabForCoordinate(tabbedPane, e.getX(), e.getY()))));
                        } else {
                            windowFrame.dispatchEvent(new WindowEvent(windowFrame, WindowEvent.WINDOW_CLOSING));
                            windowFrame.cefApp.dispose();
                            windowFrame.dispose();
                        }
                    });
                    popupMenu.add(closeTabItem);

                    popupMenu.show(tabbedPane, e.getX(), e.getY());
                }
            }
        });
        addChangeListener(l -> {
            windowFrame.setTitle("Navix");
            for (int i = 0; i < getTabCount(); i++) {
                Component c = getTabComponentAt(i);
                if (c != null) {
                    c.setBackground(getSelectedIndex() == i ? cornflowerBlue : cornflowerBlue.darker());
                }
            }
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
        setTabComponentAt(getTabCount() - 1, generateTabPanel(windowFrame, this, cefApp, cefBrowser, "New Tab", true));
        setSelectedIndex(getTabCount() - 1);
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

    public static JPanel generateTabPanel(BrowserWindow windowFrame, BrowserTabbedPane tabbedPane, CefApp cefApp, CefBrowser cefBrowser, String newTitle, boolean highlightTab) {
        if (newTitle.length() > 15) {
            newTitle = newTitle.substring(0, 12) + "...";
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
        tabPanel.setBackground(highlightTab ? cornflowerBlue : cornflowerBlue.darker());
        JLabel tabInfoLabel = new JLabel(newTitle);
        tabInfoLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
        try {
            tabInfoLabel.setIcon(new ImageIcon(ImageIO.read(new URL("https://www.google.com/s2/favicons?domain=" + cefBrowser.getURL()))));
        } catch (IOException e) {
            System.out.println("Could not get favicon for: " + cefBrowser.getURL());
        }
        tabPanel.add(tabInfoLabel, BorderLayout.CENTER);
        JButton closeTabButton = new JButton();
        closeTabButton.setBorder(new EmptyBorder(5, 5, 5, 5));
        closeTabButton.setBackground(new Color(0x0, true));
        closeTabButton.setIcon(closeImage);
        closeTabButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeTabButton.setBackground(new Color(0, 0, 0, 50));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeTabButton.setBackground(new Color(0x0, true));
            }
        });
        closeTabButton.addActionListener(l -> {
            if (tabbedPane.getTabCount() > 1) {
                tabbedPane.removeBrowserTab(cefBrowser);
            } else {
                windowFrame.dispatchEvent(new WindowEvent(windowFrame, WindowEvent.WINDOW_CLOSING));
                cefApp.dispose();
                windowFrame.dispose();
            }
        });
        tabPanel.add(closeTabButton, BorderLayout.EAST);
        return tabPanel;
    }
}
