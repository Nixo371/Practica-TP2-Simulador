package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {
	
	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("Wolf", Diet.CARNIVORE, 50.0, 60.0, mate_strategy, pos);
		this._hunting_strategy = hunting_strategy;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this._hunt_target = null;
		this._hunting_strategy = p1._hunting_strategy;
	}
	
	public void update(double dt) {
		// <1> Si esta muerto, no hacer nada
		if (this.get_state() == State.DEAD) {
			return;
		}

		switch (this.get_state()) {
			case NORMAL:
				// 1. Avanzar el animal
				// (1.1) Si ha llegado al destino, seleccionar otro aleatorio
				if (this.get_position().distanceTo(this.get_destination()) < 8.0) {
					double x = Utils._rand.nextDouble(this._region_mngr.get_width());
					double y = Utils._rand.nextDouble(this._region_mngr.get_height());
					this.set_destination(new Vector2D(x, y));
				}
				// (1.2) Moverse hacia el destino
				this.move(this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
				// (1.3) Envejecer
				this.set_age(this.get_age() + dt);
				// (1.4) Gastar energia
				this.set_energy(this.get_energy() - (18.0 * dt));
				this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
				// (1.5) Desear mas
				this.set_desire(this.get_desire() + (30.0 * dt));
				this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));

				// 2. Cambio de estado
				// (2.1) Ver si tiene hambre o si quiere emparejarse
				if (this.get_energy() < 50.0) {
					this.set_state(State.HUNGER);
					this.set_mate_target(null);
				}
				else if (this.get_desire() > 65.0) {
					this.set_state(State.MATE);
					this._hunt_target = null;
				}
				break;
			case HUNGER:
				// 1. Cambiar de objectivo de caza si esta muerto o fuera de vision
				if (this._hunt_target == null || (this._hunt_target != null && 
				(this._hunt_target.get_state() == State.DEAD || this.get_position().distanceTo(this._hunt_target.get_position()) > this.get_sight_range()))) {
					Predicate<Animal> hervibores = (a) -> a.get_diet() == Diet.HERVIBORE;
					List<Animal> visibles = this._region_mngr.get_animals_in_range(this, hervibores);
					this._hunt_target = this._hunting_strategy.select(this, visibles);
				}
				
				// 2. Modificar comportamiento por nuevo objetivo
				if (this._hunt_target != null) {
					// (2.1) Cambiar destino a la nueva pareja
					this.set_destination(this._hunt_target.get_position());
					// (2.2) Moverse hacia el destino
					this.move(3.0 * this.get_speed() * dt * Math.exp((this.get_energy() - 100.00) * 0.007));
					// (2.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (2.4) Gastar energia
					this.set_energy(this.get_energy() - (18.0 * 1.2 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (2.5) Desear mas
					this.set_desire(this.get_desire() + (30.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
					// (2.6) Matar a su objetivo
					if (this.get_position().distanceTo(this._hunt_target.get_position()) < 8.0) {
						// (2.6.1) Matar al animal
						this._hunt_target.set_state(State.DEAD);
						// (2.6.2) Quitar el objetivo
						this._hunt_target = null;
						// (2.6.3) Sumarse energia
						this.set_energy(this.get_energy() + 50.0);
						this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					}
				}
				else {
					// x.1. Avanzar el animal
					// (x.1.1) Si ha llegado al destino, seleccionar otro aleatorio
					if (this.get_position().distanceTo(this.get_destination()) < 8.0) {
						double x = Utils._rand.nextDouble(this._region_mngr.get_width());
						double y = Utils._rand.nextDouble(this._region_mngr.get_height());
						this.set_destination(new Vector2D(x, y));
					}
					// (x.1.2) Moverse hacia el destino
					this.move(this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
					// (x.1.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (x.1.4) Gastar energia
					this.set_energy(this.get_energy() - (18.0 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (x.1.5) Desear mas
					this.set_desire(this.get_desire() + (30.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
				}
				
				// 3. Cambiar de estado
				// (3.1)
				if (this.get_energy() > 50.0) {
					// (3.1.1)
					if (this.get_desire() < 65.0) {
						this.set_state(State.NORMAL);
						this._hunt_target = null;
						this.set_mate_target(null);
					}
					// (3.1.2)
					else {
						this.set_state(State.MATE);
						this._hunt_target = null;
					}
				}

				break;
				
			case MATE:
				// 1. Eliminar pareja si esta muerta o fuera del campo de vision
				if (this.get_mate_target() != null && (this.get_mate_target().get_state() == State.DEAD
				|| this.get_position().distanceTo(this.get_mate_target().get_position()) > this.get_sight_range())) {
					this.set_mate_target(null);
				}
				
				// 2. Buscar nueva pareja si no existe ya
				if (this.get_mate_target() == null) {
					Predicate<Animal> wolf = (a) -> a.get_genetic_code().equals("Wolf");
					List<Animal> visibles = this._region_mngr.get_animals_in_range(this, wolf);
					this.set_mate_target(this.get_mate_strategy().select(this, visibles));
				}
				if (this.get_mate_target() != null) {
					// (2.1) Cambiar destino a la nueva pareja
					this.set_destination(this.get_mate_target().get_position());
					// (2.2) Moverse hacia el destino
					this.move(3.0 * this.get_speed() * dt * Math.exp((this.get_energy() - 100.00) * 0.007));
					// (2.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (2.4) Gastar energia
					this.set_energy(this.get_energy() - (18.0 * 1.2 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (2.5) Desear mas
					this.set_desire(this.get_desire() + (30.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
					// (2.6) Si es posible, emparjarse con su pareja
					if (this.get_position().distanceTo(this.get_mate_target().get_position()) < 8.0) {
						// (2.6.1) Resetear deseo
						this.set_desire(0.0);
						this.get_mate_target().set_desire(0.0);
						// (2.6.2) Crear bebe si no esta embarazado el animal y con 90% de probabilidad
						if (!this.is_pregnant()) {
							if (Utils._rand.nextDouble() < 0.9) {
								this.get_pregnant(new Wolf(this, this.get_mate_target()));
							}
						}
						// (2.6.3) Gastar energia
						this.set_energy(this.get_energy() - 10.0);
						this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
						// (2.6.4) Cambio de pareja
						this.set_mate_target(null);
					}
				}
				// No se ha encontrado una pareja
				else {
					// x.1. Avanzar el animal
					// (x.1.1) Si ha llegado al destino, seleccionar otro aleatorio
					if (this.get_position().distanceTo(this.get_destination()) < 8.0) {
						double x = Utils._rand.nextDouble(this._region_mngr.get_width());
						double y = Utils._rand.nextDouble(this._region_mngr.get_height());
						this.set_destination(new Vector2D(x, y));
					}
					// (x.1.2) Moverse hacia el destino
					this.move(this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
					// (x.1.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (x.1.4) Gastar energia
					this.set_energy(this.get_energy() - (20.0 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (x.1.5) Desear mas
					this.set_desire(this.get_desire() + (40.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
				}

				
				// 3. Cambiar estado
				if (this.get_energy() < 50.0) {
					this.set_state(State.HUNGER);
					this.set_mate_target(null);
				}
				else if (this.get_desire() < 65.0) {
					this.set_state(State.NORMAL);
					this._hunt_target = null;
					this.set_mate_target(null);
				}
				break;
			default:
				break;
		}

		// <3> Ajustar la posicion a que este en el mapa
		double x = this.get_position().getX();
		double y = this.get_position().getY();
		int width = this._region_mngr.get_width();
		int height = this._region_mngr.get_height();
		
		if (x < 0 || y < 0 || x >= width || y >= height) {
			this.set_state(State.NORMAL);
			this._hunt_target = null;
			this.set_mate_target(null);
		}
		this.set_position(this.adjust_position(this.get_position()));


		// <4> Ver si muere
		if (this.get_energy() <= 0.0) {
			this.set_state(State.DEAD);
		}
		if (this.get_age() > 14.0) {
			this.set_state(State.DEAD);
		}


		// <5> Comer
		if (this.get_state() != State.DEAD) {
			this.set_energy(this.get_energy() + this._region_mngr.get_food(this, dt));
			this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
		}
		
	}

}
