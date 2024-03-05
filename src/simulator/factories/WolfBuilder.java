package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {

	private Factory<SelectionStrategy> _selection_strategy_factory;
	
	public WolfBuilder(Factory<SelectionStrategy> selection_strategy_factory) {
		super("wolf", "Wolf");
		this._selection_strategy_factory = selection_strategy_factory;
	}

	@Override
	protected Wolf create_instance(JSONObject data) {
		Wolf wolf;
		SelectionStrategy mate_strategy;
		SelectionStrategy hunt_strategy;
		Vector2D pos;
	
		if (data == null) {
			throw new IllegalArgumentException("Wolf data is null.");
		}
		
		if (data.has("mate_strategy")) {
			JSONObject j_mate_strategy = data.getJSONObject("mate_strategy");
			mate_strategy = this._selection_strategy_factory.create_instance(j_mate_strategy);
		}
		else {
			mate_strategy = new SelectFirst();
		}
		
		if (data.has("hunt_strategy")) {
			JSONObject j_hunt_strategy = data.getJSONObject("hunt_strategy");
			hunt_strategy = this._selection_strategy_factory.create_instance(j_hunt_strategy);
		}
		else {
			hunt_strategy = new SelectFirst();
		}
		
		// Make position
		if (data.has("pos")) {
			JSONObject j_pos = data.getJSONObject("pos");
			JSONArray j_x_range = j_pos.getJSONArray("x_range");
			JSONArray j_y_range = j_pos.getJSONArray("y_range");
			
			double x_low = j_x_range.getDouble(0);
			double x_high = j_x_range.getDouble(1);
			double y_low = j_y_range.getDouble(0);
			double y_high = j_y_range.getDouble(1);
			
			double x = Utils._rand.nextDouble(x_low, x_high);
			double y = Utils._rand.nextDouble(y_low, y_high);
			
			pos = new Vector2D(x, y);
		}
		else {
			pos = null;
		}
		
		wolf = new Wolf(mate_strategy, hunt_strategy, pos);
		
		return (wolf);
	}

}
