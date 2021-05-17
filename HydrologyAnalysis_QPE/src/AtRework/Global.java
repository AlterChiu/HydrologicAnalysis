package AtRework;

import java.util.List;
import java.util.Map;

import Hydro.Rainfall.ReturnPeriod.RetrunPeriod;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_EV1;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_LN3;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_LPT3;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_PT3;

public class Global {

	public static String rootFolder = "W:\\OneDrive\\工作用\\計劃案\\110 - 北科\\格網水文\\110-架構\\";
	public static String rainfallFolder = rootFolder + "Rainfall\\";
	public static String staticsFolder = rootFolder + "Statics\\";

	public static int dataDecimal = 4;
	public static double qpeGridSize = 0.0125;
	public static int startYear = 2006;
	public static int endYear = 2020;
	public static String dateFormat = "yyyy/dd/mm";
	public static String timeFormat = dateFormat + " HH";

	public static int[] rainfallDuration = new int[] { 1, 2, 3, 6, 9, 12, 18, 24, 48, 72 };
	public static int[] rainfallReturnYear = new int[] { 2, 5, 10, 50, 100, 150, 200, 500 };

	public static enum rainfallDistribute {
		EV1("EV"), LN3("LN"), LPT3("LPT3"), PT3("PT3");

		private final String name;

		rainfallDistribute(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public RetrunPeriod getDistribute(List<Double> rainfallDatas) throws Exception {

			switch (this.name) {
			case "EV":
				return new ReturnPeriod_EV1(rainfallDatas);

			case "LN":
				return new ReturnPeriod_LN3(rainfallDatas);

			case "LPT3":
				return new ReturnPeriod_LPT3(rainfallDatas);

			case "PT3":
				return new ReturnPeriod_PT3(rainfallDatas);

			default:
				throw new Exception("*ERROR* not allowable distribution, " + this.name);
			}
		}

	}

	// Variable setting
	// ============================================================

	public static int rainfallEventMinSize = 5;
	public static double minRainfallPerHour = 0.0;
	public static double maxRainfallPerHour = 200;

	public static enum rainfallPerDuration {

		Duration1("0", "200"), Duration2("0", "400"), Duration3("0", "600"), Duration6("0", "1200"),
		Duration9("0", "1800"), Duration12("0", "2400"), Duration18("0", "3600"), Duration24("0", "4800"),
		Duration48("0", "9600"), Duration72("0", "14400");

		rainfallPerDuration(String minValue, String maxValue) {
			this.maxValue = Double.parseDouble(maxValue);
			this.minVale = Double.parseDouble(minValue);
		}

		private final double maxValue;
		private final double minVale;

		public double getMaxValue() {
			return this.maxValue;
		}

		public double getMinValue() {
			return this.minVale;
		}

		public static rainfallPerDuration getLimit(int duration) throws Exception {

			switch (duration) {
			case 1:
				return Duration1;
			case 2:
				return Duration2;
			case 3:
				return Duration3;
			case 6:
				return Duration6;
			case 9:
				return Duration9;
			case 12:
				return Duration12;
			case 18:
				return Duration18;
			case 24:
				return Duration24;
			case 48:
				return Duration48;
			case 72:
				return Duration72;
			default:
				throw new Exception("*ERROR* not allowable Duration, " + duration);
			}

		}
	}
}
