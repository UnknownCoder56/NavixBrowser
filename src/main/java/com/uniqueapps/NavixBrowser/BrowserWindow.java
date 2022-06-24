package com.uniqueapps.NavixBrowser;

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
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class BrowserWindow extends JFrame {

    private static final long serialVersionUID = -3658310837225120769L;
    private final CefApp cefApp;
    private final CefClient cefClient;
    private final JTextField browserAddressField;
    private final JButton forwardNav;
    private final JButton backwardNav;
    private final JButton reloadButton;
    private final JButton addTabButton;
    private final BrowserTabbedPane tabbedPane;
    public boolean browserIsInFocus = false;

    BrowserWindow(String startURL, boolean useOSR, boolean isTransparent) {
        File libPath = new File(".", "runtime");
        try {
            addLibraryPath(libPath.getAbsolutePath());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

        try {
            tabbedPane = new BrowserTabbedPane(this, forwardNav, backwardNav);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        browserAddressField = new JTextField(startURL, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Map<RenderingHints.Key, Object> rh = new HashMap<>();
                rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHints(rh);
                super.paintComponent(g);
            }
        };

        addCefHandlers();
        addListeners();
        prepareNavBar(startURL, useOSR, isTransparent);

        tabbedPane.addBrowserTab(cefApp, startURL, useOSR, isTransparent);
    }

    private void addCefHandlers() {
        cefClient.addDialogHandler(new NavixDialogHandler(this));
        cefClient.addDisplayHandler(new NavixDisplayHandler(this, tabbedPane, cefApp));
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
                if (query.contains(".")) {
                    tabbedPane.getSelectedBrowser().loadURL(query);
                } else {
                    tabbedPane.getSelectedBrowser().loadURL("https://google.com/search?q=" + query);
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

    private void addLibraryPath(String path) throws NoSuchFieldException, IllegalAccessException {
        String libPath = System.getProperty("java.library.path");
        String newPath;

        if (libPath == null || libPath.isEmpty()) {
            newPath = path;
        } else {
            newPath = path + File.pathSeparator + libPath;
        }

        System.setProperty("java.library.path", newPath);

        Field field = ClassLoader.class.getDeclaredField("sys_paths");
        field.setAccessible(true);

        // Create override for sys_paths
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        java.util.List<String> newSysPaths = new ArrayList<>();
        newSysPaths.add(path);
        newSysPaths.addAll(Arrays.asList((String[])field.get(classLoader)));

        field.set(classLoader, newSysPaths.toArray(new String[0]));
    }
}
