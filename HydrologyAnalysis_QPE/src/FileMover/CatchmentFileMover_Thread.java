package FileMover;

import GlobalProperty.GlobalProperty;
import usualTool.AtFileFunction;

public class CatchmentFileMover_Thread extends Thread {
	private String targetCatchment = "";

	public CatchmentFileMover_Thread(String targetCatchment) {
		this.targetCatchment = targetCatchment;
	}

	public void run() {
		String readFolder = GlobalProperty.catchment_RainfallFolder + targetCatchment;
		String saveAdd = "E:\\temptFolder\\" + targetCatchment;

		AtFileFunction.createFolder(saveAdd);
		for (String fileName : getFileList()) {
			AtFileFunction.copyFile(readFolder + "\\" + fileName, saveAdd + "\\" + fileName);
		}
	}

	private String[] getFileList() {
		return new String[] { "12_eventDelay_Distribution.csv", "12_eventDelay_Rainfall.csv",
				"18_eventDelay_Distribution.csv", "18_eventDelay_Rainfall.csv", "1_eventDelay_Distribution.csv",
				"1_eventDelay_Rainfall.csv", "24_eventDelay_Distribution.csv", "24_eventDelay_Rainfall.csv",
				"3_3_100.0_noRatio_distribution.csv", "3_3_100.0_times200_noRatio_distribution.csv",
				"3_3_150.0_times100_noRatio_distribution.csv", "3_3_200.0_noRatio_distribution.csv",
				"3_3_200.0_times100_noRatio_distribution.csv", "3_3_5.0_times100_noRatio_distribution.csv",
				"3_3_50.0_noRatio_distribution.csv", "3_3_eventProperty.csv", "3_3_noRatio_distribution.csv",
				"3_eventDelay_Distribution.csv", "3_eventDelay_Rainfall.csv", "48_eventDelay_Distribution.csv",
				"48_eventDelay_Rainfall.csv", "6_eventDelay_Distribution.csv", "6_eventDelay_Rainfall.csv",
				"72_eventDelay_Distribution.csv", "72_eventDelay_Rainfall.csv", "total.csv" };
	}

}
