package app.weight.monitor.preferences;

import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import app.weight.monitor.application.WeightMonitorApplication;
import app.weight.monitor.application.gui.GUIConstants;
import application.base.app.gui.ColorProvider;
import application.inifile.IniFile;
import application.preferences.PreferencesDialog;

/**
 * Add optional choices to the preferences dialog.
 */
public class WeightMonitorPreferencesDialog extends PreferencesDialog {
	private static final long serialVersionUID = 1L;

	private JComboBox<String> backgroundColorChoice;
	private JLabel backgroundColorPreview;
	private Color backgroundColor;

	private JComboBox<String> chartLineColorChoice;
	private JLabel chartLineColorPreview;
	private Color chartLineColor;

	private JComboBox<String> trendLineColorChoice;
	private JLabel trendLineColorPreview;
	private Color trendLineColor;

	/**
	 * Create the dialog.
	 * 
	 * @param parent - the owning frame.
	 */
	public WeightMonitorPreferencesDialog(JFrame parent) {
		super(parent);
	}

	@Override
	public void additionalActionListeners() {
		backgroundColorChoice.addActionListener((event) -> {
			previewBackground();
		});
		chartLineColorChoice.addActionListener((event) -> {
			previewChartLine();
		});
		trendLineColorChoice.addActionListener((event) -> {
			previewTrendLine();
		});
	}

	@Override
	public void additionalGUIItems(JPanel contentPanel) {
		JLabel label1 = new JLabel("Chart Background Color:");
		backgroundColorChoice = new JComboBox<>();
		backgroundColorPreview = new JLabel("  ");
		backgroundColorPreview.setOpaque(true);
		contentPanel.add(label1);
		contentPanel.add(backgroundColorChoice);
		contentPanel.add(backgroundColorPreview);

		JLabel label2 = new JLabel("Chart Line Color:");
		chartLineColorChoice = new JComboBox<>();
		chartLineColorPreview = new JLabel("  ");
		chartLineColorPreview.setOpaque(true);
		contentPanel.add(label2);
		contentPanel.add(chartLineColorChoice);
		contentPanel.add(chartLineColorPreview);

		JLabel label3 = new JLabel("Trend Line Color:");
		trendLineColorChoice = new JComboBox<>();
		trendLineColorPreview = new JLabel("  ");
		trendLineColorPreview.setOpaque(true);
		contentPanel.add(label3);
		contentPanel.add(trendLineColorChoice);
		contentPanel.add(trendLineColorPreview);

		initializeOptionalFields();
	}

	@Override
	public void saveAdditionalPreferences() {
		String background = (String) backgroundColorChoice.getSelectedItem();
		String chartLine = (String) chartLineColorChoice.getSelectedItem();
		String trendLine = (String) trendLineColorChoice.getSelectedItem();
		IniFile.store(GUIConstants.BACKGROUND_COLOR, background);
		IniFile.store(GUIConstants.CHART_LINE_COLOR, chartLine);
		IniFile.store(GUIConstants.TREND_LINE_COLOR, trendLine);
		if (background == null || background.isBlank() || background.equals("default")) {
			background = GUIConstants.DEFAULT_BACKGROUND_COLOR;
		}
		if (chartLine == null || chartLine.isEmpty() || chartLine.equals("default")) {
			chartLine = GUIConstants.DEFAULT_CHART_LINE_COLOR;
		}
		if (trendLine == null || trendLine.isEmpty() || trendLine.equals("default")) {
			trendLine = GUIConstants.DEFAULT_TREND_LINE_COLOR;
		}
		WeightMonitorApplication.colorChoice = new ColorChoice(background, chartLine, trendLine);
	}

	private void initializeOptionalFields() {
		initializeColorChoice(backgroundColorChoice);
		if (!IniFile.value(GUIConstants.BACKGROUND_COLOR).isEmpty()) {
			backgroundColorChoice.setSelectedItem(IniFile.value(GUIConstants.BACKGROUND_COLOR));
		}
		initializeColorChoice(chartLineColorChoice);
		if (!IniFile.value(GUIConstants.CHART_LINE_COLOR).isEmpty()) {
			chartLineColorChoice.setSelectedItem(IniFile.value(GUIConstants.CHART_LINE_COLOR));
		}
		initializeColorChoice(trendLineColorChoice);
		if (!IniFile.value(GUIConstants.TREND_LINE_COLOR).isEmpty()) {
			trendLineColorChoice.setSelectedItem(IniFile.value(GUIConstants.TREND_LINE_COLOR));
		}
		previewBackground();
		previewChartLine();
		previewTrendLine();
	}

	private void initializeColorChoice(JComboBox<String> choice) {
		String[] colors = ColorProvider.name;
		choice.addItem("default");
		for (int i = 0; i < colors.length; i++) {
			choice.addItem(colors[i]);
		}
		choice.setSelectedIndex(0);
	}

	private Color getSelectedColor(JComboBox<String> choice, Color defaultColor) {
		Color result = null;
		if (choice.getSelectedItem().equals("default")) {
			result = defaultColor;
		} else {
			result = ColorProvider.get((String) choice.getSelectedItem());
		}
		return result;
	}

	private void previewBackground() {
		backgroundColor = getSelectedColor(backgroundColorChoice,
				ColorProvider.get(GUIConstants.DEFAULT_BACKGROUND_COLOR));
		backgroundColorPreview.setBackground(backgroundColor);
	}

	private void previewChartLine() {
		chartLineColor = getSelectedColor(chartLineColorChoice,
				ColorProvider.get(GUIConstants.DEFAULT_CHART_LINE_COLOR));
		chartLineColorPreview.setBackground(chartLineColor);
	}

	private void previewTrendLine() {
		trendLineColor = getSelectedColor(trendLineColorChoice,
				ColorProvider.get(GUIConstants.DEFAULT_TREND_LINE_COLOR));
		trendLineColorPreview.setBackground(trendLineColor);
	}

}
