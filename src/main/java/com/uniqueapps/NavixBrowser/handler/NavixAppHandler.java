package com.uniqueapps.NavixBrowser.handler;

import org.cef.CefApp;
import org.cef.handler.CefAppHandlerAdapter;

public class NavixAppHandler extends CefAppHandlerAdapter {

    public NavixAppHandler(String[] strings) {
        super(strings);
    }

    @Override
    public void stateHasChanged(CefApp.CefAppState cefAppState) {
        super.stateHasChanged(cefAppState);
        if (cefAppState == CefApp.CefAppState.TERMINATED) System.exit(0);
    }
}
