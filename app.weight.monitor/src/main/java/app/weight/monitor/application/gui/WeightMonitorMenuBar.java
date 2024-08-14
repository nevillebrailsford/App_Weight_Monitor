package app.weight.monitor.application.gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import app.weight.monitor.actions.ActionFactory;
import app.weight.monitor.application.IApplication;

public class WeightMonitorMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	private JMenu fileMenu = new JMenu("File");
	private JMenuItem preferencesItem = null;
	private JMenuItem exitItem = null;

	private JMenu editMenu = new JMenu("Edit");
	private JMenuItem undoItem = null;
	private JMenuItem redoItem = null;
	private JMenuItem copyItem = null;
	private JMenuItem pasteItem = null;
	private JMenuItem deleteItem = null;

	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem aboutItem = null;

	public WeightMonitorMenuBar(IApplication application) {
		preferencesItem = new JMenuItem(ActionFactory.instance(application).preferencesAction());
		exitItem = new JMenuItem(ActionFactory.instance(application).exitAction());
		fileMenu.add(preferencesItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		add(fileMenu);

		undoItem = new JMenuItem(ActionFactory.instance(application).undoAction());
		redoItem = new JMenuItem(ActionFactory.instance(application).redoAction());
		copyItem = new JMenuItem(ActionFactory.instance(application).copyAction());
		pasteItem = new JMenuItem(ActionFactory.instance(application).pasteAction());
		deleteItem = new JMenuItem(ActionFactory.instance(application).deleteAction());
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		editMenu.addSeparator();
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(deleteItem);
		add(editMenu);

		aboutItem = new JMenuItem(ActionFactory.instance(application).helpAboutAction());
		helpMenu.add(aboutItem);
		add(helpMenu);
	}
}
