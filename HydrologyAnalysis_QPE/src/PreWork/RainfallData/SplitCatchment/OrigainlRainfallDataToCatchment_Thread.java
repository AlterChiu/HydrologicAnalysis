package PreWork.RainfallData.SplitCatchment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import FEWS.PIXml.AtPiXmlReader;
import nl.wldelft.util.timeseries.TimeSeriesArray;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;
import GlobalProperty.GlobalProperty;

public class OrigainlRainfallDataToCatchment_Thread extends Thread {
	private String temptTime = "";

	@Override
	public void run() {
		/*
		 * get the original rainfall data
		 */
		AtPiXmlReader xmlReader = new AtPiXmlReader();
		ArrayList<TimeSeriesArray> rainfallValue = null;
		try {
			rainfallValue = xmlReader.getTimeSeriesArrays(
					OriginalRainfallDataToCatchment.originalRainfallFolder + temptTime + "rainfallData.xml");
		} catch (OperationNotSupportedException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		/*
		 * split rainfall by location
		 */
		for (TimeSeriesArray timeSeries : rainfallValue) {
			List<String[]> valueList = new ArrayList<String[]>();
			String location = timeSeries.getHeader().getLocationId();

			for (int index = 0; index < timeSeries.size(); index++) {
				if (timeSeries.getValue(index) > 0) {
					valueList
							.add(new String[] { TimeTranslate.milliToDate(timeSeries.getTime(index), "yyyyMMddHH") + "",
									timeSeries.getValue(index) + "" });
				}
			}

			/*
			 * output valueList
			 */
			try {
				new AtFileWriter(valueList.parallelStream().toArray(String[][]::new),
						OriginalRainfallDataToCatchment.locationRainfallFolder + location
								+ GlobalProperty.catchment_RainfallFolder_Day + temptTime + ".csv").csvWriter();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public OrigainlRainfallDataToCatchment_Thread(String temptTime) {
		this.temptTime = temptTime;
	}

}
