package app.weight.monitor.application;

import java.awt.Color;
import java.util.Optional;

import application.base.app.gui.ColorProvider;
import application.base.app.gui.GUIConstants;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;

/**
 * Define the application specific details
 */
public class WeightMonitorDefinition extends ApplicationDefinition {

	/**
	 * Create the application definition using the supplied name as the application.
	 * 
	 * @param applicationName - the name to be used for this application.
	 */
	public WeightMonitorDefinition(String applicationName) {
		super(applicationName);
	}

	@Override
	public boolean hasModelFile() {
		return true;
	}

	@Override
	public Optional<Color> bottomColor() {
		String bottom = IniFile.value(GUIConstants.BOTTOM_COLOR);
		if (bottom.isEmpty() || bottom.equals("default")) {
			bottom = "lightsteelblue";
			IniFile.store(GUIConstants.BOTTOM_COLOR, bottom);
		}
		Color bottomColor = ColorProvider.get(bottom);
		return Optional.ofNullable(bottomColor);
	}

	@Override
	public Optional<Color> topColor() {
		String top = IniFile.value(GUIConstants.TOP_COLOR);
		if (top.isEmpty() || top.equals("default")) {
			top = "lightcyan";
			IniFile.store(GUIConstants.TOP_COLOR, top);
		}
		Color topColor = ColorProvider.get(top);
		return Optional.ofNullable(topColor);
	}
}
