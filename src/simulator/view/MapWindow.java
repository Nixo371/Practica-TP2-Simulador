package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class MapWindow extends JFrame implements EcoSysObserver {
	private Controller _ctrl;
	private AbstractMapViewer _viewer;
	private Frame _parent;
	
	MapWindow(Frame parent, Controller ctrl) {
		super("[MAP VIEWER]");
		_ctrl = ctrl;
		_parent = parent;
		intiGUI();
		this._ctrl.addObserver(this);
	}
	
	private void intiGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());

		setContentPane(mainPanel);

		this._viewer = new MapViewer();
		mainPanel.add(this._viewer, BorderLayout.CENTER);
		pack();
		if (_parent != null)
			setLocation(
			_parent.getLocation().x + _parent.getWidth()/2 - getWidth()/2,
			_parent.getLocation().y + _parent.getHeight()/2 - getHeight()/2);
		setResizable(false);
		setVisible(true);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> { this._viewer.reset(time, map, animals); pack(); });
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> { this._viewer.reset(time, map, animals); pack(); }); 
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> { this._viewer.update(animals, time); });
	}
}
