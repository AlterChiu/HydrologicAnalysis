package AtRework.Rainfall.YearMax;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import AtRework.Global;
import AtRework.Global.rainfallPerDuration;
import AtRework.GridModel.Grid;
import usualTool.AtCommonMath;
import usualTool.AtCommonMath.StaticsModel;
import usualTool.TimeTranslate;

public class YearMaxProcessing {

	public YearMaxProcessing() throws Exception {
		// STEP 1 read each folder in rainfallData

		String rainfallRootFolder = Global.rainfallFolder;
		for (String gridName : new File(rainfallRootFolder).list()) {
			Grid grid = new Grid(gridName);

			// STEP 2 get originalData from grid folder
			Map<String, String> originalData = grid.getOriginalRainfall();

			// STEP 3 statics for each year and rainfall duration
			for (int year = Global.startYear; year <= Global.endYear; year++) {
				System.out.println(gridName + "\t" + year);

				for (int duration : Global.rainfallDuration) {
					System.out.println(duration);

					String yearMax = this.getYearMax(originalData, year, duration);
					grid.addYearMax(year, duration, yearMax);
				}
			}

			// STEP 4 make new yearMax value update to each grid folder
			grid.updateYearMax();
		}

	}

	public String getYearMax(Map<String, String> originalData, int year, int duration) throws Exception {
		String startTime = year + "\01\01 00";
		String endTime = year + "\12\31 23";
		boolean isEnd = false;

		double temptMax = Double.MIN_VALUE;
		while (!isEnd) {

			List<Double> sum = new ArrayList<>();
			for (int index = 0; index < duration; index++) {
				String temptDate = TimeTranslate.addHour(startTime, Global.timeFormat, index);
				if (temptDate.equals(endTime)) {
					isEnd = true;
				}

				double temptValue = Double.parseDouble(Optional.ofNullable(originalData.get(temptDate)).orElse("0"));
				if (temptValue < Global.minRainfallPerHour || temptValue > Global.maxRainfallPerHour) {
					sum.add(0.0);
				} else {
					sum.add(temptValue);
				}
			}

			// get max value
			double temptSum = AtCommonMath.getListStatistic(sum, StaticsModel.getSum);
			if (temptSum > temptMax)
				temptMax = temptSum;
		}

		return AtCommonMath.getDecimal_String(temptMax, Global.dataDecimal);

	}

}
