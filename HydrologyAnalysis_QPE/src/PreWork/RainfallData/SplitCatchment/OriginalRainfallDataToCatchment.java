package PreWork.RainfallData.SplitCatchment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GlobalProperty.GlobalProperty;
import usualTool.TimeTranslate;

public class OriginalRainfallDataToCatchment extends GlobalProperty {

	/*
	 * To filter the rainfall data that upper than 0 and split the data to catchment
	 * folder
	 * 
	 * <-------------------PS--------------------> There are 24H of
	 * "every catchment" in one original file and timeStep is "HOUR"
	 */
	public static int threadNum = 8;
	public static String startTime = "201711121600";
	public static String endTime = "201801011600";
	public static String originalRainfallFolder = original_RainfallFloder;
	public static String locationRainfallFolder = catchment_RainfallFolder;

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		List<String> lostData = loseData();

		String temptTime = startTime;
		Map<Integer, Thread> threadMap = new HashMap<>();

		/*
		 * Initial threads
		 */
		for (int index = 0; index < threadNum; index++) {
			if (lostData.size() > 0) {
				threadMap.put(index, initialThread(lostData.get(0)));
				threadMap.get(index).start();
				lostData.remove(0);

			} else {
				threadMap.put(index, initialThread(temptTime));
				threadMap.get(index).start();
				temptTime = TimeTranslate.addDay(temptTime, "yyyyMMddHHmm");
			}
		}

		/*
		 * run threads
		 */
		while (Double.parseDouble(endTime) > Double.parseDouble(temptTime)) {
			for (int index = 0; index < threadNum; index++) {
				if (!threadMap.get(index).isAlive()) {

					// thread end
					System.out.println(threadMap.get(index).getName() + " end");
					temptTime = TimeTranslate.addDay(temptTime, "yyyyMMddHHmm");

					// renew a thread
					threadMap.remove(index);
					threadMap.put(index, initialThread(temptTime));
					threadMap.get(index).start();
				}
			}
		}

		/*
		 * ending
		 */
		while (!threadMap.isEmpty()) {
			for (int key : threadMap.keySet()) {
				if (!threadMap.get(key).isAlive()) {

					// thread end
					System.out.println(threadMap.get(key).getName() + " end");
					threadMap.remove(key);
				}
			}
		}
	}

	private static Thread initialThread(String temptTime) {
		Thread temptThread = new Thread(new OrigainlRainfallDataToCatchment_Thread(temptTime));
		temptThread.setName(temptTime);
		return temptThread;
	}

	private static List<String> loseData() {
		String[] timeList = new String[] {};
		return new ArrayList<String>(Arrays.asList(timeList));
	}

}
