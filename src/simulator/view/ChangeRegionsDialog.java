package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	private JComboBox<String> _regionsComboBox;
	private JComboBox<String> _fromRowComboBox;
	private JComboBox<String> _toRowComboBox;
	private JComboBox<String> _fromColComboBox;
	private JComboBox<String> _toColComboBox;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status;
	
	ChangeRegionsDialog(Controller ctrl) {
		super((Frame)null, true);
		this._ctrl = ctrl;
		initGUI();
		this._ctrl.addObserver(this);
	}
	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		JPanel text_panel = new JPanel();
		JScrollPane table_panel = new JScrollPane();
		JPanel combobox_panel = new JPanel();
		JPanel button_panel = new JPanel();

		JLabel help_text = new JLabel("Select a region type, the rows/cols interval, and provide values for the parameters in the Value column (default values are used for parameters with no value).");
		text_panel.add(help_text);
		
		_regionsInfo = Main.region_factory.get_info();
		
		// _dataTableModel es un modelo de tabla que incluye todos los parámetros de
		// la region
		
		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return (column == 1);
			}
		};
		
		_dataTableModel.setColumnIdentifiers(_headers);

		JTable j_table = new JTable(_dataTableModel);
		table_panel.setViewportView(j_table);
		
		_regionsModel = new DefaultComboBoxModel<>();

		for (JSONObject o : _regionsInfo) {
			_regionsModel.addElement(o.getString("desc"));
		}

		_regionsComboBox = new JComboBox<>(_regionsModel);
		_regionsComboBox.addActionListener((e) -> regions_combo_changed(_regionsComboBox.getSelectedItem().toString(), j_table, _dataTableModel));
		combobox_panel.add(new JLabel("Region type: "));
		combobox_panel.add(_regionsComboBox);

		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();

		_fromRowComboBox = new JComboBox<>(_fromRowModel);
		_toRowComboBox = new JComboBox<>(_toRowModel);
		_fromColComboBox = new JComboBox<>(_fromColModel);
		_toColComboBox = new JComboBox<>(_toColModel);
		
		combobox_panel.add(new JLabel("Row from/to: "));
		combobox_panel.add(_fromRowComboBox);
		combobox_panel.add(_toRowComboBox);
		combobox_panel.add(new JLabel("Col from/to: "));
		combobox_panel.add(_fromColComboBox);
		combobox_panel.add(_toColComboBox);

		JButton cancel_button = new JButton();
		JButton ok_button = new JButton();
		
		cancel_button.setText("Cancel");
		cancel_button.addActionListener((e) -> cancel_button_pressed());
		
		ok_button.setText("OK");
		ok_button.addActionListener((e) -> ok_button_pressed(j_table));
		
		button_panel.add(cancel_button);
		button_panel.add(ok_button);
		
		mainPanel.add(text_panel);
		mainPanel.add(table_panel);
		mainPanel.add(combobox_panel);
		mainPanel.add(button_panel);
		
		setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
		pack();
		setResizable(false);
		setVisible(false);
	}
	
	public void open(Frame parent) {
		setLocation(//
		parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
		parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
	
	private void regions_combo_changed(String desc, JTable table, DefaultTableModel model) {
		JSONObject data = null;
		int count = 0;
		for (JSONObject o : this._regionsInfo) {
			if (desc.equals(o.get("desc"))) {
				data = o.getJSONObject("data");
				if (!data.isEmpty()) {
					count = data.length();
				}
				break;
			}
		}
		
		if (data != null) {
			model.setRowCount(count);
			Iterator<String> it = data.keys();
			int i = 0;
			while (it.hasNext()) {
				String key = it.next();
				table.setValueAt(key, i, 0);
				table.setValueAt(data.getJSONObject(key).get("desc"), i, 2);
				i++;
			}
		}
	}
	
	private void cancel_button_pressed() {
		this._status = 0;
		this.setVisible(false);
	}
	
	private void ok_button_pressed(JTable table) {
		JSONObject region_data = new JSONObject();
		for (int i = 0; i < table.getRowCount(); i++) {
			String key = table.getValueAt(i, 0).toString();
			double value;
			if (table.getValueAt(i, 1) != null) {
				value = Double.parseDouble(table.getValueAt(i, 1).toString());
				region_data.put(key, value);
			}
		}
		String region_type = Main.region_factory.get_info().get(_regionsComboBox.getSelectedIndex()).getString("type");
		
		int row_from = Integer.parseInt(this._fromRowComboBox.getSelectedItem().toString());
		int row_to = Integer.parseInt(this._toRowComboBox.getSelectedItem().toString());
		int col_from = Integer.parseInt(this._fromColComboBox.getSelectedItem().toString());
		int col_to = Integer.parseInt(this._toColComboBox.getSelectedItem().toString());
		
		JSONObject json = new JSONObject();
		JSONArray j_regions = new JSONArray();
		
		JSONArray j_row = new JSONArray();
		j_row.put(row_from);
		j_row.put(row_to);
		
		JSONArray j_col = new JSONArray();
		j_col.put(col_from);
		j_col.put(col_to);
		
		JSONObject j_spec = new JSONObject();
		j_spec.put("type", region_type);
		j_spec.put("data", region_data);
		
		JSONObject j_region = new JSONObject();
		j_region.put("row", j_row);
		j_region.put("col", j_col);
		j_region.put("spec", j_spec);
		
		j_regions.put(j_region);
		
		json.put("regions", j_regions);
		
		this._ctrl.set_regions(json);
		this._status = 1;
		this.setVisible(false);
	}
	
	private void set_comboBoxModel(DefaultComboBoxModel<String> model, int size) {
		model.removeAllElements();
		for (int i = 0; i < size; i++) {
			model.addElement(String.valueOf(i));
		}
	}
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		set_comboBoxModel(_fromRowModel, map.get_rows());
		set_comboBoxModel(_toRowModel, map.get_rows());
		set_comboBoxModel(_fromColModel, map.get_cols());
		set_comboBoxModel(_toColModel, map.get_cols());
	}
	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.onRegister(time, map, animals);
	}
	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}
	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
	}
}
