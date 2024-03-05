package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	public DynamicSupplyRegionBuilder() {
		super("dynamicsupply", "Dynamic Supply Region");
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		DynamicSupplyRegion dynamic_supply_region;
		double factor;
		double food;

		try {
			factor = data.getDouble("factor");
		}
		catch (Exception e) {
			factor = 2.0;
		}
		
		try {
			food = data.getDouble("food");
		}
		catch (Exception e) {
			food = 1000.0;
		}
		
		dynamic_supply_region = new DynamicSupplyRegion(food, factor);
		return (dynamic_supply_region);
	}

}
