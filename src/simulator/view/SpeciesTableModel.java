package simulator.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	private int _cols;
	private int _rows;
	private int[][] _table;
	private ArrayList<String> _column_names;
	private ArrayList<String> _species;
	
	SpeciesTableModel(Controller ctrl) {
		this._column_names = new ArrayList<String>();
		this._species = new ArrayList<String>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return (this._rows);
	}

	@Override
	public int getColumnCount() {
		return (this._cols + 1);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return (this._species.get(rowIndex));
		}
		return (this._table[columnIndex - 1][rowIndex]);
	}
	
	@Override
	public String getColumnName(int columnIndex) {
		return (this._column_names.get(columnIndex));
	}
	
	private void resetTableValues() {
		for (int i = 0; i < this._cols - 1; i++) {
			for (int j = 0; j < this._rows; j++) {
				this._table[i][j] = 0;
			}
		}
	}
	
	private void updateTableValues(List<AnimalInfo> animals) {
		int species_index;
		int state_index;
		
		resetTableValues();
		for (AnimalInfo a : animals) {
			// Buscar que fila hay que actualizar
			for (species_index = 0; species_index < this._species.size(); species_index++) {
				if (this._species.get(species_index).equals(a.get_genetic_code())) {
					break;
				}
			}
			// Buscar que columna hay que actualizar
			for (state_index = 0; state_index < State.values().length; state_index++) {
				if (State.values()[state_index] == a.get_state()) {
					break;
				}
			}
			
			this._table[state_index][species_index]++;
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		this._cols = State.values().length;
		
		// HashSet no permite duplicados, por lo que si metemos todos los codigos geneticos, su
		// tamano final sera el numero de codigos geneticos presentes
		HashSet<String> set = new HashSet<>();
		for (AnimalInfo a : animals) {
			set.add(a.get_genetic_code());
		}
		for (String s : set) {
			this._species.add(s);
		}
		this._rows = set.size();
		
		this._column_names.add("Species");
		for (State s : State.values()) {
			this._column_names.add(s.toString());
		}
		
		this._table = new int[this._cols][this._rows];
		updateTableValues(animals);
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
		HashSet<String> set = new HashSet<>();
		for (AnimalInfo a : animals) {
			set.add(a.get_genetic_code());
		}
		this._rows = set.size();
		this._table = new int[this._cols][this._rows];
		updateTableValues(animals);
		this.fireTableDataChanged();
	}
}