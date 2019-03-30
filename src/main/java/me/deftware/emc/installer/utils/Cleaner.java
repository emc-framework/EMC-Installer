package me.deftware.emc.installer.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;

public class Cleaner {

	public static void clean(boolean deepclean) throws Exception {
		System.out.println("Doing " + (deepclean ? "deepclean" : "clean") + " install, please wait while the installer cleans directories...");
		// Delete versions
		String[] directories = new File(Utils.getMinecraftRoot() + "versions" + File.separator).list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		for (String dir : directories) {
			if (dir.toLowerCase().contains("emc")) {
				System.out.println("Removing EMC version " + dir);
				FileUtils.forceDelete(new File(Utils.getMinecraftRoot() + "versions" + File.separator + dir));
			}
		}
		System.out.println("Removing EMC libraries folder...");
		if (new File(Utils.getMinecraftRoot() + "libraries" + File.separator + "EMC").exists()) {
			FileUtils.forceDelete(new File(Utils.getMinecraftRoot() + "libraries" + File.separator + "EMC"));
		}
		System.out.println("Removing me.deftware libraries folder...");
		if (new File(Utils.getMinecraftRoot() + "libraries" + File.separator + "me").exists()) {
			FileUtils.forceDelete(new File(Utils.getMinecraftRoot() + "libraries" + File.separator + "me"));
		}
		if (deepclean) {
			System.out.println("Removing OptiFine versions...");
			System.out.println("Removing optifine libraries folder...");
			if (new File(Utils.getMinecraftRoot() + "libraries" + File.separator + "optifine").exists()) {
				FileUtils.forceDelete(new File(Utils.getMinecraftRoot() + "libraries" + File.separator + "optifine"));
			}
			directories = new File(Utils.getMinecraftRoot() + "versions" + File.separator).list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			for (String dir : directories) {
				if (dir.toLowerCase().contains("optifine")) {
					System.out.println("Removing OptiFine version " + dir);
					FileUtils.forceDelete(new File(Utils.getMinecraftRoot() + "versions" + File.separator + dir));
				}
			}
		}
		System.out.println("Clean successful, starting installer...");
	}

}
