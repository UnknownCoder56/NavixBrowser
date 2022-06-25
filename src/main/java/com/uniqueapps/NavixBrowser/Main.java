package com.uniqueapps.NavixBrowser;

import com.formdev.flatlaf.FlatDarkLaf;
import com.uniqueapps.NavixBrowser.component.BrowserWindow;

import javax.swing.*;

public class Main {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}
		var window = new BrowserWindow("navix://home", false, false);
		window.setSize(600, 400);
		window.setTitle("Navix");
		window.setVisible(true);
	}
}
