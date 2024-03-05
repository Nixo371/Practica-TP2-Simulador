package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {

	public SelectClosestBuilder() {
		super("closest", "A selection strategy that chooses the closest valid animal in the list.");
	}

	@Override
	protected SelectClosest create_instance(JSONObject data) {
		return (new SelectClosest());
	}

}
