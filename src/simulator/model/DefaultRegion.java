package simulator.model;

public class DefaultRegion extends Region {
	public DefaultRegion() {
		super();
	}
	
	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Diet.CARNIVORE) {
			return (0.0);
		}
		
		int n = 0;
		for (Animal animal : this.get_animals()) {
			if (animal.get_diet() == Diet.HERVIBORE) {
				n++;
			}
		}
		
		return (60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt);
	}

	@Override
	public void update(double dt) {
		// Nada
	}
}
