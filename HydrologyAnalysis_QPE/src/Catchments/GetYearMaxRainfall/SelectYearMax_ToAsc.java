package Catchments.GetYearMaxRainfall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import asciiFunction.XYZToAscii;
import usualTool.AtFileReader;

public class SelectYearMax_ToAsc {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int delayArray[] = GlobalProperty.GlobalProperty.returnPeriod_delayTime;

		for (int delay : delayArray) {
			String saveAdd = "H:\\RainfallData\\statics\\年最大降雨量\\" + delay + "\\";
			String[][] distributionConternt = null;
			for (String fileName : new File(saveAdd).list()) {
				if (fileName.contains(".csv")) {
					distributionConternt = new AtFileReader(saveAdd + fileName).getCsv();
					break;
				}
			}

			for (int column = 3; column < distributionConternt[0].length; column++) {
				List<String[]> temptColumn = new ArrayList<>();

				for (int line = 1; line < distributionConternt.length; line++) {
					temptColumn.add(new String[] { distributionConternt[line][1], distributionConternt[line][2],
							distributionConternt[line][column] });
				}

				XYZToAscii toAscii = new XYZToAscii(temptColumn.parallelStream().toArray(String[][]::new));
				toAscii.setCellSize(0.0125).setCoordinateScale(5).setValueScale(3);
				toAscii.start();
				toAscii.saveAscii(saveAdd + distributionConternt[0][column] + ".asc");
				System.out.println(distributionConternt[0][column] + "\tend");
			}

		}

	}
}
