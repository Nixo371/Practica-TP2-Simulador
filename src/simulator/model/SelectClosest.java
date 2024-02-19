package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {

	public Animal select(Animal a, List<Animal> as) {
		if (as.isEmpty()) {
			return (null);
		}
		Animal selected = as.get(0);
		double min_distance = selected.get_position().distanceTo(a.get_position());
		for (Animal animal : as) {
			if (animal.get_position().distanceTo(a.get_position()) < min_distance) {
				selected = animal;
				min_distance = animal.get_position().distanceTo(a.get_position());
			}
		}
		return (selected);
	}

}
