package AtRework;

public class Global {

	public static String rootFolder = "W:\\OneDrive\\工作用\\計劃案\\110 - 北科\\格網水文\\110-架構\\";
	public static String rainfallFolder = rootFolder + "Rainfall\\";
	public static String staticsFolder = rootFolder + "Statics\\";

	public static int dataDecimal = 4;
	public static int startYear = 2006;
	public static int endYear = 2020;
	public static String timeFormat = "yyyy/dd/mm HH";

	public static int[] rainfallDuration = new int[] { 1, 2, 3, 6, 9, 12, 18, 24, 48, 72 };
	public static int[] rainfallReturnYear = new int[] { 2, 5, 10, 50, 100, 150, 200, 500 };

	public static enum RainfallDistribute {
		EV1, LN3, LPT3, PT3
	}

}
