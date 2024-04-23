package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	private double _food;
	private double _factor;
	
	public DynamicSupplyRegion(double initial_food, double growth_factor) {
		super();
		this._food = initial_food;
		this._factor = growth_factor;
	}
	
	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Diet.CARNIVORE)
			return (0.0);
		
		int n = 0;
		for (Animal animal : this.get_animals()) {
			if (animal.get_diet() == Diet.HERVIBORE) {
				n++;
			}
		}
		
		double food = Math.min(_food, 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt);
		this._food -= food;
		
		return (food);
	}
	
	public String toString() {
		return ("dynamic");
	}
	
	public void update(double dt) {
		if (Utils._rand.nextDouble() < 0.5) {
			this._food += dt * this._factor;
		}
	}
}
