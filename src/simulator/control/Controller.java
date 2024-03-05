package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	private Simulator _sim;
	
	public Controller(Simulator sim) {
		this._sim = sim;
	}
	
	public void load_data(JSONObject data) {
		JSONArray j_animals = data.getJSONArray("animals");
		
		// regions
		if (data.has("regions")) {
			JSONArray j_regions = data.getJSONArray("regions");
			for (int i = 0; i < j_regions.length(); i++) {
				JSONObject j_region_item = j_regions.getJSONObject(i);
				int rf = j_region_item.getJSONArray("row").getInt(0);
				int rt = j_region_item.getJSONArray("row").getInt(1);
				int cf = j_region_item.getJSONArray("col").getInt(0);
				int ct = j_region_item.getJSONArray("col").getInt(1);
				JSONObject j_region = j_region_item.getJSONObject("spec");
				for (int r = rf; r <= rt; r++) {
					for (int c = cf; r <= ct; c++) {
						this._sim.set_region(r, c, j_region);
					}
				}
				i++;
			}
		}
		
		// animals
		for (int i = 0; i < j_animals.length(); i++) {
			JSONObject j_animal_item = j_animals.getJSONObject(i);
			int amount = j_animal_item.getInt("amount");
			JSONObject j_animal = j_animal_item.getJSONObject("spec");
			for (int a = 0; a < amount; a++) {
				this._sim.add_animal(j_animal);
			}
		}
	}
	
	private static List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
		ol.add(new ObjInfo(a.get_genetic_code(),
		(int) a.get_position().getX(),
		(int) a.get_position().getY(), (int)Math.round(a.get_age()) + 2));
		return ol;
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		SimpleObjectViewer view = null;
			if (sv) {
				MapInfo m = _sim.get_map_info();
				view = new SimpleObjectViewer("[ECOSYSTEM]",
				m.get_width(), m.get_height(),
				m.get_cols(), m.get_rows());
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
			}
		JSONObject json = new JSONObject();
		json.put("in", this._sim.as_JSON());
		while (this._sim.get_time() < t) {
			this._sim.advance(dt);
			if (sv) {
				view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
			}
		}
		json.put("out", this._sim.as_JSON());
		PrintStream p = new PrintStream(out);
		p.println(json.toString());
		
		if (sv) {
			view.close();
		}
	}

}
