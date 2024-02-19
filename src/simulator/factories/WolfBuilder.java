package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {

	Factory<SelectionStrategy> _selection_strategy_factory;
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
		// TODO "type"
		JSONObject j_data = data.getJSONObject("data");
		if (j_data == null) {
			throw new IllegalArgumentException("Wolf data is null.");
		}
		// Build mating strategy
		Builder<? extends SelectionStrategy> b_mate_strategy;
		JSONObject j_mate_strategy = j_data.getJSONObject("mate_strategy");
		String mate_strategy_type = j_mate_strategy.getString("type");
		switch (mate_strategy_type) {
		case "closest":
			b_mate_strategy = new SelectClosestBuilder();
			break;
		case "youngest":
			b_mate_strategy = new SelectYoungestBuilder();
			break;
		case "first":
		default:
			b_mate_strategy = new SelectFirstBuilder();
			break;
		}
		mate_strategy = b_mate_strategy.create_instance(j_mate_strategy);
		
		// Build danger strategy
		Builder<? extends SelectionStrategy> b_hunt_strategy;
		JSONObject j_hunt_strategy = j_data.getJSONObject("hunt_strategy");
		String hunt_strategy_type = j_hunt_strategy.getString("type");
		switch (hunt_strategy_type) {
		case "closest":
			b_hunt_strategy = new SelectClosestBuilder();
			break;
		case "youngest":
			b_hunt_strategy = new SelectYoungestBuilder();
			break;
		case "first":
		default:
			b_hunt_strategy = new SelectFirstBuilder();
			break;
		}
		hunt_strategy = b_hunt_strategy.create_instance(j_hunt_strategy);
		
		// Make position
		JSONObject j_pos = j_data.getJSONObject("pos");
		if (j_pos == null) {
			// Si no existe este campo, la posicion es null
			pos = null;
		}
		else {
			JSONArray j_x_range = j_pos.getJSONArray("x_range");
			JSONArray j_y_range = j_pos.getJSONArray("y_range");
			
			double x_low = j_x_range.getDouble(0);
			double x_high = j_x_range.getDouble(1);
			double y_low = j_y_range.getDouble(0);
			double y_high = j_y_range.getDouble(1);
			
			double x = Utils._rand.nextDouble(x_high - x_low) + x_low;
			double y = Utils._rand.nextDouble(y_high - y_low) + y_low;
			
			pos = new Vector2D(x, y);
		}
		
		wolf = new Wolf(mate_strategy, hunt_strategy, pos);
		
		return (wolf);
	}

}
