package simulator.model;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements AnimalInfo {
	private String genetic_code;
	private Diet _diet;
	private State _state;
	private Vector2D _pos;
	private Vector2D _dest;
	private double _energy;
	private double _speed;
	private double _age;
	private double _desire;
	private double _sight_range;
	private Animal _mate_target;
	private Animal _baby;
	protected AnimalMapView _region_mngr;
	private SelectionStrategy _mate_strategy;
	
	protected Animal(String genetic_code, Diet diet, double sight_range,
			double init_speed, SelectionStrategy mate_strategy, Vector2D pos) {
		
		if (genetic_code.isEmpty()) {
			throw new IllegalArgumentException("genetic code cannot be empty!");
		}
		if (sight_range < 0) {
			throw new IllegalArgumentException("sight range cannot be negative!");
		}
		if (init_speed < 0) {
			throw new IllegalArgumentException("inital speed cannot be negative!");
		}
		if (mate_strategy == null) {
			throw new IllegalArgumentException("mating strategy cannot be null!");
		}
		
		this.genetic_code = genetic_code;
		this._diet = diet;
		this._sight_range = sight_range;
		this._speed = init_speed;
		this._mate_strategy = mate_strategy;
		this._pos = pos;
		this._age = 0.0;
		this._speed = Utils.get_randomized_parameter(init_speed, 0.1);
		this._state = State.NORMAL;
		this._energy = 100.0;
		this._desire = 0.0;
		this._dest = null;
		this._baby = null;
		this._region_mngr = null;
	}

	protected Animal(Animal p1, Animal p2) {
		this._dest = null;
		this._baby = null;
		this._age = 0.0;
		this._mate_target = null;
		this._region_mngr = null;
		this._state = State.NORMAL;
		this._desire = 0.0;
		this.genetic_code = p1.genetic_code;
		this._diet = p1._diet;
		this._energy = (p1._energy + p2._energy) / 2;
		this._pos = p1.get_position().plus(Vector2D.get_random_vector(-1, 1).scale(60.0 * (Utils._rand.nextGaussian() + 1)));
		this._sight_range = Utils._rand.nextDouble((p1.get_sight_range() + p2.get_sight_range()) / 2);
		this._speed = Utils._rand.nextDouble((p1.get_speed() + p2.get_speed()) / 2);
	
		// TODO: Figure this out
		this._mate_strategy = p1.get_mate_strategy();
	}

	void init(AnimalMapView reg_mngr) {
		this._region_mngr = reg_mngr;
		if (this._pos == null) {
			double x = Utils._rand.nextDouble(this._region_mngr.get_width());
			double y = Utils._rand.nextDouble(this._region_mngr.get_height());
			this._pos = new Vector2D(x, y);
		}
		else {
			double x = Utils.constrain_value_in_range(this._pos.getX(), 0, this._region_mngr.get_width() - 1);
			double y = Utils.constrain_value_in_range(this._pos.getY(), 0, this._region_mngr.get_height() - 1);
			this._pos = new Vector2D(x, y);
		}
		double x = Utils._rand.nextDouble(this._region_mngr.get_width());
		double y = Utils._rand.nextDouble(this._region_mngr.get_height());
		this._dest = new Vector2D(x, y);
	}

	Animal deliver_baby() {
		Animal baby = this._baby;
		this._baby = null;
		return (baby);
	}

	protected void move(double speed) {
		this._pos = this._pos.plus(this._dest.minus(this._pos).direction().scale(speed));
	}
	
	public abstract void update(double dt);

	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();

		json.put("pos", this._pos.asJSONArray());
		json.put("gcode", this.genetic_code);
		json.put("diet", this._diet.toString());
		json.put("state", this._state.toString());

		return (json);
	}
	
	public State get_state() {
		return (this._state);
	}
	public void set_state(State state) {
		this._state = state;
	}
	public Vector2D get_position() {
		return (this._pos);
	}
	public void set_position(Vector2D pos) {
		this._pos = pos;
	}
	public String get_genetic_code() {
		return (this.genetic_code);
	}
	public Diet get_diet() {
		return (this._diet);
	}
	public double get_speed() {
		return (this._speed);
	}
	public double get_desire() {
		return (this._desire);
	}
	public void set_desire(double desire) {
		this._desire = desire;
	}
	public double get_sight_range() {
		return (this._sight_range);
	}
	public double get_energy() {
		return (this._energy);
	}
	public void set_energy(double energy) {
		this._energy = energy;
	}
	public double get_age() {
		return (this._age);
	}
	public void set_age(double age) {
		this._age = age;
	}
	public Vector2D get_destination() {
		return (this._dest);
	}
	public void set_destination(Vector2D dest) {
		this._dest = dest;
	}
	public Animal get_mate_target() {
		return (this._mate_target);
	}
	public void set_mate_target(Animal mate_target) {
		this._mate_target = mate_target;
	}
	public SelectionStrategy get_mate_strategy() {
		return (this._mate_strategy);
	}
	public Animal get_baby() {
		return (this._baby);
	}
	public void get_pregnant(Animal baby) {
		this._baby = baby;
	}
	public boolean is_pregnant() {
		return (this._baby != null);
	}
}
