package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("Sheep", Diet.HERVIBORE, 40.0, 35.0, mate_strategy, pos);
		this._danger_strategy = danger_strategy;
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this._danger_source = null;
		this._danger_strategy = p1._danger_strategy;
	}

	public void update(double dt) {
		if (this.get_state() == State.DEAD) {
			return;
		}

		switch (this.get_state()) {
			case NORMAL:
				// 1. Avanzar el animal
				// (1.1) Si ha llegado al destino, seleccionar otro aleatorio
				if (this.get_position().distanceTo(this.get_destination()) < 8) {
					double x = Utils._rand.nextDouble(this._region_mngr.get_width());
					double y = Utils._rand.nextDouble(this._region_mngr.get_height());
					this.set_destination(new Vector2D(x, y));
				}
				// (1.2) Moverse hacia el destino
				this.move(this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
				// (1.3) Envejecer
				this.set_age(this.get_age() + dt);
				// (1.4) Gastar energia
				this.set_energy(this.get_energy() - (20 * dt));
				this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
				// (1.5) Desear mas
				this.set_desire(this.get_desire() + (40.0 * dt));
				this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));

				// 2. Cambio de estado
				// (2.1) Buscar amenaza
				if (this._danger_source == null) {
					Predicate<Animal> carnivores = (a) -> a.get_diet() == Diet.CARNIVORE;
					List<Animal> visibles = this._region_mngr.get_animals_in_range(this, carnivores);
					this._danger_source = this._danger_strategy.select(this, visibles);
					// Buscar la amenaza
				}
				// (2.2) En peligro?
				else {
					this.set_state(State.DANGER);
					this.set_mate_target(null);
				}
				if (this._danger_source == null && this.get_desire() > 65) {
					this.set_state(State.MATE);
					this._danger_source = null;
				}
				break;
			case DANGER:
				// 1. Se ha muerto
				if (this.get_state() == State.DEAD) {
					this._danger_source = null;
				}

				// 2. Mover el animal si no hay peligro
				if (this._danger_source == null) {
					// x.1. Avanzar el animal
					// (x.1.1) Si ha llegado al destino, seleccionar otro aleatorio
					if (this.get_position().distanceTo(this.get_destination()) < 8) {
						double x = Utils._rand.nextDouble(this._region_mngr.get_width());
						double y = Utils._rand.nextDouble(this._region_mngr.get_height());
						this.set_destination(new Vector2D(x, y));
					}
					// (x.1.2) Moverse hacia el destino
					this.move(this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
					// (x.1.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (x.1.4) Gastar energia
					this.set_energy(this.get_energy() - (20 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (x.1.5) Desear mas
					this.set_desire(this.get_desire() + (40.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
				}
				// Mover el animal con peligro
				else {
					// (2.1) Cambiar el destino para huir del peligro
					this.set_destination(this.get_position().plus(this.get_position().minus(_danger_source.get_position()).direction()));
					// (2.2) Moverse hacia el destino
					this.move(2.0 * this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
					// (2.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (2.4) Gastar energia
					this.set_energy(this.get_energy() - (20 * 1.2 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (2.5) Desear mas
					this.set_desire(this.get_desire() + (40.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
				}

				// 3. Cambio de estado
				// (3.1) Comprobar que la amenaza sigue
				if (this._danger_source == null || this.get_position().distanceTo(this._danger_source.get_position()) > this.get_sight_range()) {
					// (3.1.1) Buscar nueva amenaza
					Predicate<Animal> carnivores = (a) -> a.get_diet() == Diet.CARNIVORE;
					// carnivoros
					List<Animal> visibles = this._region_mngr.get_animals_in_range(this, carnivores);
					this._danger_source = this._danger_strategy.select(this, visibles);
					// (3.1.2) No hay amenaza
					if (this._danger_source == null) {
						// (3.1.2.1) Determinar nuevo estado
						if (this.get_desire() < 65) {
							this.set_state(State.NORMAL);
							this._danger_source = null;
							this.set_mate_target(null);
						}
						else {
							this.set_state(State.MATE);
							this._danger_source = null;
						}
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
					Predicate<Animal> sheep = (a) -> a.get_genetic_code().equals("Sheep");
					List<Animal> visibles = this._region_mngr.get_animals_in_range(this, sheep);
					this.set_mate_target(this.get_mate_strategy().select(this, visibles));
				}
				
				if (this.get_mate_target() != null) {
					// (2.1) Cambiar destino a la nueva pareja
					this.set_destination(this.get_mate_target().get_position());
					// (2.2) Moverse hacia el destino
					this.move(2 * this.get_speed() * dt * Math.exp((this.get_energy() - 100.00) * 0.007));
					// (2.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (2.4) Gastar energia
					this.set_energy(this.get_energy() - (20.0 * 1.2 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (2.5) Desear mas
					this.set_desire(this.get_desire() + (40.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
					// (2.6) Si es posible, emparjarse con su pareja
					if (this.get_position().distanceTo(this.get_mate_target().get_position()) < 8.0) {
						// (2.6.1) Resetear deseo
						this.set_desire(0.0);
						this.get_mate_target().set_desire(0.0);
						// (2.6.2) Crear bebe si no esta embarazado el animal y con 90% de probabilidad
						if (!this.is_pregnant()) {
							if (Utils._rand.nextDouble() < 0.9) {
								this.get_pregnant(new Sheep(this, this.get_mate_target()));
							}
					 	}
						// (2.6.3) Cambiar de pareja
						this.set_mate_target(null);
					}
				}
				else {
					// x.1. Avanzar el animal
					// (x.1.1) Si ha llegado al destino, seleccionar otro aleatorio
					if (this.get_position().distanceTo(this.get_destination()) < 8) {
						double x = Utils._rand.nextDouble(this._region_mngr.get_width());
						double y = Utils._rand.nextDouble(this._region_mngr.get_height());
						this.set_destination(new Vector2D(x, y));
					}
					// (x.1.2) Moverse hacia el destino
					this.move(this.get_speed() * dt * Math.exp((this.get_energy() - 100.0) * 0.007));
					// (x.1.3) Envejecer
					this.set_age(this.get_age() + dt);
					// (x.1.4) Gastar energia
					this.set_energy(this.get_energy() - (20 * dt));
					this.set_energy(Utils.constrain_value_in_range(this.get_energy(), 0.0, 100.0));
					// (x.1.5) Desear mas
					this.set_desire(this.get_desire() + (40.0 * dt));
					this.set_desire(Utils.constrain_value_in_range(this.get_desire(), 0.0, 100.0));
				}
				
				// 3. Buscar nueva amenaza
				Predicate<Animal> carnivores = (a) -> a.get_diet() == Diet.CARNIVORE;
				List<Animal> visibles = this._region_mngr.get_animals_in_range(this, carnivores);
				this._danger_source = this._danger_strategy.select(this, visibles);
				
				// 4. Cambiar estado
				if (this._danger_source != null) {
					this.set_state(State.DANGER);
					this.set_mate_target(null);
				}
				if (this._danger_source == null && this.get_desire() < 65.0) {
					this.set_state(State.NORMAL);
					this._danger_source = null;
					this.set_mate_target(null);
				}
				break;
			default:
				break;
		}

		// Ajustar la posicion a que este en el mapa
		double x = this.get_position().getX();
		double y = this.get_position().getY();
		int width = this._region_mngr.get_width();
		int height = this._region_mngr.get_height();
		boolean fuera = false;
		while (x >= width) {
			x = (x - width);
			fuera = true;
		}
		while (x < 0) {
			x = (x + width);
			fuera = true;
		}
		while (y >= height) {
			y = (y - height);
			fuera = true;
		}
		while (y < 0) {
			y = (y + height);
			fuera = true;
		}
		if (fuera) {
			this.set_position(new Vector2D(x, y));
			this.set_state(State.NORMAL);
			this._danger_source = null;
			this.set_mate_target(null);
		}


		// Ver si muere
		if (this.get_energy() <= 0) {
			this.set_state(State.DEAD);
		}
		if (this.get_age() > 8) {
			this.set_state(State.DEAD);
		}


		// Comer
		if (this.get_state() != State.DEAD) {
			this.set_energy(this.get_energy() + this._region_mngr.get_food(this, dt));
		}
		
	}
	
}
