package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {
	private Controller _ctrl;
	private JLabel _time;
	private JLabel _animal_count;
	private JLabel _dimension;
	
	StatusBar(Controller ctrl) {
		this.initGUI();
		ctrl.addObserver(this);
		this._ctrl = ctrl;
	}
	
	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		
		this._time = new JLabel();
		this._animal_count = new JLabel();
		this._dimension = new JLabel();
		
		this._time.setText("Time: 0");
		this._animal_count.setText("Total Animals: 0");
		this._dimension.setText("Dimension: 800x600 20x15");
		
		JSeparator s = new JSeparator(JSeparator.VERTICAL);
		s.setPreferredSize(new Dimension(10, 20));
		
		this.add(this._time);
			this.add(s);
		this.add(this._animal_count);
			this.add(s);
		this.add(this._dimension);
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this._time.setText("Time: " + time);
		this._animal_count.setText("Total Animals: " + animals.size());
		
		StringBuilder sb = new StringBuilder();
		sb.append("Dimension: ");
		
		sb.append(map.get_width());
		sb.append("x");
		sb.append(map.get_height());
		
		sb.append(" ");
		
		sb.append(map.get_cols());
		sb.append("x");
		sb.append(map.get_rows());
		
		this._dimension.setText(sb.toString());
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		this._time.setText("Time: " + time);
		this._animal_count.setText("Total Animals: " + animals.size());
	}
}
