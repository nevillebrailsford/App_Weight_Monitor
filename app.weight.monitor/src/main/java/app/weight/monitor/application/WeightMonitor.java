package app.weight.monitor.application;

import java.awt.Color;
import java.util.Optional;
import java.util.logging.Logger;

import javax.swing.JFrame;

import app.weight.monitor.Constants;
import app.weight.monitor.storage.ReadingsLoad;
import application.base.app.ApplicationBaseForGUI;
import application.base.app.Parameters;
import application.base.app.gui.ColorProvider;
import application.base.app.gui.GUIConstants;
import application.definition.ApplicationConfiguration;
import application.definition.ApplicationDefinition;
import application.inifile.IniFile;
import application.storage.StoreDetails;

public class WeightMonitor extends ApplicationBaseForGUI {
	private static final long serialVersionUID = 1L;
	private static final String CLASS_NAME = WeightMonitor.class.getName();

	private static Logger LOGGER = null;

	private JFrame parent;

	@Override
	public void configureStoreDetails() {
		dataLoader = new ReadingsLoad();
		storeDetails = new StoreDetails(dataLoader, Constants.MODEL, Constants.READINGS_FILE);
	}

	@Override
	public ApplicationDefinition createApplicationDefinition(Parameters parameters) {
		ApplicationDefinition definition = new ApplicationDefinition(parameters.getNamed().get("name")) {

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
		};
		return definition;
	}

	@Override
	public void start(JFrame parent) {
		LOGGER = ApplicationConfiguration.logger();
		LOGGER.entering(CLASS_NAME, "start");
		this.parent = parent;
		System.out.println(
				"Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is starting");
		LOGGER.exiting(CLASS_NAME, "start");

	}

	@Override
	public void terminate() {
		LOGGER.entering(CLASS_NAME, "terminate");
		System.out.println(
				"Application " + ApplicationConfiguration.applicationDefinition().applicationName() + " is stopping");
		LOGGER.exiting(CLASS_NAME, "terminate");
	}

	/**
	 * Main entry point for program.
	 * 
	 * @param args - any number of arguments passed in from command line.
	 */
	public static void main(String[] args) {
		System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");
		launch(args);
	}
}
