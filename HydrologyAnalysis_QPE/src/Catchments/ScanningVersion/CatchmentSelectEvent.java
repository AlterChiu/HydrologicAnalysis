package Catchments.ScanningVersion;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GlobalProperty.GlobalProperty;

public class CatchmentSelectEvent extends GlobalProperty {

	/*
	 * select the event from each catchment by given time delay
	 */
	private static int threadNum = 4;
	public static String fileAdd = catchment_RainfallFolder;
	public static String[] fileList = null;
	public static int eventDelayTime = 1;
	public static String inputFile = GlobalProperty.catchment_RainfallAnalysis_total;
	public static String saveName = "\\" + CatchmentSelectEvent.eventDelayTime + "_eventDelay_Rainfall.csv";

	public static void main(String[] args) throws IOException, ParseException {
		// TODO Auto-generated method stub
		int delayList[] = new int[] { 48 };
		for (int delay : delayList) {
			eventDelayTime = delay;
			saveName = "\\" + CatchmentSelectEvent.eventDelayTime + "_eventDelay_Rainfall.csv";

			int fileCount = 0;

			// the fileList here is the name of catchments
			List<String> catchmentsList = new ArrayList<>(Arrays.asList(fileList = new File(fileAdd).list()));
			List<String> lostData = loseData();
			int fileLength = catchmentsList.size();

			/*
			 * initial thread
			 */
			Map<Integer, Thread> threadList = new HashMap<>();
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
						System.out.println(delay + "_" + threadList.get(index).getName() + " end");

						if (fileCount == fileLength) {
							break runThread;
						} else {
							threadList.remove(index);
							threadList.put(index, initialThread(catchmentsList.get(fileCount), fileCount));
							threadList.get(index).start();
						}

						fileCount++;
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
	}

	private static Thread initialThread(String targetCatchment, int fileCount) {
		Thread temptThread = new Thread(new CatchmentSelectEvent_Thread(targetCatchment));
		temptThread.setName(fileCount + "");
		return temptThread;
	}

	private static List<String> loseData() {
		String[] timeList = new String[] { "22786" };
		return new ArrayList<String>(Arrays.asList(timeList));
	}

}
