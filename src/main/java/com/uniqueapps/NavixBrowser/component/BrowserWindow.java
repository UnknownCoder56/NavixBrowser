package com.uniqueapps.NavixBrowser.component;

import com.uniqueapps.NavixBrowser.handler.*;
import com.uniqueapps.NavixBrowser.listener.NavixComponentListener;
import com.uniqueapps.NavixBrowser.listener.NavixWindowListener;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class BrowserWindow extends JFrame {

    private static final long serialVersionUID = -3658310837225120769L;
    private final CefApp cefApp;
    private final CefClient cefClient;
    private final RoundedTextField browserAddressField;
    private final JButton forwardNav;
    private final JButton backwardNav;
    private final JButton reloadButton;
    private final JButton addTabButton;
    private final BrowserTabbedPane tabbedPane;
    public boolean browserIsInFocus = false;

    public BrowserWindow(String startURL, boolean useOSR, boolean isTransparent) {
        CefApp.addAppHandler(new NavixAppHandler(null));
        CefApp.startup(new String[]{
                "--disable-features=IsolateOrigins,site-per-process",
                "--disable-gpu",
                "--disable-software-rasterizer"});

        CefSettings cefSettings = new CefSettings();
        cefSettings.windowless_rendering_enabled = useOSR;
        cefSettings.user_agent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.2704.106 Safari/537.36 Navix/0.5";
        cefSettings.user_agent_product = "Navix 0.5";
        File file = new File(".", "cache");
        file.mkdirs();
        cefSettings.cache_path = file.getAbsolutePath();
        cefApp = CefApp.getInstance(cefSettings);
        cefClient = cefApp.createClient();

        try {
            setIconImage(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/pages/navix.png"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        backwardNav = new JButton();
        forwardNav = new JButton();
        reloadButton = new JButton();
        addTabButton = new JButton();
        browserAddressField = new RoundedTextField(30);

        try {
            tabbedPane = new BrowserTabbedPane(this, forwardNav, backwardNav, browserAddressField);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        addCefHandlers();
        addListeners();
        prepareNavBar(startURL, useOSR, isTransparent);

        tabbedPane.addBrowserTab(cefApp, startURL, useOSR, isTransparent);
    }

    private void addCefHandlers() {
        cefClient.addDialogHandler(new NavixDialogHandler(this));
        cefClient.addDisplayHandler(new NavixDisplayHandler(this, tabbedPane, browserAddressField, cefApp));
        cefClient.addDownloadHandler(new NavixDownloadHandler());
        cefClient.addFocusHandler(new NavixFocusHandler(this));
        cefClient.addLoadHandler(new NavixLoadHandler(forwardNav, backwardNav, this));
    }

    private void addListeners() {
        // A hack to enable browser resizing in non-OSR mode
        addComponentListener(new NavixComponentListener(this, tabbedPane));
        addWindowListener(new NavixWindowListener(this, cefApp));
    }

    private void prepareNavBar(String startURL, boolean useOSR, boolean isTransparent) {
        browserAddressField.addActionListener(l -> {
            String query = browserAddressField.getText();
            try {
                new URL(query);
                tabbedPane.getSelectedBrowser().loadURL(query);
            } catch (MalformedURLException e) {
                if (query.contains(".") || query.contains("://")) {
                    tabbedPane.getSelectedBrowser().loadURL(query);
                } else {
                    tabbedPane.getSelectedBrowser().loadURL("https://slsearch.cf/search?q=" + query);
                }
            }
        });
        browserAddressField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!browserIsInFocus) return;
                browserIsInFocus = false;
                KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
                browserAddressField.requestFocusInWindow();
            }
        });

        browserAddressField.setFont(new JLabel().getFont());

        backwardNav.setEnabled(false);
        forwardNav.setEnabled(false);

        backwardNav.setBorder(new EmptyBorder(0, 0, 0, 0));
        forwardNav.setBorder(new EmptyBorder(0, 0, 0, 0));
        reloadButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        addTabButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        backwardNav.setBackground(this.getBackground());
        forwardNav.setBackground(this.getBackground());
        reloadButton.setBackground(this.getBackground());
        addTabButton.setBackground(this.getBackground());

        try {
            backwardNav.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/left-chevron.png"))).getScaledInstance(30, 30, BufferedImage.SCALE_SMOOTH)));
            forwardNav.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/right-chevron.png"))).getScaledInstance(30, 30, BufferedImage.SCALE_SMOOTH)));
            reloadButton.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/reload.png"))).getScaledInstance(30, 30, BufferedImage.SCALE_SMOOTH)));
            addTabButton.setIcon(new ImageIcon(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/add-button.png"))).getScaledInstance(30, 30, BufferedImage.SCALE_SMOOTH)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        backwardNav.addActionListener(l -> {
            if (tabbedPane.getSelectedBrowser().canGoBack()) {
                tabbedPane.getSelectedBrowser().goBack();
            }
        });
        forwardNav.addActionListener(l -> {
            if (tabbedPane.getSelectedBrowser().canGoForward()) {
                tabbedPane.getSelectedBrowser().goForward();
            }
        });
        reloadButton.addActionListener(l -> tabbedPane.getSelectedBrowser().loadURL(tabbedPane.getSelectedBrowser().getURL()));
        addTabButton.addActionListener(l -> tabbedPane.addBrowserTab(cefApp, startURL, useOSR, isTransparent));

        JPanel navBar = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 3;
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0;
        gbc.weightx = 0.1;
        navBar.add(backwardNav, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.1;
        navBar.add(forwardNav, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        gbc.weighty = 50;
        navBar.add(reloadButton, gbc);

        gbc.gridx = 3;
        gbc.weightx = 50;
        navBar.add(browserAddressField, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0.1;
        navBar.add(addTabButton, gbc);

        getContentPane().add(navBar, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
}
