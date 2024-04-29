package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	private double default_factor;
	private final String factor_desc = "food increase factor (optional, default 2.0";
	private double default_food;
	private final String food_desc = "initial amount of food (optional, default 1000.0";
	
	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic Supply Region");
		this.default_factor = 2.0;
		this.default_food = 1000.0;
	}

	@Override
	protected DynamicSupplyRegion create_instance(JSONObject data) {
		DynamicSupplyRegion dynamic_supply_region;
		
		double factor = this.default_factor;
		double food = this.default_food;

		if (data.has("factor")) {
			factor = data.getDouble("factor");
		}
		
		if (data.has("food")) {
			food = data.getDouble("food");
		}
		
		dynamic_supply_region = new DynamicSupplyRegion(food, factor);
		return (dynamic_supply_region);
	}
	
	protected void fill_in_data(JSONObject o) {
		JSONObject j_factor = new JSONObject();
		j_factor.put("value", default_factor);
		j_factor.put("desc", factor_desc);
		
		JSONObject j_food = new JSONObject();
		j_food.put("value", default_food);
		j_food.put("desc", food_desc);
		
		o.put("factor", j_factor);
		o.put("food", j_food);
	}

}
