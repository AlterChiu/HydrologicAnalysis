package GlobalProperty;

public class GlobalProperty {

	// <================================================>
	// < ++++++++++++++++++++++Global+++++++++++++++++++++ >
	// <================================================>
	static public String workSpace = "H:\\";

	public static int startYear = 2006;
	public static int endYear = 2017;
	public static int[] returnPeriod_year = new int[] { 2, 5, 10, 50, 100, 150, 200, 500 };
	public static int[] returnPeriod_delayTime =new int[] { 1, 3, 6, 12, 18, 24, 48, 72 };
	public static String[] returnPeriod_distribution = new String[] { "EV1", "LN3", "LPT3", "PT3" };

	// <================================================>
	// <++++++++++++++++++++++PreWork++++++++++++++++++++ >
	// <================================================>
	/*
	 * 1. Original Rainfall Data To Catchment 2. Catchment Collect Daily Data To
	 * Total 3. Catchment Select Event 4. CatchmentCreateRainfall 5.
	 * CatchmentCreateRainfall_Collection 6. CatchmentCreateRainfall_ToAsc
	 */
	static public String rainfallDataFolder = workSpace + "RainfallData\\";
	static public String catchment_RainfallFolder = rainfallDataFolder + "catchment\\";
	static public String catchment_RainfallFolder_Day = "\\day\\";

	static public String designRainfall_CreateFolder = rainfallDataFolder + "\\createRainfall\\";

	static public String catchment_RainfallAnalysis_total = "\\total.csv";
	static public String catchment_RainfallAnalysis_event = "eventProperty.csv";
	static public String catchment_RainfallAnalysis_Distribution = "distribution.csv";

	static public String original_RainfallFloder = rainfallDataFolder + "original\\";

}
