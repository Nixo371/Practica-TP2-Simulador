package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;

public class DefaultRegionBuilder extends Builder<DefaultRegion> {

	public DefaultRegionBuilder() {
		super("default", "Default Region");
	}

	@Override
	protected DefaultRegion create_instance(JSONObject data) {
		if (!data.get("type").equals("default")) {
			throw new IllegalArgumentException("The JSON file passed does not have the correct 'type' field value.\nFound: " + data.get("type") + "\nExpected: 'default'");
		}
		return (new DefaultRegion());
	}
	
	

}
