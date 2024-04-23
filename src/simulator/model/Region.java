package simulator.model;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {
	protected LinkedList<Animal> _animal_list;
	
	public Region() {
		this._animal_list = new LinkedList<Animal>();
	}
	
	final void add_animal(Animal a) {
		this._animal_list.add(a);
	}
	
	final void remove_animal(Animal a) {
		this._animal_list.remove(a);
	}
	
	final List<Animal> get_animals() {
		return (Collections.unmodifiableList(this._animal_list));
	}
	
	public List<AnimalInfo> getAnimalsInfo() {
		return (Collections.unmodifiableList(this._animal_list));
	}
	
	public abstract String toString();
	
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
		JSONArray animals = new JSONArray();
		for (Animal a : this.get_animals()) {
			animals.put(a.as_JSON());
		}
		json.put("animals", animals);
		return (json);
	}
}
