package Catchments.threeHourVersion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import asciiFunction.XYZToAscii;
import usualTool.AtFileReader;

public class CatchmentCreateRainfall_ToAsc {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String[][] distributionConternt = new AtFileReader(
				"H:\\RainfallData\\createRainfall\\篩選事件100\\3_3_150.0_times100_noRatio_distribution\\3_3_150.0_times100_noRatio_distribution.csv").getCsv();
		String saveAdd = "H:\\RainfallData\\createRainfall\\篩選事件100\\3_3_150.0_times100_noRatio_distribution\\";

		for (int column = 3; column < distributionConternt[0].length; column++) {
			List<String[]> temptLine = new ArrayList<>();

			for (int line = 1; line < distributionConternt.length; line++) {
				temptLine.add(new String[] { distributionConternt[line][1], distributionConternt[line][2],
						distributionConternt[line][column] });
			}

			XYZToAscii toAscii = new XYZToAscii(temptLine.parallelStream().toArray(String[][]::new));
			toAscii.setCellSize(0.0125);
			toAscii.start();
			toAscii.saveAscii(saveAdd + distributionConternt[0][column] + ".asc");
			System.out.println(distributionConternt[0][column] + "\tend");
		}

	}

}
