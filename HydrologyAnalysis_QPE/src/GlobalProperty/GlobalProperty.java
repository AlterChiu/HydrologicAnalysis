package GlobalProperty;

public class GlobalProperty {

	// <================================================>
	// < ++++++++++++++++++++++Global+++++++++++++++++++++ >
	// <================================================>
	static public String workSpace = "H:\\";

	public static int startYear = 2006;
	public static int endYear = 2017;
	public static int[] returnPeriod_year = new int[] { 2, 5, 10, 50, 100, 150, 200, 500 };
	public static int[] returnPeriod_delayTime = new int[] { 1, 3, 6, 12, 18, 24, 48, 72 };
	public static String[] returnPeriod_distribution = new String[] { "EV1", "LN3", "LPT3", "PT3" };

	// <================================================>
	// <++++++++++++++++++++++PreWork Event++++++++++++++++ >
	// <================================================>
	/*
	 * 1. Original Rainfall Data To Catchment
	 * 
	 * <to seperate the rainfall data from piXml to each catchment>
	 * 
	 * @
	 * 
	 * @input : PiXml Rainfall file (.xml)
	 * 
	 * @output : csv Rainfall file which skip the 0 and null value (.csv)
	 * 
	 * @
	 * 
	 * 2. Catchment Collect Daily Data To Total
	 * 
	 * <PreWork.1 make a daily data for each catchment , this process make the daily
	 * data to whole time line>
	 * 
	 * @
	 * 
	 * @input : catchment daily rainfall file (.csv)
	 * 
	 * @output : catchment total rainfall file (2006-2017) (.csv)
	 * 
	 * @
	 * 
	 */
	// <================================================>
	// <++++++++++++++++++++++Catchments +++++++++++++++++ >
	// <================================================>
	/*
	 * 
	 * 1. Catchment Select Event
	 * 
	 * <select the event from the raw data in each catchment,there are two method to
	 * select the event. Scanning method and Scanning method , they all in
	 * "CreateEvent" package>
	 * 
	 * @
	 * 
	 * @ input : catchment total rainfall file (.csv)
	 * 
	 * @ output : catchment event file which selected by event delay (.csv)
	 * (1,3,6,12,18,24,48,72 hours)
	 * 
	 * @
	 * 
	 * 2. CatchmentCreateRainfall
	 * 
	 * <create rainfall in four distributions by the selected event (in different
	 * method)>
	 * 
	 * @
	 * 
	 * @ input : catchment event file (.csv)
	 * 
	 * @ output : catchment design rainfall in different return period (.csv)
	 * 
	 * @
	 * 
	 * @ 3. CatchmentCreateRainfall_Collection
	 * 
	 * <all the process before, result data is saved at the CatchmentFolder , this
	 * process collect the result from catchments folder and save it in the
	 * "CreateRainfall Folder">
	 * 
	 * @
	 * 
	 * @ input : "FOLDER" of catchment design rainfall in different return period
	 * (.csv)
	 * 
	 * @ output : coordinate with (distribution*returnPeriod) for each
	 * catchment(.csv)
	 * 
	 * @
	 * 
	 * 4. CatchmentCreateRainfall_ToAsc<MUST MOVE FILE MANUALLY>
	 * 
	 * <the result of process PreWork.5 is the .xyz data , this process will make it
	 * to .asc>
	 * 
	 * @
	 * 
	 * @ input : coordinate with (distribution*returnPeriod) (.csv)
	 * 
	 * @ output : ASCII file for design rainfall in difference returnPeriod and
	 * distribution(.asc)
	 * 
	 * @
	 */

	// <================================================>
	// <++++++++++++ Comparision.ReturnPeriodRainfall +++++++++++++>
	// <================================================>

	/*
	 * @ After Create.Rainfall
	 * 
	 * <get the returnPeriodRainfall different between database and QPE result>
	 * 
	 * <QPE result is for grid solution>
	 * 
	 * <database result is for polygon solution>
	 * 
	 * @
	 * 
	 * @ input :
	 * 
	 * @1.ASCII file for design rainfall in difference returnPeriod and
	 * distribution(.asc)
	 * 
	 * @2. polygon file for the database catchment
	 * 
	 * @ output : Shape file for the hydrological analysis which contain the (ascii
	 * mean value , database value , error value)(.shp)
	 * 
	 * @
	 * 
	 */

	// <================================================>
	// <++++++++++++++++++ Catchment.Year Max ++++++++++++++++>
	// <================================================>

	/*
	 * @After Catchment.SelectEvent
	 * 
	 * @ 1. SelectYearMax
	 * 
	 * <this process will output the YearMax Rainfall from each catchment by
	 * Comparision.YearMaxRainfall >
	 * 
	 * @
	 * 
	 * @ input : catchment event file (.csv)
	 * 
	 * @ output : the yearMaxRainfall for each catchment (2006-2017)(.csv)
	 * 
	 * @
	 * 
	 * @2. SelectYearMax_Collection
	 * 
	 * <collect the YearMax from each catchment>
	 * 
	 * @
	 * 
	 * @ input : "FOLDER" of catchment YearMaxRainfall (.csv)
	 * 
	 * @ output : coordinate with YearMaxRainfall for each catchment(.csv)
	 * 
	 * @
	 * 
	 * @3. SelectYearMax_ToAsc<MUST MOVE FILE MANUALLY>
	 * 
	 * <Translate the .xyy result from Statics YearMax.3 to .asc>
	 * 
	 * @
	 * 
	 * @ input : coordinate with YearMaxRainfall for each catchments(.csv)
	 * 
	 * @ output : ASCII file for YearMaxRainfall(.asc)
	 */

	// <================================================>
	// <++++++++++++ Comparision.YearMaxRainfall ++++++++++++++++>
	// <================================================>

	/*
	 * @ After Statics.YearMax
	 * 
	 * @ 1. get the different between database and QPE result
	 * 
	 * @ 2. QPE result is for grid solution
	 * 
	 * @ 3.database result is for polygon solution
	 */

	// <================================================>
	// <++++++++++++Comparision.YearMax.Statics++++++++++++++++ >
	// <================================================>

	/*
	 * @ After Catchment.Year Max
	 * 
	 * @ 1. statics for the result that come from Comparision.YearMax.Statics
	 */

	static public String rainfallDataFolder = workSpace + "RainfallData\\";
	static public String catchment_RainfallFolder = rainfallDataFolder + "catchment\\";
	static public String catchment_RainfallFolder_Day = "\\day\\";

	static public String designRainfall_CreateFolder = rainfallDataFolder + "\\createRainfall\\";
	static public String rainfallDataStaticsFolder = rainfallDataFolder + "\\statics\\";

	static public String catchment_RainfallAnalysis_total = "\\total.csv";
	static public String catchment_RainfallAnalysis_event = "eventProperty.csv";
	static public String catchment_RainfallAnalysis_Distribution = "distribution.csv";

	static public String original_RainfallFloder = rainfallDataFolder + "original\\";

}
