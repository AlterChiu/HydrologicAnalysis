package AtRework;

import AtRework.Rainfall.YearMax.YearMaxProcessing;
import AtRework.Statics.StaticsInitialize;
import AtRework.Statics.Distribution.DistributionProcessing;
import AtRework.Statics.YearMax.Convert.YearMaxConvert;

public class MainProcessing {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// STEP 1 convert original data to specific folder structure

		// STEP 2 statics yearMax for each duration
		new YearMaxProcessing();

		// STEP 3 initialize the "Statics" folder structure
		new StaticsInitialize();

		// STEP 4 convert yearMax rainfall from grid folder to maxRainfall .ascFile for
		// each year
		new YearMaxConvert();
		
		// STEP 5 statics for each distribution in selected years(which between startYear to endYear)
		new DistributionProcessing();
	}

}
