package com.uniqueapps.NavixBrowser;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}
		var window = new BrowserWindow("https://www.google.com", false, false);
		window.setSize(600, 400);
		window.setTitle("Navix");
		window.setVisible(true);
	}
}
