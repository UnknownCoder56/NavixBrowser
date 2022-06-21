package com.uniqueapps.NavixBrowser.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefCallback;
import org.cef.handler.CefResourceHandlerAdapter;
import org.cef.network.CefRequest;

public class NavixSchemeHandler extends CefResourceHandlerAdapter {

    public static final String scheme = "navix";
    CefBrowser browser;

    public NavixSchemeHandler(CefBrowser browser) {
        this.browser = browser;
    }

    @Override
    public boolean processRequest(CefRequest cefRequest, CefCallback cefCallback) {
        String action = cefRequest.getURL();
        switch (action) {
            case "home" -> {

            }
            case "about" -> {

            }
            case "whats-new" -> {

            }
        }
        return false;
    }
}
