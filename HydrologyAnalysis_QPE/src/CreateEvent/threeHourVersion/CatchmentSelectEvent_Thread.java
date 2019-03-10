package CreateEvent.threeHourVersion;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import usualTool.AtCommonMath;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;
import usualTool.TimeTranslate;
import GlobalProperty.GlobalProperty;

public class CatchmentSelectEvent_Thread extends Thread {
	private String targetFolder = "";

	public CatchmentSelectEvent_Thread(String targetFolder) {
		this.targetFolder = targetFolder;
	}

	public void run() {
		/*
		 * Read rainfall data by selected date
		 */
		String catchmentData[][] = null;
		try {
			catchmentData = new AtFileReader(GlobalProperty.catchment_RainfallFolder + targetFolder
					+ GlobalProperty.catchment_RainfallAnalysis_total).getCsv();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		/*
		 * output content
		 */
		List<String[]> eventList = new ArrayList<>();

		/*
		 * Read Content
		 */
		// for the time that event start
		String eventStart = catchmentData[0][0];
		List<Double> event = new ArrayList<>();

		if (Double.parseDouble(eventStart) > CatchmentSelectEvent.startYear
				&& Double.parseDouble(eventStart) < CatchmentSelectEvent.endYear) {
			for (int line = 0; line < catchmentData.length - 1; line++) {
				String rightTime = catchmentData[line][0];
				String nextTime = catchmentData[line + 1][0];

				/*
				 * delatTime is the time delay between right time and next time
				 */
				long delayHour = 0;
				try {
					delayHour = new BigDecimal((TimeTranslate.StringToLong(nextTime, "yyyyMMddHH")
							- TimeTranslate.StringToLong(rightTime, "yyyyMMddHH")) / 3600000)
									.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				} catch (ParseException e) {
					e.printStackTrace();
				}

				/*
				 * add this data to list, then check for the next one
				 */
				event.add(Double.parseDouble(catchmentData[line][1]));

				/*
				 * check for time delay
				 */
				// if the delay is smaller than "maximumDelay" then record it to a new event
				if (delayHour > CatchmentSelectEvent.maximumDelay) {

					/*
					 * if the event period is more than 3 hours, record it
					 */
					if (event.size() >= CatchmentSelectEvent.minmumEventPeriod) {

						// record it
						List<String> recordEvent = getEventProperty(event, eventStart, rightTime);
						eventList.add(recordEvent.parallelStream().toArray(String[]::new));
					}

					/*
					 * reset the event list
					 */
					eventStart = catchmentData[line][0];
					event.clear();
				}
				/*
				 * if delay hour is bigger than 1, interpolation it
				 */
				else {
					for (int delay = 1; delay < delayHour; delay++) {
						event.add(0.);
					}
				}
			}
		}

		/*
		 * catchment end
		 */
		// output the event selected event, file name will be
		// "maximumDelay_minmumEventPeriod_eventProperty.csv"
		eventList.sort(Rainfall24H_Comparision.reversed());
		eventList.add(0, new String[] { "24H-Rainfall delay", "startTime", "endTime", "totalLength", "meanRainfall",
				"peakRainfall", "peakTime", "summaryRainfall", "rainfallData" });
		try {
			new AtFileWriter(eventList.parallelStream().toArray(String[][]::new),
					GlobalProperty.catchment_RainfallFolder + targetFolder + "\\" + CatchmentSelectEvent.maximumDelay
							+ "_" + CatchmentSelectEvent.minmumEventPeriod + "_"
							+ GlobalProperty.catchment_RainfallAnalysis_event).csvWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<String> getEventProperty(List<Double> eventData, String eventStart, String eventEnd) {
		/*
		 * +++++++++++++++++++++++++++++++ event ++++++++++++++++++++++++++++++
		 * 24H-Rainfall delay, startTime ,endTime, totalLength ,meanRainfall
		 * ,peakRainfall , peakTime , summaryRainfall ,rainfallData
		 */
		AtCommonMath eventStatics = new AtCommonMath(eventData);
		double maxValue = eventStatics.getMax();
		double summaryValue = eventStatics.getSum();

		// setting event property
		List<String> propertyList = new ArrayList<String>();
		propertyList.add(new BigDecimal(summaryValue * Math.pow(24./eventData.size(), 1./3))
				.setScale(3, BigDecimal.ROUND_HALF_UP).toString());
		propertyList.add(eventStart);
		propertyList.add(eventEnd);
		propertyList.add(String.format("%03d", eventData.size()));
		propertyList.add(new BigDecimal(eventStatics.getMean()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		propertyList.add(maxValue + "");
		propertyList.add(eventData.indexOf(maxValue) + "");
		propertyList.add(new BigDecimal(summaryValue).setScale(2, BigDecimal.ROUND_HALF_UP).toString());

		eventData.forEach(e -> propertyList.add(String.valueOf(e)));
		return propertyList;
	}

	/*
	 * Sorted function
	 */
	// <===============================================>

	/*
	 * 24H Rainfall
	 */
	private Comparator<String[]> Rainfall24H_Comparision = (eventData1, eventData2) -> {
		Double eventRainfall1 = Double.parseDouble(eventData1[0]);
		Double eventRainfall2 = Double.parseDouble(eventData2[0]);

		return eventRainfall1.compareTo(eventRainfall2);
	};

}
