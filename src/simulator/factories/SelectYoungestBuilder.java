package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

	public SelectYoungestBuilder() {
		super("youngest", "A selection strategy that chooses the youngest valid animal in the list.");
	}

	@Override
	protected SelectYoungest create_instance(JSONObject data) {
		String type = data.getString("type");
		if (!type.equals("youngest")) {
			throw new IllegalArgumentException("The JSON file passed does not have the correct 'type' field value.\nFound: " + type + "\nExpected: 'youngest'");	
		}
		return (new SelectYoungest());
	}

}
