package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.misc.Vector2D;

public class Simulator implements JSONable, Observable<EcoSysObserver> {
	private Factory<Animal> _animals_factory;
	private Factory<Region> _regions_factory;
	private RegionManager _region_manager;
	List<Animal> _animal_list;
	List<EcoSysObserver> _observer_list;
	double _time;
	
	public Simulator(int cols, int rows, int width, int height,
			Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;
		
		this._region_manager = new RegionManager(cols, rows, width, height);
		this._animal_list = new LinkedList<Animal>();
		this._observer_list = new LinkedList<EcoSysObserver>();
		this._time = 0.0;
	}

	private void set_region(int row, int col, Region r) {
		this._region_manager.set_region(row, col, r);

		notify_on_region_set(row, col, r);
	}
	
	public void set_region(int row, int col, JSONObject r_json) {
		Region new_region = this._regions_factory.create_instance(r_json);
		this.set_region(row, col, new_region);
	}

	private void add_animal(Animal a) {
		this._animal_list.add(a);
		this._region_manager.register_animal(a);
		notify_on_animal_added(a);
	}
	
	public void add_animal(JSONObject a_json) {
		Animal new_animal = this._animals_factory.create_instance(a_json);
		this.add_animal(new_animal);
	}
	
	public MapInfo get_map_info() {
		return (this._region_manager);
	}
	public List<? extends AnimalInfo> get_animals() {
		return (Collections.unmodifiableList(this._animal_list));
	}
	public double get_time() {
		return (this._time);
	}
	
	public void advance(double dt) {
		this._time += dt;
		
		ArrayList<Animal> dead_animals = new ArrayList<Animal>();
		for (Animal a : this._animal_list) {
			if (a.get_state() == State.DEAD) {
				dead_animals.add(a);
			}
		}
		for (Animal a : dead_animals) {
			this._animal_list.remove(a);
			this._region_manager.unregister_animal(a);
		}
		
		for (Animal a : this._animal_list) {
			a.update(dt);
			this._region_manager.update_animal_region(a);
		}
		this._region_manager.update_all_regions(dt);
		
		ArrayList<Animal> babies = new ArrayList<Animal>();
		for (Animal a : this._animal_list) {
			if (a.is_pregnant()) {
				Animal baby = a.deliver_baby();
				babies.add(baby);
			}
		}
		for (Animal a : babies) {
			double x = a.get_position().getX();
			double y = a.get_position().getY();
			int width = this._region_manager.get_width();
			int height = this._region_manager.get_height();

			while (x >= width) {
				x = (x - width);
			}
			while (x < 0) {
				x = (x + width);
			}
			while (y >= height) {
				y = (y - height);
			}
			while (y < 0) {
				y = (y + height);
			}
			
			a.set_position(new Vector2D(x, y));
			this.add_animal(a);
		}
		
		notify_on_advanced(dt);
	}
	
	public void reset(int cols, int rows, int width, int height) {
		this._animal_list.clear();
		this._region_manager = new RegionManager(cols, rows, width, height);
		this._time = 0;
		
		notify_on_reset();
	}
	
	public void addObserver(EcoSysObserver o) {
		this._observer_list.add(o);
		o.onRegister(this._time, this._region_manager, new ArrayList<>(this._animal_list));
	}
	
	public void removeObserver(EcoSysObserver o) {
		this._observer_list.remove(o);
	}
	
	private void notify_on_register() {
		
	}
	private void notify_on_reset() {
		List<AnimalInfo> animals = new ArrayList<>(this._animal_list);
		for (EcoSysObserver o : this._observer_list) {
			o.onReset(this._time, this._region_manager, animals);
		}
	}
	private void notify_on_animal_added(Animal a) {
		List<AnimalInfo> animals = new ArrayList<>(this._animal_list);
		for (EcoSysObserver o : this._observer_list) {
			o.onAnimalAdded(this._time, this._region_manager, animals, a);
		}
	}
	private void notify_on_region_set(int row, int col, Region r) {
		for (EcoSysObserver o : this._observer_list) {
			o.onRegionSet(row, col, this._region_manager, r);
		}
	}
	private void notify_on_advanced(double dt) {
		List<AnimalInfo> animals = new ArrayList<>(this._animal_list);
		for (EcoSysObserver o : this._observer_list) {
			o.onAvanced(this._time, this._region_manager, animals, dt);
		}
	}
	
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
		
		json.put("time", this._time);
		json.put("state", this._region_manager.as_JSON());
		
		return (json);
	}
}
