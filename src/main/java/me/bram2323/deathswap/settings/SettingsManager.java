package me.bram2323.deathswap.settings;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import me.bram2323.deathswap.DeathSwap;

public class SettingsManager {
	
	public static SettingsManager instance;
	
	FileConfiguration config;
	List<Setting> Settings = new ArrayList<>();
	
	public SettingsManager() {
		if (instance != null) return;
		instance = this;
		config = DeathSwap.main.getConfig();
	}
	
	
	public void AddSetting(String Name, Boolean DefaultValue, String Discription) {
		Setting setting = new Setting(Name, Discription, 1, DefaultValue, false);
		config.addDefault(Name, DefaultValue);
		Settings.add(setting);
		SetSetting(Name, config.get(Name).toString());
	}
	
	public void AddSetting(String Name, int DefaultValue, String Discription, Boolean ticks) {
		Setting setting = new Setting(Name, Discription, 2, DefaultValue, ticks);
		config.addDefault(Name, DefaultValue);
		Settings.add(setting);
	}
	
	public void AddSetting(String Name, int DefaultValue, String Discription, Boolean ticks, SettingsModifier modifierNumberSetting, int modifierNumber, SettingsModifier modifierStringSetting, String modifierString) {
		Setting setting = new Setting(Name, Discription, 2, DefaultValue, ticks, modifierNumberSetting, modifierStringSetting, modifierNumber, modifierString);
		config.addDefault(Name, DefaultValue);
		Settings.add(setting);
	}
	
	public void AddSetting(String Name, String DefaultValue, String Discription) {
		Setting setting = new Setting(Name, Discription, 3, DefaultValue, false);
		config.addDefault(Name, DefaultValue);
		Settings.add(setting);
	}
	
	
	public Object GetSetting(String name) {
		Setting setting = GetSettingVar(name);
		if (setting == null) return null;
		else return config.get(setting.Name);
	}

	public int GetTeleportMode(){
		String mode = (String)GetSetting("TeleportMode");

		if (mode.equals("random")){
			return 0;
		}
		else if (mode.equals("circle")){
			return 1;
		}
		else if (mode.equals("random_circle")){
			return 2;
		}
		else if (mode.equals("fixed_circle")){
			return 3;
		}
		else if (mode.equals("pairs")){
			return 4;
		}
		return 0;
	}

	public String SetSetting(String name, String value){
		if (SettingExists(name)){
			Setting setting = GetSettingVar(name);
			if (setting.Type == 3){
				config.set(setting.Name, value);
				return ChatColor.GREEN + "Setting '" + setting.Name + "' changed to " + value;
			}
			else if (setting.Type == 1){
				value = value.toLowerCase();
				if (value.equals("true") || value.equals("false")){
					config.set(setting.Name, value.equals("true"));
					return ChatColor.GREEN + "Setting '" + setting.Name + "' changed to " + value;
				}
				else {
					return ChatColor.RED + "Usage: /dssettings set " + setting.Name + " <true, false>";
				}
			}
			else if (setting.Type == 2){
				int number;
				try {
					number = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					return ChatColor.RED + "You need to input a valid number!";
				}

				if (setting.ModifierNumberSetting != SettingsModifier.Ignore){
					SettingsModifier mod = setting.ModifierNumberSetting;
					int num = setting.ModifierNumber;
					if (mod == SettingsModifier.Higher && number <= num){
						return ChatColor.RED + "Value has to be greater than " + num + "!";
					}
					else if (mod == SettingsModifier.Lower && number >= num){
						return ChatColor.RED + "Value has to be less than " + num + "!";
					}
					else if (mod == SettingsModifier.HigherEquals && number < num){
						return ChatColor.RED + "Value has to be greater than or equal to " + num + "!";
					}
					else if (mod == SettingsModifier.LowerEquals && number > num){
						return ChatColor.RED + "Value has to be lower than or equal to " + num + "!";
					}
				}

				if (setting.ModifierStringSetting != SettingsModifier.Ignore){
					SettingsModifier mod = setting.ModifierStringSetting;
					if (SettingExists(setting.ModifierString)){
						Setting numSetting = GetSettingVar(setting.ModifierString);
						if (numSetting.Type == 2){
							int num = (int)GetSetting(numSetting.Name);
							if (mod == SettingsModifier.Higher && number <= num){
								return ChatColor.RED + "Value has to be greater than '" + numSetting.Name + "'!";
							}
							else if (mod == SettingsModifier.Lower && number >= num){
								return ChatColor.RED + "Value has to be less than '" + numSetting.Name + "'!";
							}
							else if (mod == SettingsModifier.HigherEquals && number < num){
								return ChatColor.RED + "Value has to be greater than or equal to '" + numSetting.Name + "'!";
							}
							else if (mod == SettingsModifier.LowerEquals && number > num){
								return ChatColor.RED + "Value has to be lower than or equal to '" + numSetting.Name + "'!";
							}
						}
					}
				}

				config.set(setting.Name, number);
				if (setting.Ticks){
					float seconds = (float) number / 20;
					return ChatColor.GREEN + "Setting '" + setting.Name + "' changed to " + number + " ticks (" + seconds + " seconds)";
				}
				else return ChatColor.GREEN + "Setting '" + setting.Name + "' changed to " + number;
			}
			else {
				return ChatColor.RED + "An internal error occurred";
			}
		}
		
		return ChatColor.RED + "Setting does not exist!\nUsage: /dssettings set <setting> <value>";
	}

	public String SetTeleportMode(String value) {
		value = value.toLowerCase();
		if (value.equals("random") || value.equals("circle") || value.equals("random_circle") || value.equals("fixed_circle") || value.equals("pairs")) {
			String result = SetSetting("TeleportMode", value);
			if (value.equals("pairs") && DeathSwap.game.state == 1) {
				DeathSwap.game.makePairs();
			}
			return result;
		}
		else {
			return ChatColor.RED + "Useage: /dssettings set TeleportMode <random, circle, random_circle, fixed_circle, pairs>";
		}
	}


	public Boolean SettingExists(String name){
		for (Setting setting : Settings) {
			if (setting.Name.toLowerCase().equals(name.toLowerCase())) return true;
		}
		return false;
	}

	public Setting GetSettingVar(String name){
		for (Setting setting : Settings) {
			if (setting.Name.toLowerCase().equals(name.toLowerCase())) return setting;
		}
		return null;
	}


	public List<String> GetSettingNamesList(String name){
		List<String> names = new ArrayList<>();
		for (Setting setting : Settings) {
			if (setting.Name.toLowerCase().startsWith(name.toLowerCase())) names.add(setting.Name);
		}
		return names;
	}

	public List<String> GetSettingTabcomplete(String name){
		List<String> values = new ArrayList<>();
		if (SettingExists(name)) {
			Setting setting = GetSettingVar(name);
			if (setting.Type == 1) {
				values.add("true");
				values.add("false");
			}
			else {
				values.add(setting.DefaultValue.toString());
			}
		}
		return values;
	}


	public String GetSettingsString(){
		String message = "";
		Boolean first = true;
		for (Setting setting : Settings) {
			if (!first) message += "\n";
			message += setting.Name + ": " + GetSetting(setting.Name);
			first = false;
		}
		return message;
	}

	public String GetSettingsGameString(){
		String Config = "The swap will occur between " + ChatColor.GREEN + ((int)GetSetting("MinTimer") / 20f) + ChatColor.WHITE + " and " + ChatColor.GREEN + ((int)GetSetting("MaxTimer") / 20f) + ChatColor.WHITE + " seconds" +
		"\nYou'll get a warning " + ChatColor.GREEN +  ((int)GetSetting("Warning") / 20f) + ChatColor.WHITE + " seconds before a swap" +
		"\nYou'll be " + ChatColor.GREEN + ((int)GetSetting("Safe") / 20f) + ChatColor.WHITE + " seconds invincible after a swap" +
		"\nTeleportation is based on a "+ ChatColor.GREEN + GetSetting("TeleportMode") + ChatColor.WHITE + " method";
		return Config;
	}

	public String GetHelpString(){
		String message = "";
		Boolean first = true;
		for (Setting setting : Settings) {
			if (!first) message += "\n";
			message += ChatColor.DARK_GREEN + "- " + setting.Name + ": " + ChatColor.GREEN + setting.Discription;
			first = false;
		}
		return message;
	}
}
