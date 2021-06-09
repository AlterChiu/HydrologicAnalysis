package AtRework.Statics;

import AtRework.Global;
import usualTool.AtFileFunction;

public class StatisticsInitialize {

	public StatisticsInitialize() {

		// create for folder for each return period
		for (int duration : Global.rainfallDuration) {
			AtFileFunction.createFolder(Global.staticsFolder + String.valueOf(duration));

			String durationFolder = Global.staticsFolder + String.valueOf(duration);
			AtFileFunction.createFolder(durationFolder + "\\maxRainfall");
			AtFileFunction.createFolder(durationFolder + "\\distribution");
		}

	}

}
