package CreateEvent.ScanningVersion;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;
import GlobalProperty.GlobalProperty;
import Hydro.Rainfall.ReturnPeriod.RetrunPeriod;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_EV1;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_LN3;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_LPT3;
import Hydro.Rainfall.ReturnPeriod.ReturnPeriod_PT3;

public class CatchmentCreateRainfall_Thread extends Thread {
	private String targetFolder = "";

	public CatchmentCreateRainfall_Thread(String targetFolder) {
		this.targetFolder = targetFolder;
	}

	public void run() {
		/*
		 * Read rainfall data by selected event
		 */
		String catchmentData[][] = null;
		List<Double> rainfallList_24 = new ArrayList<Double>();
		try {
			catchmentData = new AtFileReader(
					GlobalProperty.catchment_RainfallFolder + targetFolder + CatchmentCreateRainfall.rainfallName)
							.getCsv(1, 0);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		/*
		 * initial the max rainfall Map for each year and get the year maximum
		 */
		Map<Integer, Double> maxRainfallMap = initialYearRainfall();
		for (String[] temptLine : catchmentData) {

			/*
			 * get the year max
			 */
//			try {
//				int temptYear = Integer.parseInt(TimeTranslate.StringGetSelected(temptLine[1], "yyyyMMddHH", "yyyy"));
//				double temptRainfall = maxRainfallMap.get(temptYear);
//
//				if (temptRainfall < Double.parseDouble(temptLine[0])) {
//					maxRainfallMap.put(temptYear, Double.parseDouble(temptLine[0]));
//				}
//
//			} catch (NumberFormatException | ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			/*
			 * get the all events
			 */
			rainfallList_24.add(Double.parseDouble(temptLine[0]));
		}

		/*
		 * get the year max
		 */
		// translate the year maximum map to the rainfall array
//		for (Integer key : maxRainfallMap.keySet()) {
//			rainfallList_24.add(maxRainfallMap.get(key));
//		}

		/*
		 * output content
		 */
		List<String> outContent = new ArrayList<>();

		/*
		 * create the distribution
		 */
		RetrunPeriod EV1 = new ReturnPeriod_EV1(rainfallList_24);
		outContent.add(getCreateRafinalByYear(EV1, "EV1"));

		RetrunPeriod LN3 = new ReturnPeriod_LN3(rainfallList_24);
		outContent.add(getCreateRafinalByYear(LN3, "LN3"));

		RetrunPeriod LPT3 = new ReturnPeriod_LPT3(rainfallList_24);
		outContent.add(getCreateRafinalByYear(LPT3, "LPT3"));

		RetrunPeriod PT3 = new ReturnPeriod_PT3(rainfallList_24);
		outContent.add(getCreateRafinalByYear(PT3, "PT3"));

		/*
		 * Output result
		 */
		StringBuilder outContentTitle = new StringBuilder();
		outContentTitle.append("distribution");
		for (Integer temptReturnPeriod : CatchmentCreateRainfall.returnPeriod_year) {
			outContentTitle.append("," + temptReturnPeriod);
		}
		outContent.add(0, outContentTitle.toString());
		try {
			new AtFileWriter(outContent.parallelStream().toArray(String[]::new),
					GlobalProperty.catchment_RainfallFolder + targetFolder + CatchmentCreateRainfall.saveName)
							.csvWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * build the rainfall data of each distribution
	 */
	private String getCreateRafinalByYear(RetrunPeriod temptPeriod, String periodName) {
		StringBuilder sb = new StringBuilder();
		sb.append(periodName);
		for (Integer temptReturnPeriod : CatchmentCreateRainfall.returnPeriod_year) {
			sb.append("," + temptPeriod.getPeriodRainfall(temptReturnPeriod));
		}

		return sb.toString();
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
