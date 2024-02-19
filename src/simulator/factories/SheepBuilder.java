package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.SelectionStrategy;
import simulator.model.Animal;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {

	Factory<SelectionStrategy> _selection_strategy_factory;
	public SheepBuilder(Factory<SelectionStrategy> selection_strategy_factory) {
		super("sheep", "Sheep");
		this._selection_strategy_factory = selection_strategy_factory;
	}

	@Override
	protected Animal create_instance(JSONObject data) {
		Sheep sheep;
		SelectionStrategy mate_strategy;
		SelectionStrategy danger_strategy;
		Vector2D pos;
		
		JSONObject j_data = data.getJSONObject("data");
		if (j_data == null) {
			throw new IllegalArgumentException("Sheep data is null.");
		}
		// Build mating strategy
		Builder<SelectionStrategy> b_mate_strategy;
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
		Builder<? extends SelectionStrategy> b_danger_strategy;
		JSONObject j_danger_strategy = j_data.getJSONObject("danger_strategy");
		String danger_strategy_type = j_danger_strategy.getString("type");
		switch (danger_strategy_type) {
		case "closest":
			b_danger_strategy = new SelectClosestBuilder();
			break;
		case "youngest":
			b_danger_strategy = new SelectYoungestBuilder();
			break;
		case "first":
		default:
			b_danger_strategy = new SelectFirstBuilder();
			break;
		}
		danger_strategy = b_danger_strategy.create_instance(j_danger_strategy);
		
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
		
		sheep = new Sheep(mate_strategy, danger_strategy, pos);
		
		return (sheep);
	}

}
