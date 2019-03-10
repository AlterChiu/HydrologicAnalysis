package FileMover;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import usualTool.FileFunction;

public class MoveFile {
	private static int threadNum = 20;
	public static String fileAdd = "E:\\QpesumsAnalysis\\RainfallData\\catchment\\";
	public static String saveAdd = "H:\\RainfallData\\catchment\\";
	public static FileFunction ff = new FileFunction();
	public static String[] fileList = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int fileCount = 26;

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
					System.out.println(threadList.get(index).getName() + " end");
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
	}

	private static List<String> loseData() {
		String[] timeList = new String[] {"4"};
		return new ArrayList<String>(Arrays.asList(timeList));
	}

	private static Thread initialThread(String targetCatchment, int fileCount) {
		Thread temptThread = new Thread(new threadClass(fileAdd + targetCatchment, saveAdd + targetCatchment));
		temptThread.setName(fileCount + "");
		return temptThread;
	}

	public static class threadClass extends Thread {
		private String targetFolder = "";
		private String saveFolder = "";

		public threadClass(String targetFolder, String saveFolder) {
			this.targetFolder = targetFolder + "\\day\\";
			this.saveFolder = saveFolder + "\\day\\";
		}

		public void run() {
			String[] eventList = new File(this.targetFolder).list();
			for (String event : eventList) {
				ff.copyFile(targetFolder + event, saveFolder + event);
			}
		}
	}
}
