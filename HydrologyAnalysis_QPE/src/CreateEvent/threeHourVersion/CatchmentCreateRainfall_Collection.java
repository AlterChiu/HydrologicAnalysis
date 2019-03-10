package CreateEvent.threeHourVersion;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import CreateEvent.threeHourVersion.CatchmentCreateRainfall;
import GlobalProperty.GlobalProperty;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class CatchmentCreateRainfall_Collection extends GlobalProperty {
	/*
	 * select the event from each catchment by given time delay
	 */
	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		List<String> catchmentsList = new ArrayList<>(
				Arrays.asList(CatchmentCreateRainfall.fileList = new File(CatchmentCreateRainfall.fileAdd).list()));

		/*
		 * out content
		 */
		List<String[]> outContent = new ArrayList<String[]>();
		List<String> outContentTitle = new ArrayList<>();
		outContentTitle.add("catchment");
		outContentTitle.add("X");
		outContentTitle.add("Y");
		for (String distribution : CatchmentCreateRainfall.returnPeriod_distribution) {
			for (Integer year : CatchmentCreateRainfall.returnPeriod_year) {
				outContentTitle.add(distribution + "_" + year);
			}
		}
		outContent.add(outContentTitle.parallelStream().toArray(String[]::new));

		/*
		 * catchment folder
		 */
		for (String catchment : catchmentsList) {
			String[][] returnPeriodContent = new AtFileReader(
					catchment_RainfallFolder + catchment + CatchmentCreateRainfall.saveName).getCsv();

			// catchment data
			List<String> catchmentData = new ArrayList<>();
			catchmentData.add(catchment);
			catchmentData.add(catchment.split("_")[0]);
			catchmentData.add(catchment.split("_")[1]);
			for (int year = 1; year < returnPeriodContent.length; year++) {
				for (int distribution = 1; distribution < returnPeriodContent[year].length; distribution++) {
					catchmentData.add(returnPeriodContent[year][distribution]);
				}
			}
			outContent.add(catchmentData.parallelStream().toArray(String[]::new));
		}

		new AtFileWriter(outContent.parallelStream().toArray(String[][]::new),
				designRainfall_CreateFolder + CatchmentCreateRainfall.saveName).csvWriter();
	}
}
