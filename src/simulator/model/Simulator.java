package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;

public class Simulator implements JSONable {
	private Factory<Animal> _animals_factory;
	private Factory<Region> _regions_factory;
	private RegionManager _region_manager;
	List<Animal> _animal_list;
	double _time;
	
	public Simulator(int cols, int rows, int width, int height,
			Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;
		
		this._region_manager = new RegionManager(cols, rows, width, height);
		this._animal_list = new LinkedList<Animal>();
		this._time = 0.0;
	}

	private void set_region(int row, int col, Region r) {
		this._region_manager.set_region(row, col, r);
	}
	
	public void set_region(int row, int col, JSONObject r_json) {
		Region new_region = this._regions_factory.create_instance(r_json);
		this.set_region(row, col, new_region);
	}

	private void add_animal(Animal a) {
		this._animal_list.add(a);
		this._region_manager.register_animal(a);
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
				babies.add(a.deliver_baby());
			}
		}
		for (Animal a : babies) {
			this.add_animal(a);
		}
	}
	
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
		
		json.put("time", this._time);
		json.put("state", this._region_manager.as_JSON());
		
		return (json);
	}
}
