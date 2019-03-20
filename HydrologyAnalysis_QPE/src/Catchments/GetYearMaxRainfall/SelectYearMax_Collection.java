package Catchments.GetYearMaxRainfall;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import GlobalProperty.GlobalProperty;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class SelectYearMax_Collection extends GlobalProperty {
	public static int temptDelayTime = 0;
	private static int eventDelayArray[] = GlobalProperty.returnPeriod_delayTime;

	public static int startYear = GlobalProperty.startYear;
	public static int endYear = GlobalProperty.endYear;

	public static String yearMaxRainfallFile = SelectYearMax.saveName;
	public static String saveName = "";

	/*
	 * select the event from each catchment by given time delay
	 */
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub

		for (int eventDelay : eventDelayArray) {
			temptDelayTime = eventDelay;
			saveName = "\\Rainfall" + String.format("%03d", eventDelay) + "_YearMax.csv";

			List<String> catchmentsList = new ArrayList<>(
					Arrays.asList(SelectYearMax.fileList = new File(SelectYearMax.fileAdd).list()));

			/*
			 * out content Title
			 */
			List<String[]> outContent = new ArrayList<String[]>();
			List<String> outContentTitle = new ArrayList<>();
			outContentTitle.add("catchment");
			outContentTitle.add("X");
			outContentTitle.add("Y");
			for (int year = startYear; year <= endYear; year++) {
				outContentTitle.add(year + "");
			}
			outContent.add(outContentTitle.parallelStream().toArray(String[]::new));

			/*
			 * catchment folder
			 */
			for (String catchment : catchmentsList) {
				String[][] yearMaxContent = new AtFileReader(catchment_RainfallFolder + catchment + saveName).getCsv();

				// catchment data
				List<String> catchmentData = new ArrayList<>();
				catchmentData.add(catchment);
				catchmentData.add(catchment.split("_")[0]);
				catchmentData.add(catchment.split("_")[1]);
				for (int year = 0; year < yearMaxContent.length; year++) {
					catchmentData.add(yearMaxContent[year][1]);
				}
				outContent.add(catchmentData.parallelStream().toArray(String[]::new));
			}

			new AtFileWriter(outContent.parallelStream().toArray(String[][]::new), rainfallDataStaticsFolder + saveName)
					.csvWriter();
		}
	}
}
