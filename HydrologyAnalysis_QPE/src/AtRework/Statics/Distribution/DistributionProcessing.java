package AtRework.Statics.Distribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import AtRework.Global;
import Hydro.Rainfall.ReturnPeriod.RetrunPeriod;
import asciiFunction.AsciiBasicControl;
import usualTool.AtCommonMath;
import usualTool.AtFileWriter;

public class DistributionProcessing {

	// collect for all asciiFile from Statics.maxRainfall
	private List<AsciiBasicControl> asciiList = new ArrayList<>();

	// output collection
	private Map<String, AsciiBasicControl> outMap = new HashMap<>();

	private Global.rainfallPerDuration durationLimit;

	public DistributionProcessing() throws Exception {

		// STEP 1 run all duration
		for (int duration : Global.rainfallDuration) {

			String durationFolder = Global.staticsFolder + duration;
			String maxRainfallFloder = durationFolder + "\\maxRainfall\\";
			String distributionFolder = durationFolder + "\\distribution\\";
			this.durationLimit = Global.rainfallPerDuration.getLimit(duration);
			System.out.println("*NOTICE* Collecting yearMaxRainfall, duration=" + duration);

			// STEP 2 collect all yearMax .ascFile to List
			// STEP 3 consider all the year between startYear and endYear
			getAsciiList(maxRainfallFloder);

			// STEP 4 calculate for all grids
			AsciiBasicControl temptAscii = asciiList.get(0);
			for (int row = 0; row < temptAscii.getRow(); row++) {
				for (int column = 0; column < temptAscii.getColumn(); column++) {
					if (!temptAscii.isNull(column, row)) {
						System.out.print("*NOTICE* grid, " + row + ", " + "column, ");

						// STEP 5 get all yearMax rainfall in specific grid
						List<Double> gridValue = this.getGridValue(row, column);

						// STEP 6 run all distribution and returnPeriod
						this.getReturnPeriodInEachDis(row, column, gridValue);
					}
				}
			}

			// STEP 7 output to ascFiles
			this.outputToAscii(distributionFolder, duration);
		}
	}

	// STEP 3 consider all the year between startYear and endYear
	private void getAsciiList(String maxRainfallFloder) throws IOException {
		for (int year = Global.startYear; year <= Global.endYear; year++) {
			asciiList.add(new AsciiBasicControl(maxRainfallFloder + year + ".asc"));
		}
		System.out.println("*NOTICE* Collecting each year asciiFile over");
	}

	// STEP 5 get all yearMax rainfall in specific grid
	private List<Double> getGridValue(int row, int column) {
		List<Double> gridValue = new ArrayList<>();
		for (AsciiBasicControl ascii : asciiList) {
			double temptGridValue = Double.parseDouble(ascii.getValue(column, row));

			// check for value in duration rainfall limit
			if (temptGridValue >= durationLimit.getMinValue() && temptGridValue <= durationLimit.getMaxValue())
				gridValue.add(temptGridValue);
		}
		System.out.print("envent size=" + gridValue.size() + ", ");
		return gridValue;
	}

	// STEP 6 run all distribution and returnPeriod
	private void getReturnPeriodInEachDis(int row, int column, List<Double> gridValue) throws Exception {
		for (Global.rainfallDistribute distribution : Global.rainfallDistribute.values()) {
			for (int returnPeriod : Global.rainfallReturnYear) {

				// setting keyName for outMap
				String keyName = distribution.getName() + "_" + returnPeriod;
				AsciiBasicControl outAscii = Optional.ofNullable(outMap.get(keyName)).orElse(asciiList.get(0).clone());

				// check for event size is enough for returnPeriod statics
				if (gridValue.size() < Global.rainfallEventMinSize) {
					outAscii.setValue(column, row, outAscii.getNullValue());
					System.out.print(keyName + "=null, ");
				} else {
					RetrunPeriod returnPeriodDis = distribution.getDistribute(gridValue);
					String returnPeriodValue = AtCommonMath
							.getDecimal_String(returnPeriodDis.getPeriodRainfall(returnPeriod), Global.dataDecimal);
					outAscii.setValue(column, row, returnPeriodValue);
					System.out.print(keyName + "=" + returnPeriodValue + ", ");
				}
				outMap.put(keyName, outAscii);

			}
		}
		System.out.println("complete");
	}

	// STEP 7 output to ascFiles
	private void outputToAscii(String distributionFolder, int duration) {
		outMap.keySet().forEach(keyName -> {
			String saveAdd = distributionFolder + keyName + ".asc";
			try {
				new AtFileWriter(outMap.get(keyName).getAsciiFile(), saveAdd).textWriter(" ");
				System.out.println("*NOTICE* Compltet Rainfall ReturnPeriod, in duration " + duration);

			} catch (IOException e) {
				e.printStackTrace();

				// clear
			} finally {
				this.clear();
			}
		});
	}

	private void clear() {
		this.asciiList.clear();
		this.outMap.clear();
	}

}
