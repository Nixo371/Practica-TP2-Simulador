package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty()) {
			return (null);
		}
		Animal selected = as.get(0);
		double min_age = selected.get_age();
		for (Animal animal : as) {
			if (animal.get_age() < min_age) {
				selected = animal;
				min_age = animal.get_age();
			}
		}
		return (selected);
	}

}
