package Catchments.threeHourVersion;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;
import GlobalProperty.GlobalProperty;

public class CatchmentSelectYearMaxEvent_Thread extends Thread {
	private String targetFolder = "";

	public CatchmentSelectYearMaxEvent_Thread(String targetFolder) {
		this.targetFolder = targetFolder;
	}

	public void run() {
		/*
		 * output variables
		 */
		Map<Integer, Double> yearMax = initialYearRainfall();

		/*
		 * Read rainfall data by selected event
		 */
		String catchmentData[][] = null;
		try {
			catchmentData = new AtFileReader(
					GlobalProperty.catchment_RainfallFolder + targetFolder + CatchmentSelectYearMaxEvent.rainfallName)
							.getCsv(1, 0);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		/*
		 * select the max rainfall in each year
		 */
		for (String temptLine[] : catchmentData) {
			int temptYear = GlobalProperty.startYear;
			try {
				temptYear = Integer.parseInt(TimeTranslate.StringGetSelected(temptLine[1], "yyyyMMddHH", "yyyy"));
			} catch (NumberFormatException | ParseException e) {
			}

			double temptValue = Double.parseDouble(temptLine[0]);
			if (yearMax.get(temptYear) < temptValue) {
				yearMax.put(temptYear, temptValue);
			}
		}

		/*
		 * output the year max
		 */
		List<String[]> outList = new ArrayList<>();
		for (int year = GlobalProperty.startYear; year <= GlobalProperty.endYear; year++) {
			outList.add(new String[] { year + "",
					new BigDecimal(yearMax.get(year)).setScale(3, BigDecimal.ROUND_HALF_UP).toString() });
		}
		try {
			new AtFileWriter(outList.parallelStream().toArray(String[][]::new),
					GlobalProperty.catchment_RainfallFolder + targetFolder + CatchmentSelectYearMaxEvent.saveName)
							.csvWriter();
		} catch (IOException e) {
		}

	}

	/*
	 * 
	 */
	private Map<Integer, Double> initialYearRainfall() {
		Map<Integer, Double> outMap = new TreeMap<>();
		for (int year = GlobalProperty.startYear; year <= GlobalProperty.endYear; year++) {
			outMap.put(year, 0.);
		}
		return outMap;
	}
}
