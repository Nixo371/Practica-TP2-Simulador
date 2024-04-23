package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.misc.Utils;

class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _fileButton;
	private JButton _viewerButton;
	private JButton _regionsButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JButton _quitButton;
	private JSpinner _steps;
	private JTextField _dt;
	
	// TODO añade más atributos aquí …
	
	ControlPanel(Controller ctrl) {
		this._ctrl = ctrl;
		this.initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);
		
		// TODO crear los diferentes botones/atributos y añadirlos a _toolaBar.
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// _toolaBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten.
		
		// File Button
		this._toolaBar.add(Box.createGlue());
		this._toolaBar.addSeparator();
		this._fileButton = new JButton();
		this._fileButton.setToolTipText("File");
		this._fileButton.setIcon(new ImageIcon("resources/icons/open.png"));
		this._fileButton.addActionListener((e) -> open_button_pressed());
		this._toolaBar.add(this._fileButton);
		
		// Viewer Button
		this._toolaBar.add(Box.createGlue());
		this._viewerButton = new JButton();
		this._viewerButton.setToolTipText("Viewer");
		this._viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		this._viewerButton.addActionListener((e) -> new MapWindow(ViewUtils.getWindow(this), this._ctrl));
		this._toolaBar.add(this._viewerButton);
		
		// Regions Button
		this._toolaBar.add(Box.createGlue());
		this._toolaBar.addSeparator();
		this._regionsButton = new JButton();
		this._regionsButton.setToolTipText("Regions");
		this._regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		this._regionsButton.addActionListener((e) -> this._changeRegionsDialog.open(ViewUtils.getWindow(this)));
		this._toolaBar.add(this._regionsButton);
		
		// Run Button
		this._toolaBar.add(Box.createGlue());
		this._runButton = new JButton();
		this._runButton.setToolTipText("Run");
		this._runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		this._runButton.addActionListener((e) -> run_button_pressed());
		this._toolaBar.add(this._runButton);
		
		// Stop Button
		this._toolaBar.add(Box.createGlue());
		this._toolaBar.addSeparator();
		this._stopButton = new JButton();
		this._stopButton.setToolTipText("Viewer");
		this._stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		this._stopButton.addActionListener((e) -> run_sim(0, 0)); // TODO
		this._toolaBar.add(this._stopButton);
		
		// Steps Input
		this._toolaBar.add(Box.createGlue());
		this._toolaBar.add(new JLabel("Steps:"));
		this._toolaBar.add(Box.createGlue());
		this._steps = new JSpinner(new SpinnerNumberModel(10000, 0, 100000, 100));
		this._steps.setPreferredSize(new Dimension(80, 40));
		this._steps.setToolTipText("Steps");		
		this._toolaBar.add(this._steps);
		
		// Delta-Time Input

		this._toolaBar.add(Box.createGlue());
		this._toolaBar.add(new JLabel("Delta-Time:"));
		this._toolaBar.add(Box.createGlue());
		this._dt = new JTextField();
		this._dt.setToolTipText("Delta-Time");
		this._dt.setText("0.03");
		this._toolaBar.add(this._dt);
		
		// Quit Button
		this._toolaBar.add(Box.createGlue()); // this aligns the button to the right
		this._toolaBar.addSeparator();
		this._quitButton = new JButton();
		this._quitButton.setToolTipText("Quit");
		this._quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		this._quitButton.addActionListener((e) -> ViewUtils.quit(this));
		this._toolaBar.add(this._quitButton);
		
		this._fc = new JFileChooser();
		this._fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

		// TODO Inicializar _changeRegionsDialog con instancias del diálogo de cambio
		// de regiones
		this._changeRegionsDialog = new ChangeRegionsDialog(this._ctrl);
	}
	
	private void open_button_pressed() {
		this._fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		int s = this._fc.showOpenDialog(ViewUtils.getWindow(this));
		
		if (s == JFileChooser.APPROVE_OPTION) {
			File file = this._fc.getSelectedFile();
			try {
				InputStream is = new FileInputStream(file);
				JSONObject json = new JSONObject(new JSONTokener(is));
				
				int width = json.getInt("width");
				int height = json.getInt("height");
				int rows = json.getInt("rows");
				int cols = json.getInt("cols");
				
				this._ctrl.reset(cols, rows, width, height);
				this._ctrl.load_data(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void run_button_pressed() {
		// TODO:(1) deshabilita todos los botones excepto el botón de stop , y cambia
				// el valor del atributo _stopped a false; (2) saca el valor del delta-time del correspondiente
				// JTextField; y (3) llama al método run_sim con el valor actual de pasos, especificado en el
				// correspondiente JSpinner:
		this._fileButton.setEnabled(false);
		this._viewerButton.setEnabled(false);
		this._regionsButton.setEnabled(false);
		this._runButton.setEnabled(false);
		this._quitButton.setEnabled(false);
		
		this._stopped = false;
		
		int steps = (int) this._steps.getValue();
		double dt = Double.parseDouble(this._dt.getText());
		run_sim(steps, dt);
	}
	
	private void run_sim(int n, double dt) {
		if (n > 0 && !this._stopped) {
			try {
				this._ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			}
			catch (Exception e) {
				e.printStackTrace();
				// TODO: ViewUtils.showErrorMsg(e.getMessage());

				this._fileButton.setEnabled(true);
				this._viewerButton.setEnabled(true);
				this._regionsButton.setEnabled(true);
				this._runButton.setEnabled(true);
				this._stopButton.setEnabled(true);
				this._quitButton.setEnabled(true);
				
				this._stopped = true;
			}
		}
		else {
			this._fileButton.setEnabled(true);
			this._viewerButton.setEnabled(true);
			this._regionsButton.setEnabled(true);
			this._runButton.setEnabled(true);
			this._stopButton.setEnabled(true);
			this._quitButton.setEnabled(true);
			
			this._stopped = true;
		}
	}
}
