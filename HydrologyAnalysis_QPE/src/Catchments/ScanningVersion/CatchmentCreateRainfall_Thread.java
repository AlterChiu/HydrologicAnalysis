package Catchments.ScanningVersion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import usualTool.AtFileReader;
import usualTool.AtFileWriter;
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
		List<Double> yearMaxRainfallList = new ArrayList<Double>();
		try {
			catchmentData = new AtFileReader(
					GlobalProperty.catchment_RainfallFolder + targetFolder + CatchmentCreateRainfall.inputFile)
							.getCsv();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		for (String temptLine[] : catchmentData) {
			yearMaxRainfallList.add(Double.parseDouble(temptLine[1]));
		}

		/*
		 * output content
		 */
		List<String> outContent = new ArrayList<>();

		/*
		 * create the distribution
		 */
		RetrunPeriod EV1 = new ReturnPeriod_EV1(yearMaxRainfallList);
		outContent.add(getCreateRafinalByYear(EV1, "EV1"));

		RetrunPeriod LN3 = new ReturnPeriod_LN3(yearMaxRainfallList);
		outContent.add(getCreateRafinalByYear(LN3, "LN3"));

		RetrunPeriod LPT3 = new ReturnPeriod_LPT3(yearMaxRainfallList);
		outContent.add(getCreateRafinalByYear(LPT3, "LPT3"));

		RetrunPeriod PT3 = new ReturnPeriod_PT3(yearMaxRainfallList);
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

}
