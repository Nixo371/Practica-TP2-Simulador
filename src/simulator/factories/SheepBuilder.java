package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.SelectionStrategy;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {

	private Factory<SelectionStrategy> _selection_strategy_factory;
	
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

		if (data == null) {
			throw new IllegalArgumentException("Sheep data is null.");
		}

		if (data.has("mate_strategy")) {
			JSONObject j_mate_strategy = data.getJSONObject("mate_strategy");
			mate_strategy = this._selection_strategy_factory.create_instance(j_mate_strategy);
		}
		else {
			mate_strategy = new SelectFirst();
		}
		
		if (data.has("danger_strategy")) {
			JSONObject j_danger_strategy = data.getJSONObject("danger_strategy");
			danger_strategy = this._selection_strategy_factory.create_instance(j_danger_strategy);
		}
		else {
			danger_strategy = new SelectFirst();
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

		sheep = new Sheep(mate_strategy, danger_strategy, pos);
		
		return (sheep);
	}
	
	@Override
	protected void fill_in_data(JSONObject o) {
		
	}

}
