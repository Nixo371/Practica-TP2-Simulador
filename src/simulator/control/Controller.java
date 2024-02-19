package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Simulator;

public class Controller {
	private Simulator _sim;
	
	public Controller(Simulator sim) {
		this._sim = sim;
	}
	
	public void load_data(JSONObject data) {
		JSONArray j_animals = data.getJSONArray("animals");
		JSONArray j_regions = data.getJSONArray("regions");
		
		if (j_regions == null) {
			// TODO
		}
		// regions
		int i = 0;
		JSONObject j_region = j_regions.getJSONObject(i);
		while (j_region != null) {
			// TODO
			i++;
			j_region = j_regions.getJSONObject(i);
		}
		
		// animals
		i = 0;
		JSONObject j_animal = j_animals.getJSONObject(i);
		while (j_animal != null) {
			// TODO
			i++;
			j_animal = j_regions.getJSONObject(i);
		}
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		JSONObject json = new JSONObject();
		json.put("in", this._sim.as_JSON());
		while (this._sim.get_time() < t) {
			this._sim.advance(dt);
			if (sv) {
				// TODO show on screen
			}
		}
		json.put("out", this._sim.as_JSON());
		PrintStream p = new PrintStream(out);
		p.println(json.toString());
	}

}
