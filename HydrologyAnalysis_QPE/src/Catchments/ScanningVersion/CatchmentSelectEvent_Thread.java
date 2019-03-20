package Catchments.ScanningVersion;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

		/*
		 * eventStart
		 */
		for (int startLine = 0; startLine < catchmentData.length - 1; startLine++) {
			String startTime = catchmentData[startLine][0];

			if (Double.parseDouble(startTime) >= CatchmentSelectEvent.startYear
					&& Double.parseDouble(startTime) < CatchmentSelectEvent.endYear) {

				/*
				 * createMap
				 */
				Map<Integer, Double> eventValue = new TreeMap<Integer, Double>();
				for (int delay = 0; delay < CatchmentSelectEvent.eventDelayTime; delay++) {
					eventValue.put(delay, 0.);
				}
				eventValue.put(0, Double.parseDouble(catchmentData[startLine][1]));

				/*
				 * eventEnd
				 */
				for (int endLine = startLine + 1; endLine < catchmentData.length
						- CatchmentSelectEvent.eventDelayTime; endLine++) {
					String temptTime = catchmentData[endLine][0];
					int skipHour = 999;
					try {
						skipHour = new BigDecimal((TimeTranslate.StringToLong(temptTime, "yyyyMMddHH")
								- TimeTranslate.StringToLong(startTime, "yyyyMMddHH")) / 3600000.)
										.setScale(1, BigDecimal.ROUND_HALF_UP).intValue();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (skipHour < CatchmentSelectEvent.eventDelayTime) {
						eventValue.put(skipHour, Double.parseDouble(catchmentData[endLine][1]));
					} else {
						break;
					}
				}

				/*
				 * compute event rainfall
				 */
				List<Double> temptList = new ArrayList<>();
				for (int delay = 0; delay < CatchmentSelectEvent.eventDelayTime; delay++) {
					temptList.add(eventValue.get(delay));
				}
				try {
					eventList.add(getEventProperty(temptList, startTime,
							TimeTranslate.addHour(startTime, "yyyyMMddHH", CatchmentSelectEvent.eventDelayTime - 1))
									.parallelStream().toArray(String[]::new));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				"peakRainfall", "peakTime", "summaryRainfall" });
		try {
			new AtFileWriter(eventList.parallelStream().toArray(String[][]::new),
					GlobalProperty.catchment_RainfallFolder + targetFolder + "\\" + CatchmentSelectEvent.saveName)
							.csvWriter();
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
		double summaryValue = eventStatics.getSum(2);

		// setting event property
		List<String> propertyList = new ArrayList<String>();
		propertyList.add(summaryValue + "");
		propertyList.add(eventStart);
		propertyList.add(eventEnd);
		propertyList.add(String.format("%03d", eventData.size()));
		propertyList.add(new BigDecimal(eventStatics.getMean()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
		propertyList.add(maxValue + "");
		propertyList.add(eventData.indexOf(maxValue) + "");
		propertyList.add(new BigDecimal(summaryValue).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
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