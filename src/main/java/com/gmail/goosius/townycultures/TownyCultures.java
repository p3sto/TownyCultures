package com.gmail.goosius.townycultures;

import com.gmail.goosius.townycultures.command.TownyAdminCultureAddon;
import com.gmail.goosius.townycultures.command.CultureChatCommand;
import com.gmail.goosius.townycultures.command.CultureCommand;
import com.gmail.goosius.townycultures.command.TownyAdminReloadAddon;
import com.gmail.goosius.townycultures.integrations.TownyCulturesPlaceholderExpansion;
import com.gmail.goosius.townycultures.listeners.TownyDynmapListener;
import com.gmail.goosius.townycultures.listeners.TownyListener;
import com.gmail.goosius.townycultures.metadata.ResidentMetaDataController;
import com.gmail.goosius.townycultures.utils.CultureUtil;

import com.gmail.goosius.townycultures.utils.CultureManager;
import com.palmergames.bukkit.towny.Towny;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.gmail.goosius.townycultures.settings.Settings;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import com.palmergames.bukkit.util.Version;
import com.palmergames.util.StringMgmt;
import com.gmail.goosius.townycultures.listeners.StatusScreenListener;

import java.util.List;

public class TownyCultures extends JavaPlugin {

	private static TownyCultures plugin;
	public static String prefix = "[TownyCultures] ";
	private static Version requiredTownyVersion = Version.fromString("0.100.4.0");
	private static boolean dynmapTowny = false;
	private static CultureManager manager;

	public static TownyCultures getTownyCultures() {
		return plugin;
	}

	@Override
	public void onEnable() {

		plugin = this;
		printSickASCIIArt();

		if (!townyVersionCheck(getTownyVersion())) {
			severe("Towny version does not meet required minimum version: " + requiredTownyVersion.toString());
			onDisable();
			return;
		} else {
			info("Towny version " + getTownyVersion() + " found.");
		}

		manager = new CultureManager(Towny.getPlugin());

		if (!Settings.loadSettingsAndLang()) {
			onDisable();
			return;
		}

		if (Settings.isTownyCulturesEnabled()) {

			checkPlugins();

			registerListeners(Bukkit.getServer().getPluginManager());

			registerCommands();

			info("TownyCultures loaded successfully.");
		} else {
			info("TownyCultures loaded successfully but is disabled by config.");
		}
	}

	private void checkPlugins() {
		Plugin test = getServer().getPluginManager().getPlugin("PlaceholderAPI");
		if (test != null) {
			new TownyCulturesPlaceholderExpansion(this).register();
			info("Found PlaceholderAPI. Enabling support...");
		}

		test = getServer().getPluginManager().getPlugin("Dynmap-Towny");
		if (test != null)
			dynmapTowny = true;

	}

	@Override
	public void onDisable() {
		severe("Shutting down....");
	}

	public String getVersion() {
		return this.getDescription().getVersion();
	}

	private boolean townyVersionCheck(String version) {
		return Version.fromString(version).compareTo(requiredTownyVersion) >= 0;
	}

	private String getTownyVersion() {
		return Bukkit.getPluginManager().getPlugin("Towny").getDescription().getVersion();
	}

	private void registerListeners(PluginManager pm) {
		pm.registerEvents(new StatusScreenListener(), this);
		pm.registerEvents(new TownyListener(), this);
		if (dynmapTowny)
			pm.registerEvents(new TownyDynmapListener(), this);
	}

	private void registerCommands() {
		new CultureCommand();
		new TownyAdminCultureAddon();
		new TownyAdminReloadAddon();
		getCommand("cc").setExecutor(new CultureChatCommand());
	}

	private void printSickASCIIArt() {
		Bukkit.getConsoleSender().sendMessage(" .---.                           .--.     . . ");
		Bukkit.getConsoleSender().sendMessage("   |                            :         |_|_");
		Bukkit.getConsoleSender().sendMessage("   | .-..  .    ._.--. .  .     |    .  . | |  .  . .--..-. .--.");
		Bukkit.getConsoleSender().sendMessage("   |(   )\\  \\  /  |  | |  |     :    |  | | |  |  | |  (.-' `--.");
		Bukkit.getConsoleSender().sendMessage("   ' `-'  `' `'   '  `-`--|      `--'`--`-`-`-'`--`-'   `--'`--'");
		Bukkit.getConsoleSender().sendMessage("                          ;");
		Bukkit.getConsoleSender().sendMessage("                       `-'");
		Bukkit.getConsoleSender().sendMessage("                        by Goosius & LlmDl");
		Bukkit.getConsoleSender().sendMessage("");
	}

	public static CultureManager getManager() {
		return manager;
	}

	public static String getCulture(Player player) {
		Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
		if (resident == null)
			return Translatable.of("status_no_town").defaultLocale();
		return getCulture(resident);
	}

	public static String getCulture(Resident resident) {
		return StringMgmt.capitalize(ResidentMetaDataController.getResidentCulture(resident));
	}

	public static boolean hasCulture(Object obj) {
		if (obj instanceof Player player) {
			return !CultureUtil.isValidCultureName(getCulture(player));
		} else if (obj instanceof Resident resident) {
			return !CultureUtil.isValidCultureName(getCulture(resident));
		} else if (obj instanceof Town town ) {
			return ResidentMetaDataController.hasCulture(town);
		}
		return false;
	}

	public static void info(String message) {
		plugin.getLogger().info(message);
	}

	public static void severe(String message) {
		plugin.getLogger().severe(message);
	}
}
