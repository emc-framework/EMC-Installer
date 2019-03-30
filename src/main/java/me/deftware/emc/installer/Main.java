package me.deftware.emc.installer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import me.deftware.emc.installer.ui.InstallerUI;
import me.deftware.emc.installer.utils.Cleaner;
import me.deftware.emc.installer.utils.WebUtils;

import javax.swing.*;

public class Main {

    private static final String versionsURL = "https://gitlab.com/EMC-Framework/maven/raw/master/versions.json";
    public static String customEMC = "";

    public static void main(String[] args) {
        try {
            OptionParser optionParser = new OptionParser();
            optionParser.allowsUnrecognizedOptions();
            OptionSpec<String> emcVersion = optionParser.accepts("emc").withOptionalArg();
            OptionSpec<Boolean> clean = optionParser.accepts("clean").withOptionalArg().ofType(Boolean.class).describedAs("bool");
            OptionSpec<Boolean> deepclean = optionParser.accepts("deepclean").withOptionalArg().ofType(Boolean.class).describedAs("bool");
            OptionSet optionSet = optionParser.parse(args);
            if (optionSet.has(emcVersion)) {
                System.out.println("Using custom EMC version " + optionSet.valueOf(emcVersion));
                customEMC = optionSet.valueOf(emcVersion);
            }
            if (optionSet.has(clean) || optionSet.has(deepclean)) {
                Cleaner.clean(optionSet.has(deepclean));
            }
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
