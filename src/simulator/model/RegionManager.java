package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public class RegionManager implements AnimalMapView {
	private int _width;
	private int _height;
	private int _columns;
	private int _rows;
	private int _region_width;
	private int _region_height;
	
	private ArrayList<ArrayList<Region>> _regions;
	private HashMap<Animal, Region> _animal_region;
	
	public RegionManager(int cols, int rows, int width, int height) {
		this._columns = cols;
		this._rows = rows;
		this._width = width;
		this._height = height;
		
		this._region_width = width / cols;
		this._region_height = height / rows;
		
		// Inicializar matriz de regiones
		this._regions = new ArrayList<ArrayList<Region>>(cols);
		for (int r = 0; r < rows; r++) {
			ArrayList<Region> row = new ArrayList<Region>(rows);
			for (int c = 0; c < cols; c++) {
				row.add(new DefaultRegion());
			}
			this._regions.add(row);
		}
		
		// Inicializar mapa de regiones
		this._animal_region = new HashMap<Animal, Region>();
	}
	
	public void set_region(int row, int col, Region r) {
		Region current_region = this._regions.get(row).get(col);
		for (Animal a : current_region.get_animals()) {
			r.add_animal(a);
			this._animal_region.replace(a, current_region, r);
		}
		this._regions.get(row).set(col, r);
	}
	
	public void register_animal(Animal a) {
		if (!this._animal_region.containsKey(a)) {
			a.init(this);
		}
		
		int	x_index = (int)(a.get_position().getX() / this._region_width);
		int y_index = (int)(a.get_position().getY() / this._region_height);

		Region region = this._regions.get(y_index).get(x_index);
		region.add_animal(a);
		
		this._animal_region.put(a, region);
	}
	
	public void unregister_animal(Animal a) {
		int	x_index = (int) Math.floor(a.get_position().getX() / this._region_width);
		int y_index = (int) Math.floor(a.get_position().getY() / this._region_height);
		
		Region region = this._regions.get(y_index).get(x_index);
		region.remove_animal(a);
		
		this._animal_region.remove(a);
	}
	
	public void update_animal_region(Animal a) {
		int	x_index = (int) Math.floor(a.get_position().getX() / this._region_width);
		int y_index = (int) Math.floor(a.get_position().getY() / this._region_height);
		
		Region current_region = this._animal_region.get(a);
		Region actual_region = this._regions.get(y_index).get(x_index);
		if (current_region != actual_region) {
			actual_region.add_animal(a);
			current_region.remove_animal(a);
			
			this._animal_region.replace(a, current_region, actual_region);
		}
	}
	
	public double get_food(Animal a, double dt) {
		Region region = this._animal_region.get(a);
		
		return (region.get_food(a, dt));
	}
	
	public void update_all_regions(double dt) {
		for (ArrayList<Region> list : this._regions) {
			for (Region r : list) {
				r.update(dt);
			}
		}
	}
	
	public List<Animal> get_animals_in_range(Animal a, Predicate<Animal> filter) {
		ArrayList<Animal> animals_in_range = new ArrayList<Animal>();
		
		// Busca las esquinas arriba a la izquierda y abajo a la derecha
		Vector2D top_left = a.get_position().plus(new Vector2D(-a.get_sight_range(), -a.get_sight_range()));
		Vector2D bottom_right = a.get_position().plus(new Vector2D(a.get_sight_range(), a.get_sight_range()));
		// Saca los indices dentro de la matriz de donde hay que mirar
		int top_index = (int) Math.floor(top_left.getY() / this._region_height);
		int left_index = (int) Math.floor(top_left.getX() / this._region_width);
		int bottom_index = (int) Math.floor(bottom_right.getY() / this._region_height);
		int right_index = (int) Math.floor(bottom_right.getX() / this._region_width);
		
		// Comprobar que son indices validos
		if (top_index < 0)
			top_index = 0;
		if (left_index < 0)
			left_index = 0;
		if (bottom_index >= this._rows)
			bottom_index = this._rows - 1;
		if (right_index >= this._columns)
			right_index = this._columns - 1;

		for (int r = top_index; r <= bottom_index; r++) {
			for (int c = left_index; c < right_index; c++) {
				Region region = this._regions.get(r).get(c);
				for (Animal animal : region.get_animals()) {
					if (filter.test(animal) && a.get_position().distanceTo(animal.get_position()) <= a.get_sight_range()) {
						animals_in_range.add(animal);
					}
				}
			}
		}
		return (animals_in_range);
	}
	
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
		JSONArray regions = new JSONArray();
		for (int r = 0; r < this._rows; r++) {
			for (int c = 0; c < this._columns; c++) {
				JSONObject region = new JSONObject();
				region.put("row", r);
				region.put("col", c);
				region.put("data", this._regions.get(r).get(c).as_JSON());
				
				regions.put(region);
			}
		}
		json.put("regiones", regions);
		return (json);
	}

	@Override
	public int get_cols() {
		return (this._columns);
	}
	@Override
	public int get_rows() {
		return (this._rows);
	}
	@Override
	public int get_width() {
		return (this._width);
	}
	@Override
	public int get_height() {
		return (this._height);
	}
	@Override
	public int get_region_width() {
		return (this._region_width);
	}
	@Override
	public int get_region_height() {
		return (this._region_height);
	}
	
	@Override
	public Iterator<RegionData> iterator() {
		Iterator<RegionData> iterator = new Iterator<RegionData>() {
			private int col = 0;
			private int row = 0;
			
			@Override
			public boolean hasNext() {
				return (col < _columns && row < _rows);
			}
			
			@Override
			public RegionData next() {
				RegionData region_data = new RegionData(row, col, _regions.get(row).get(col));
				col++;
				if (col == _columns) {
					col = 0;
					row++;
				}
				return (region_data);
			}
		};
		
		return (iterator);
	}
}
