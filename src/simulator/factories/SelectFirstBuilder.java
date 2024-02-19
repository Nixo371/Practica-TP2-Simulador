package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder() {
		super("first", "A selection strategy that chooses the first valid animal in the list.");
	}

	@Override
	protected SelectFirst create_instance(JSONObject data) {
		String type = data.getString("type");
		if (!type.equals("first")) {
			throw new IllegalArgumentException("The JSON file passed does not have the correct 'type' field value.\nFound: " + type + "\nExpected: 'first'");	
		}
		return (new SelectFirst());
	}

}
