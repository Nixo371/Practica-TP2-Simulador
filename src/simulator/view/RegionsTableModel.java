package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	private int _cols;
	private int _rows;
	private int _r_cols;
	private int _r_rows;
	private int[][] _table;
	private ArrayList<String> _column_names;
	private ArrayList<MapInfo.RegionData> _regions;
	private ArrayList<Diet> _diets;
	
	RegionsTableModel(Controller ctrl) {
		this._column_names = new ArrayList<String>();
		this._regions = new ArrayList<MapInfo.RegionData>();
		this._diets = new ArrayList<Diet>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return (this._rows);
	}

	@Override
	public int getColumnCount() {
		return (this._cols);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int row = rowIndex / this._r_cols;
		int col = rowIndex % this._r_cols;
		
		if (columnIndex == 0) return (row);
		if (columnIndex == 1) return (col);
		
		MapInfo.RegionData r = null;
		for (MapInfo.RegionData a : this._regions) {
			if (a.col() == col && a.row() == row) {
				r = a;
				break;
			}
		}
		if (r == null) return (null);
		
		if (columnIndex == 2) return (r.r().toString());
		
		List<AnimalInfo> animal_list = r.r().getAnimalsInfo();
		Diet diet = this._diets.get(columnIndex - 3);
		int count = 0;
		for (AnimalInfo a : animal_list) {
			if (a.get_diet() == diet) {
				count++;
			}
		}
		return (count);
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return (this._column_names.get(columnIndex));
	}
	
	private void resetTableValues() {
		for (int i = 0; i < this._cols - 3; i++) {
			for (int j = 0; j < this._rows; j++) {
				this._table[j][i] = 0;
			}
		}
	}
	
	private void updateTableValues(MapInfo map) {
		int diet_index;
		int region_index;
		
		resetTableValues();
		for (MapInfo.RegionData r : map) {
			region_index = (r.row() * this._r_cols) + r.col();
			for (AnimalInfo a : r.r().getAnimalsInfo()) {
				diet_index = this._diets.indexOf(a.get_diet());
				this._table[region_index][diet_index]++;
			}
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this._cols = 3;
		this._rows = 0;
		
		for (MapInfo.RegionData r : map) {
			this._regions.add(r);
			this._rows++;
		}
		this._r_cols = map.get_cols();
		this._r_rows = map.get_rows();
		
		this._column_names.add("Row");
		this._column_names.add("Col");
		this._column_names.add("Desc.");
		
		for (Diet d : Diet.values()) {
			this._diets.add(d);
			this._column_names.add(d.toString());
			this._cols++;
		}
		
		this._table = new int[this._rows][this._cols - 3];
		updateTableValues(map);
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
		for (int i = 0; i < this._regions.size(); i++) {
			if (this._regions.get(i).row() == row && this._regions.get(i).col() == col) {
				this._regions.set(i, new MapInfo.RegionData(row, col, r));
			}
		}
		this.fireTableDataChanged();
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		updateTableValues(map);
		this.fireTableDataChanged();
	}
}
