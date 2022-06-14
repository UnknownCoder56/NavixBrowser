package com.uniqueapps.NavixBrowser.handler;

import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;

public class NavixDownloadHandler implements CefDownloadHandler {

    @Override
    public void onBeforeDownload(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, String suggestedFileName, CefBeforeDownloadCallback cefBeforeDownloadCallback) {

    }

    @Override
    public void onDownloadUpdated(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, CefDownloadItemCallback cefDownloadItemCallback) {

    }
}
