package me.deftware.emc.installer.ui;

import com.google.gson.*;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import me.deftware.emc.installer.utils.Utils;
import me.deftware.emc.installer.utils.WebUtils;
import me.deftware.emc.installer.utils.jsonbuilder.AbstractJsonBuilder;
import me.deftware.emc.installer.utils.jsonbuilder.LegacyJsonBuilder;
import me.deftware.emc.installer.utils.jsonbuilder.ModernJsonBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;

public class InstallerUI {

	private final String buildVersion = "1.3.4";

	// Settings
	private String clientName = "EMC";

	// Json Objects
	private JsonObject emcJson;

	// Components
	private JButton installButton;
	private JButton cancelButton;
	private JComboBox comboBox1;
	private JRadioButton withVanillaRadioButton;
	private JRadioButton forForgeRadioButton;
	private JPanel mainPanel;
	private JLabel madeByLabel;

	public InstallerUI(JsonObject emcJson, JFrame frame) {
		madeByLabel.setText(madeByLabel.getText() + ", v" + buildVersion);
		frame.setTitle(clientName + " Installer");
		this.emcJson = emcJson;
		ActionListener listener = ((e) -> {
			withVanillaRadioButton.setSelected(!e.getActionCommand().equals("forge"));
			forForgeRadioButton.setSelected(e.getActionCommand().equals("forge"));
		});
		withVanillaRadioButton.addActionListener(listener);
		forForgeRadioButton.addActionListener(listener);
		forForgeRadioButton.setActionCommand("forge");
		withVanillaRadioButton.setActionCommand("opti");
		withVanillaRadioButton.setText("With Vanilla");
		installButton.addActionListener((e) -> install());
		cancelButton.addActionListener((e) -> System.exit(0));
		emcJson.entrySet().forEach((entry) -> comboBox1.addItem(entry.getKey()));
	}

	public static JFrame create(JsonObject emcJson) {
		JFrame frame = new JFrame("");
		InstallerUI ui = new InstallerUI(emcJson, frame);
		JPanel panel = ui.mainPanel;
		panel.setBorder(new EmptyBorder(10, 10, 10, 10));
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setMinimumSize(new Dimension(400, 0));
		frame.setResizable(false);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		return frame;
	}

	private void install() {
		String mcVersion = comboBox1.getSelectedItem().toString();
		JsonObject selectVersionData = emcJson.get(mcVersion).getAsJsonObject();
		DialogUI dialog = new DialogUI("Installing " + clientName + ", please wait...", "", false, () -> {
			DialogUI installed = new DialogUI(String.format("Install complete, the installer will now exit. Open your Minecraft launcher and select \"release %s-%s\"", mcVersion, clientName), "Install complete", true, () -> System.exit(0));
			installed.setAlwaysOnTop(true);
			installed.setVisible(true);
		});
		new Thread(() -> {
			try {
				new Thread(() -> dialog.setVisible(true)).start();
				// Download required libs
				if (forForgeRadioButton.isSelected()) {
					if (!selectVersionData.get("forge").getAsBoolean()) {
						DialogUI noForge = new DialogUI("Forge is not available for your selected version, please click \"with vanilla\" instead.", "Error", true, () -> {
							dialog.setVisible(false);
						});
						noForge.setAlwaysOnTop(true);
						noForge.setVisible(true);
						return;
					}
					// Download and install EMC
					String link = "https://gitlab.com/EMC-Framework/maven/raw/master/me/deftware/EMC-Forge/"
							+ selectVersionData.get("version").getAsString() + "/EMC-Forge-" + selectVersionData.get("version").getAsString() + "-full.jar";
					new File(Utils.getMinecraftRoot() + "mods" + File.separator + mcVersion + File.separator).mkdirs();
					WebUtils.download(link, Utils.getMinecraftRoot() + "mods" + File.separator + mcVersion
							+ File.separator + "EMC.jar");
				}
				// Make JSON
				if (!forForgeRadioButton.isSelected()) {
					// Client json
					AbstractJsonBuilder jsonBuilder = mcVersion.equals("1.12.2") ? new LegacyJsonBuilder() : new ModernJsonBuilder();
					JsonObject json = jsonBuilder.build(mcVersion, selectVersionData.get("version").getAsString(), clientName, selectVersionData.get("tweaker").getAsString(), selectVersionData.get("inheritsFrom").getAsString());
					// Add extra libs
					JsonObject extraLibs = selectVersionData.get("extraLibs").getAsJsonObject();
					extraLibs.entrySet().forEach((entry) -> {
						JsonObject lib = extraLibs.get(entry.getKey()).getAsJsonObject();
						// Add lib
						JsonArray libs = json.get("libraries").getAsJsonArray();
						libs.add(jsonBuilder.generateMavenRepo("name", lib.get("name").getAsString(), "url", lib.get("url").getAsString()));
						// Add tweaker
						if (!lib.get("tweaker").getAsString().equals("")) {
							if (jsonBuilder instanceof LegacyJsonBuilder) {
								String minecraftArguments = json.get("minecraftArguments").getAsString();
								json.addProperty("minecraftArguments", minecraftArguments + " --tweakClass " + lib.get("tweaker").getAsString());
							} else {
								JsonArray game = json.get("arguments").getAsJsonObject().get("game").getAsJsonArray();
								game.add("--tweakClass");
								game.add(lib.get("tweaker").getAsString());
							}
						}
					});
					// Install client json
					File clientDir = new File(Utils.getMinecraftRoot() + "versions" + File.separator + mcVersion + "-" + clientName + File.separator);
					if (!clientDir.mkdirs()) {
						System.out.println("Unable to create client dir, probably already exists");
					}
					try (Writer writer = new FileWriter(new File(clientDir.getAbsolutePath() + File.separator + mcVersion + "-" + clientName + ".json"))) {
						new GsonBuilder().setPrettyPrinting().create().toJson(json, writer);
					}
					// Install launcher profile
					File profiles_json = new File(Utils.getMinecraftRoot() + "launcher_profiles.json");
					JsonObject launcherJson = new JsonParser().parse(Files.newBufferedReader(profiles_json.toPath())).getAsJsonObject();
					JsonObject profiles = launcherJson.get("profiles").getAsJsonObject();
					if (!profiles.has(mcVersion + "-" + clientName)) {
						profiles.add(mcVersion + "-" + clientName, AbstractJsonBuilder.generateLaunchProfile(clientName, mcVersion));
					}
					launcherJson.addProperty("selectedProfile", mcVersion + "-" + clientName);
					try (Writer writer = new FileWriter(profiles_json)) {
						new GsonBuilder().setPrettyPrinting().create().toJson(launcherJson, writer);
					}
				}
				dialog.onContinue();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}).start();
	}


	{
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
		$$$setupUI$$$();
	}

	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(11, 2, new Insets(0, 0, 0, 0), -1, -1));
		installButton = new JButton();
		installButton.setEnabled(true);
		installButton.setText("Install");
		mainPanel.add(installButton, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		cancelButton = new JButton();
		cancelButton.setEnabled(true);
		cancelButton.setText("Cancel");
		mainPanel.add(cancelButton, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		comboBox1 = new JComboBox();
		final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
		comboBox1.setModel(defaultComboBoxModel1);
		mainPanel.add(comboBox1, new GridConstraints(1, 0, 5, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		withVanillaRadioButton = new JRadioButton();
		withVanillaRadioButton.setSelected(true);
		withVanillaRadioButton.setText("With OptiFine");
		mainPanel.add(withVanillaRadioButton, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		forForgeRadioButton = new JRadioButton();
		forForgeRadioButton.setText("For Forge");
		mainPanel.add(forForgeRadioButton, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		mainPanel.add(spacer1, new GridConstraints(9, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label1 = new JLabel();
		label1.setText("Select below if you want to install for Forge or OptiFine. ");
		mainPanel.add(label1, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer2 = new Spacer();
		mainPanel.add(spacer2, new GridConstraints(9, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		final JLabel label2 = new JLabel();
		label2.setText("Select which Minecraft version you want to install for:");
		mainPanel.add(label2, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		madeByLabel = new JLabel();
		madeByLabel.setText("Made by https://deftware.me/");
		mainPanel.add(madeByLabel, new GridConstraints(10, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}
}
