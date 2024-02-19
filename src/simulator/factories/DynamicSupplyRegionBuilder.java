package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;

public class DynamicSupplyRegionBuilder extends Builder<DynamicSupplyRegion> {

	public DynamicSupplyRegionBuilder() {
		super("default", "Dynamic Supply Region");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		DynamicSupplyRegion dynamic_supply_region;
		double factor;
		double food;
		if (!data.get("type").equals("default")) {
			throw new IllegalArgumentException("The JSON file passed does not have the correct 'type' field value.\nFound: " + data.get("type") + "\nExpected: 'default'");
		}
		JSONObject j_data = data.getJSONObject("data");
		try {
			factor = j_data.getDouble("factor");
		}
		catch (Exception e) {
			factor = 2.0;
		}
		
		try {
			food = j_data.getDouble("food");
		}
		catch (Exception e) {
			food = 1000.0;
		}
		
		dynamic_supply_region = new DynamicSupplyRegion(food, factor);
		return (dynamic_supply_region);
	}

}
