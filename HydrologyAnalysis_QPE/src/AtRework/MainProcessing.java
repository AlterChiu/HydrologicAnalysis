package AtRework;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import AtRework.GridModel.Grid;
import AtRework.Rainfall.Original.Adaptor.RainfallAdaptor;
import AtRework.Rainfall.YearMax.YearMaxProcessing;
import AtRework.Statics.StatisticsInitialize;
import AtRework.Statics.Distribution.DistributionProcessing;
import AtRework.Statics.YearMax.Convert.YearMaxConvert;
import usualTool.AtFileFunction;
import usualTool.AtFileReader;

public class MainProcessing {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// STEP 1 convert original data to specific folder structure
		String sourceFilesFolder = "W:\\OneDrive\\工作用\\計劃案\\110 - 北科\\格網水文\\原始資料\\rainfallData\\";
		new RainfallAdaptor(sourceFilesFolder);

		// STEP 2 statics yearMax for each duration
		new YearMaxProcessing();

		// STEP 3 initialize the "Statistics" folder structure
		new StatisticsInitialize();

		// STEP 4 convert yearMax rainfall from grid folder to maxRainfall .ascFile for
		// each year
		new YearMaxConvert();

		// STEP 5 statics for each distribution in selected years(which between
		// startYear to endYear)
		new DistributionProcessing();

	}

}
