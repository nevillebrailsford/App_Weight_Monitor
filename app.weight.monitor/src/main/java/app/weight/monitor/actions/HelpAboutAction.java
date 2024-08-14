package app.weight.monitor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import app.weight.monitor.application.IApplication;

public class HelpAboutAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private IApplication application;

	public HelpAboutAction(IApplication application) {
		super("About");
		this.application = application;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		application.helpAboutAction();
	}

}
