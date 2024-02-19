package simulator.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private Map<String, Builder<T>> _builders;
	private List<JSONObject> _builders_info;
	
	public BuilderBasedFactory() {
		this._builders = new HashMap<String, Builder<T>>();
		this._builders_info = new LinkedList<JSONObject>();
	}
	
	public BuilderBasedFactory(List<Builder<T>> builders) {
		this();
		
		for (Builder<T> b : builders) {
			this.add_builder(b);
		}
	}
	
	public void add_builder(Builder<T> b) {
		this._builders.put(b.get_type_tag(), b);
		this._builders_info.add(b.get_info());
	}
	
	@Override
	public T create_instance(JSONObject info) {
		if (info == null) {
			throw new IllegalArgumentException("’info’ cannot be null");
		}
		String type = info.getString("type");
		Builder<T> builder = this._builders.get(type);
		
		T instance = builder.create_instance(info.has("data") ? info.getJSONObject("data") : new JSONObject());
		if (instance == null) {
			throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
		}
		return (instance);
		// Look for a builder with a tag equals to info.getString("type"), in the
		// map _builder, and call its create_instance method and return the result
		// if it is not null. The value you pass to create_instance is the following
		// because ‘data’ is optional:
		//
		// TODO Find out why this is "getJSONObject" and not "JSONObject"
		// info.has("data") ? info.getJSONObject("data") : new getJSONObject()
		// …
		// If no builder is found or the result is null ...
		
	}
	
	@Override
	public List<JSONObject> get_info() {
		return (Collections.unmodifiableList(this._builders_info));
	}
}
