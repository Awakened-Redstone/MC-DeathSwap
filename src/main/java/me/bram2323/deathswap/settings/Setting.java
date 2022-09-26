package me.bram2323.deathswap.settings;

public class Setting {
	
	public String Name;
	public String Discription;
	public int Type;
	public Object DefaultValue;
	public Boolean Ticks;
	
	public SettingsModifier ModifierNumberSetting;
	public SettingsModifier ModifierStringSetting;
	public int ModifierNumber;
	public String ModifierString;
	
	public Setting(String name, String discription, int type, Object defaultValue, Boolean ticks) {
		Name = name;
		Discription = discription;
		Type = type;
		DefaultValue = defaultValue;
		Ticks = ticks;
		
		ModifierNumberSetting = SettingsModifier.Ignore;
		ModifierStringSetting = SettingsModifier.Ignore;
		ModifierNumber = 0;
		ModifierString = "";
	}
	
	public Setting(String name, String discription, int type, Object defaultValue, Boolean ticks, SettingsModifier modifierNumberSetting, SettingsModifier modifierStringSetting, int modifierNumber, String modifierString) {
		Name = name;
		Discription = discription;
		Type = type;
		DefaultValue = defaultValue;
		Ticks = ticks;
		
		ModifierNumberSetting = modifierNumberSetting;
		ModifierStringSetting = modifierStringSetting;
		ModifierNumber = modifierNumber;
		ModifierString = modifierString;
	}
}
