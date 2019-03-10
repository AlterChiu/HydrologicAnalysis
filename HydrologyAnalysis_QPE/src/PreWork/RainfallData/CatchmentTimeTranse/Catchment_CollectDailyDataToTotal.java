package PreWork.RainfallData.CatchmentTimeTranse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import GlobalProperty.GlobalProperty;

public class Catchment_CollectDailyDataToTotal extends GlobalProperty {

	/*
	 * Collect the daily data from catchment make it to the total dataSet
	 */
	public static int threadNum = 4;
	public static Double startYear = 200601011600.;
	public static Double endYear = 201801011600.;
	public static String fileAdd = catchment_RainfallFolder;
	public static String[] fileList = null;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int fileCount = 22783;

		// the fileList here is the name of catchments
		List<String> catchmentsList = new ArrayList<>(Arrays.asList(fileList = new File(fileAdd).list()));
		List<String> lostData = loseData();
		int fileLength = catchmentsList.size();

		/*
		 * initial thread
		 */
		Map<Integer, Thread> threadList = new TreeMap<>();
		for (int index = 0; index < threadNum; index++) {
			if (lostData.size() > 0) {
				threadList.put(index, initialThread(catchmentsList.get(Integer.parseInt(lostData.get(0))),
						Integer.parseInt(lostData.get(0))));
				threadList.get(index).start();
				lostData.remove(0);

			} else {
				threadList.put(index, initialThread(catchmentsList.get(fileCount), fileCount));
				fileCount++;
				threadList.get(index).start();
			}
		}

		/*
		 * run thread
		 */
		runThread: while (fileCount < fileLength) {
			for (int index = 0; index < threadNum; index++) {
				if (!threadList.get(index).isAlive()) {
//					System.out.println(threadList.get(index).getName() + " end");
					fileCount++;

					if (fileCount == fileLength) {
						break runThread;
					} else {
						threadList.remove(index);
						threadList.put(index, initialThread(catchmentsList.get(fileCount), fileCount));
						threadList.get(index).start();
					}
				}
			}
		}

		/*
		 * end thread
		 */
		while (threadList.size() > 0) {
			for (Object index : threadList.keySet().toArray()) {
				if (!threadList.get(index).isAlive()) {
					System.out.println(threadList.get(index).getName() + " end");
					threadList.remove(index);
				}
			}
		}
	}

	private static Thread initialThread(String targetCatchment, int fileCount) {
		Thread temptThread = new Thread(new Catchment_CollectDailyDataToTotal_Thread(targetCatchment));
		temptThread.setName(fileCount + "");
		return temptThread;
	}

	private static List<String> loseData() {
		String[] timeList = new String[] { "22786"};
		return new ArrayList<String>(Arrays.asList(timeList));
	}

}
