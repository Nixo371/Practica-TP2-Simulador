package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame {
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		this._ctrl = ctrl;
		this.initGUI();
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		
		ControlPanel control_panel = new ControlPanel(this._ctrl);
		mainPanel.add(control_panel, BorderLayout.PAGE_START);
		
		StatusBar status_bar = new StatusBar(this._ctrl);
		mainPanel.add(status_bar, BorderLayout.PAGE_END);
		
		// Definición del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		
		InfoTable species_table = new InfoTable("Species", new SpeciesTableModel(this._ctrl));
		species_table.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(species_table);

		InfoTable region_table = new InfoTable("Regions", new RegionsTableModel(this._ctrl));
		region_table.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(region_table);
		
		// TODO addWindowListener( … );
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}
}
