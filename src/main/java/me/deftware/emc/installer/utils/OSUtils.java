package me.deftware.emc.installer.utils;

class OSUtils {

	private static String OS = System.getProperty("os.name").toLowerCase();

	static boolean isWindows() {
		return OS.contains("win");
	}

	static boolean isMac() {
		return OS.contains("darwin") || OS.contains("mac");
	}

	static boolean isLinux() {
		return OS.contains("nux");
	}

}
