package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Builder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.WolfBuilder;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.DefaultRegion;
import simulator.model.DynamicSupplyRegion;
import simulator.model.MapInfo;
import simulator.model.Region;
import simulator.model.SelectClosest;
import simulator.model.SelectFirst;
import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;
import simulator.model.Simulator;
import simulator.model.Wolf;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;
import simulator.control.Controller;

public class Main {

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_dt = 0.003; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _time = null;
	private static Double _dt = null; 
	private static String _in_file = null;
	private static String _out_file = null;
	private static boolean _sv = false;
	private static ExecMode _mode = ExecMode.BATCH;

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parse_dt_option(line);
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_out_file_option(line);
			parse_simple_viewer_option(line);
			parse_time_option(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();

		// delta time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg().desc("A double representing actual time, in seconds, per simulation step. Default value: 0.03.").build());
		
		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());

		// simple viewer
		cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());
		
		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());

		return cmdLineOptions;
	}
	
	private static void parse_dt_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("dt", _default_dt.toString());
		try {
			_dt = Double.parseDouble(t);
			assert (_dt >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for dt: " + t);
		}
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}
	
	private static void parse_out_file_option(CommandLine line) throws ParseException {
		if (line.hasOption("o")) {
			_out_file = line.getOptionValue("o");
			if (_out_file == null) {
				throw new ParseException("In batch mode an input configuration file is required");
			}
		}
	}
	
	private static void parse_simple_viewer_option(CommandLine line) throws ParseException {
		if (line.hasOption("sv")) {
			_sv = true;
		}
	}

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}
	private static Factory<Animal> animal_factory;
	private static Factory<Region> region_factory;

	private static void init_factories() {
		List<Builder<SelectionStrategy>> selection_strategy_builders = new ArrayList<>();
		selection_strategy_builders.add(new SelectFirstBuilder());
		selection_strategy_builders.add(new SelectClosestBuilder());
		selection_strategy_builders.add(new SelectYoungestBuilder());
		BuilderBasedFactory<SelectionStrategy> selection_strategy_factory = new BuilderBasedFactory<SelectionStrategy>(selection_strategy_builders);
		
		List<Builder<Animal>> animal_builders = new ArrayList<>();
		animal_builders.add(new SheepBuilder(selection_strategy_factory));
		animal_builders.add(new WolfBuilder(selection_strategy_factory));
		animal_factory = new BuilderBasedFactory<Animal>(animal_builders);
		
		List<Builder<Region>> region_builders = new ArrayList<>();
		region_builders.add(new DefaultRegionBuilder());
		region_builders.add(new DynamicSupplyRegionBuilder());
		region_factory = new BuilderBasedFactory<Region>(region_builders);
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}


	private static void start_batch_mode() throws Exception {
		InputStream is = new FileInputStream(new File(_in_file));
		
		// (1) Load file and convert to JSONObject
		JSONObject json = load_JSON_file(is);
	
		// (2) Create output file
		OutputStream os;
		if (_out_file != null) {
			os = new FileOutputStream(new File(_out_file));
		}
		else {
			os = new FileOutputStream(new File("output.txt"));
		}
		
		// (3) Create Simulator instance
		int width = json.getInt("width");
		int height = json.getInt("height");
		int rows = json.getInt("rows");
		int cols = json.getInt("cols");
		Simulator simulator = new Simulator(cols, rows, width, height, animal_factory, region_factory);
		
		// (4) Create Controller instance
		Controller controller = new Controller(simulator);
		
		// (5) Call `load_data` with the input JSONObject
		controller.load_data(json);
		
		// (6) Call `run`
		controller.run(_time, _dt, _sv, os);
		
		// (7) Close output file
		os.close();
	}

	private static void start_GUI_mode() throws Exception {
		
	}

	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
