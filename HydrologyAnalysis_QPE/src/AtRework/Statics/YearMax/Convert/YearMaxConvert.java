package AtRework.Statics.YearMax.Convert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import AtRework.Global;
import AtRework.GridModel.Grid;
import asciiFunction.XYZToAscii;

public class YearMaxConvert {

	public YearMaxConvert() throws Exception {

		// STEP 1 run all years and duration
		for (int duration : Global.rainfallDuration) {
			for (int year = Global.startYear; year <= Global.endYear; year++) {
				System.out.println("Convert to Ascii, duration=" + duration + ", year=" + year);

				// STEP 2 create xyzList of specific year and duration
				List<Double[]> outXYZ = new ArrayList<>();

				// STEP 3 find value from each grid folder
				for (String gridName : new File(Global.rainfallFolder).list()) {
					Grid grid = new Grid(gridName);
					Map<Integer, Map<Integer, String>> yearMaxRainfall = grid.getYearMaxRainfall();

					String maxRainfall = Optional.ofNullable(yearMaxRainfall.get(year).get(duration)).orElseThrow(
							() -> new Exception("*ERROR* there is no yearMaxRainfall in gird " + gridName));

					outXYZ.add(new Double[] { grid.getX(), grid.getY(), Double.parseDouble(maxRainfall) });
				}

				// STEP 4 convert list data to ascii
				String saveAdd = Global.staticsFolder + duration + "\\maxRainfall\\" + year + ".asc";
				this.toAscii(outXYZ, saveAdd);
			}
		}

	}

	private void toAscii(List<Double[]> datas, String saveAdd) throws IOException {
		XYZToAscii toAscii = new XYZToAscii(datas);

		toAscii.setNullValue("-99");
		toAscii.setValueScale(Global.dataDecimal);
		toAscii.setCellSize(Global.qpeGridSize);
		toAscii.start();

		toAscii.saveAscii(saveAdd);
	}

}
