package PreWork.RainfallData.CatchmentTimeTranse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import GlobalProperty.GlobalProperty;

import usualTool.AtFileWriter;

public class Catchment_CollectDailyDataToTotal_Thread extends Thread {
	private String targetCatchment;

	@Override
	public void run() {
		String catchmentDaily = Catchment_CollectDailyDataToTotal.fileAdd + targetCatchment
				+ GlobalProperty.catchment_RainfallFolder_Day;
		String catchmentTotal = Catchment_CollectDailyDataToTotal.fileAdd + targetCatchment
				+ GlobalProperty.catchment_RainfallAnalysis_total;
		List<String> outPutList = new ArrayList<>();
		/*
		 * Daily File
		 */
		for (String dayFile : new File(catchmentDaily).list()) {
			double dailyYear = Double.parseDouble((dayFile.split(".csv")[0]));
			if (dailyYear >= Catchment_CollectDailyDataToTotal.startYear
					&& dailyYear <= Catchment_CollectDailyDataToTotal.endYear) {

				/*
				 * Reader File
				 */
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(catchmentDaily + dayFile));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String temptLine = "";
				try {
					while ((temptLine = br.readLine()) != null) {
						outPutList.add(temptLine);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/*
		 * Output File
		 */
		try {
			new AtFileWriter(outPutList.parallelStream().toArray(String[]::new), catchmentTotal).csvWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Catchment_CollectDailyDataToTotal_Thread(String targetCatchment) {
		this.targetCatchment = targetCatchment;
	}
}
