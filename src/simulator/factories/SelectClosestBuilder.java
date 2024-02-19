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
		String type = data.getString("type");
		if (!type.equals("closest")) {
			throw new IllegalArgumentException("The JSON file passed does not have the correct 'type' field value.\nFound: " + type + "\nExpected: 'closest'");	
		}
		return (new SelectClosest());
	}

}
