package com.uniqueapps.NavixBrowser;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.IOException;

public class Main {
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatLightLaf());
		} catch (UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}
		try {
			var window = new BrowserWindow("https://www.google.com", false, false);
			window.setSize(600, 400);
			window.setTitle("Navix");
			window.setVisible(true);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			Main.main(null);
		}
	}
}
