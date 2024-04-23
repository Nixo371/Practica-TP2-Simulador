package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic Supply Region");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		DynamicSupplyRegion dynamic_supply_region;
		double factor;
		double food;

		if (data.has("factor")) {
			factor = data.getDouble("factor");
		}
		else {
			factor = 2.0;
		}
		
		if (data.has("food")) {
			food = data.getDouble("food");
		}
		else {
			food = 1000.0;
		}
		
		dynamic_supply_region = new DynamicSupplyRegion(food, factor);
		return (dynamic_supply_region);
	}
	
	protected void fill_in_data(JSONObject o) {
		
	}

}
