package CreateEvent.threeHourVersion;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import usualTool.AtCommonMath;
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
			catchmentData = new AtFileReader(GlobalProperty.catchment_RainfallFolder + targetFolder + "\\"
					+ CatchmentCreateRainfall.maximumDelay + "_" + CatchmentCreateRainfall.minmumEventPeriod + "_"
					+ GlobalProperty.catchment_RainfallAnalysis_event).getCsv(1, 0);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
//		for (String[] event : catchmentData) {
//			double temptValue = Double.parseDouble(event[0]);
//			if (temptValue >= CatchmentCreateRainfall.minRainfallValue) {
//				rainfallList_24.add(temptValue);
//			}
//		}

		if (catchmentData.length > 200) {
			for (String[] event : catchmentData) {
				double temptValue = Double.parseDouble(event[0]);
				if (temptValue >= CatchmentCreateRainfall.minRainfallValue) {
					rainfallList_24.add(temptValue);
				}
			}
		} else {
			for (String[] event : catchmentData) {
				rainfallList_24.add(Double.parseDouble(event[0]));
			}
		}

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
							.textWriter("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getCreateRafinalByYear(RetrunPeriod temptPeriod, String periodName) {
		StringBuilder sb = new StringBuilder();
		sb.append(periodName);
		for (Integer temptReturnPeriod : CatchmentCreateRainfall.returnPeriod_year) {
			sb.append("," + temptPeriod.getPeriodRainfall(temptReturnPeriod));
		}

		return sb.toString();
	}
}
