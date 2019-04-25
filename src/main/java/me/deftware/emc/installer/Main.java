package me.deftware.emc.installer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.deftware.emc.installer.ui.InstallerUI;
import me.deftware.emc.installer.utils.WebUtils;

import javax.swing.*;

public class Main {

	private static final String versionsURL = "https://gitlab.com/EMC-Framework/maven/raw/master/versions.json";

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new Thread(() -> {
				try {
					System.out.print("Fetching json files... ");
					JsonObject emcJson = new Gson().fromJson(WebUtils.get(versionsURL), JsonObject.class);
					System.out.println("Done");
					InstallerUI.create(emcJson).setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
