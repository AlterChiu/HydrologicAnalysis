package AtRework.GridModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import AtRework.Global;
import usualTool.AtCommonMath;

import usualTool.AtFileFunction;
import usualTool.AtFileReader;
import usualTool.AtFileWriter;

public class Grid {

	private double x;
	private double y;

	private String name;
	private String folderPath;
	private String yearMaxPath;
	private String originalDataPath;

	private Map<String, String> temptOriginalData = new HashMap<>();
	private List<YearMax> temptYearMaxData = new ArrayList<>();

	// xy coordination in EPSG:4326(WGS84)
	public Grid(double x, double y) {
		this.x = AtCommonMath.getDecimal_Double(x, Global.dataDecimal);
		this.y = AtCommonMath.getDecimal_Double(y, Global.dataDecimal);
		this.name = AtCommonMath.getDecimal_String(x, Global.dataDecimal) + "_"
				+ AtCommonMath.getDecimal_String(y, Global.dataDecimal);
		this.folderPath = Global.rainfallFolder + this.name;
		this.yearMaxPath = this.folderPath + "\\yearMax.csv";
		this.originalDataPath = this.folderPath + "\\originalData.csv";
	}

	public Grid(String gridName) {
		this.x = AtCommonMath.getDecimal_Double(gridName.split("_")[0], Global.dataDecimal);
		this.y = AtCommonMath.getDecimal_Double(gridName.split("_")[1], Global.dataDecimal);
		this.name = gridName;
		this.folderPath = Global.rainfallFolder + this.name;
		this.yearMaxPath = this.folderPath + "\\yearMax.csv";
		this.originalDataPath = this.folderPath + "\\originalData.csv";
	}

	public String getName() {
		return this.name;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}

	public boolean checkExsist() {
		return new File(this.folderPath).exists();
	}

	public void createFolder() throws IOException {
		if (!this.checkExsist()) {

			// create folder
			AtFileFunction.createFolder(this.folderPath);

			// initial folder datas
			new AtFileWriter("DateTime(yyyy/MM/dd HH),rainfallValue(mm)", this.originalDataPath).csvWriter();
			new AtFileWriter("year(yyyy),ReturnYear,rainfallValue(mm)", this.yearMaxPath).csvWriter();
		}
	}

	// Original data
	// <=======================================================>
	public Map<String, String> getOriginalRainfall() throws Exception {
		if (!this.checkExsist()) {
			throw new Exception("*ERROR* Grid Folder not exsist, " + this.name);
		}

		Map<String, String> outMap = new TreeMap<>();
		String[][] originalContent = new AtFileReader(this.originalDataPath).getCsv(1, 0);

		for (String temptLine[] : originalContent) {
			outMap.put(temptLine[0], temptLine[1]);
		}

		return outMap;
	}

	public void addOriginalData(String date, String value) {

		this.temptOriginalData.put(date, value);
	}

	public void updateOriginalRainfall() throws Exception {
		Map<String, String> originalRainfall = this.getOriginalRainfall();
		this.temptOriginalData.keySet().forEach(date -> {
			originalRainfall.put(date, this.temptOriginalData.get(date));
		});

		List<String[]> outValue = new ArrayList<>();
		outValue.add(new String[] { " DateTime(yyyy/MM/dd HH)", "rainfallValue(mm)" });

		originalRainfall.keySet().forEach(date -> {
			outValue.add(new String[] { date, originalRainfall.get(date) });
		});
		this.temptOriginalData.clear();

		new AtFileWriter(outValue.parallelStream().toArray(String[][]::new), this.originalDataPath).csvWriter();
	}

	// YearMax
	// <=======================================================>
	public Map<Integer, Map<Integer, String>> getYearMaxRainfall() throws Exception {
		if (!this.checkExsist()) {
			throw new Exception("*ERROR* Grid Folder not exsist, " + this.name);
		}

		Map<Integer, Map<Integer, String>> outMap = new TreeMap<>();
		String[][] originalContent = new AtFileReader(this.yearMaxPath).getCsv(1, 0);

		for (String temptLine[] : originalContent) {
			int year = Integer.parseInt(temptLine[0]);
			int duration = Integer.parseInt(temptLine[1]);
			String rainfall = temptLine[2];

			Map<Integer, String> yearMap = Optional.ofNullable(outMap.get(year)).orElse(new HashMap<>());
			yearMap.put(duration, rainfall);
			outMap.put(year, yearMap);
		}

		return outMap;
	}

	public void addYearMax(int year, int duration, String value) {
		temptYearMaxData.add(new YearMax(year, duration, value));
	}

	public void updateYearMax() throws Exception {
		// update data
		Map<Integer, Map<Integer, String>> originalMap = this.getYearMaxRainfall();
		this.temptYearMaxData.forEach(yearMax -> {
			Map<Integer, String> temptYearMap = Optional.of(originalMap.get(yearMax.year)).orElse(new HashMap<>());
			temptYearMap.put(yearMax.duration, yearMax.value);
			originalMap.put(yearMax.year, temptYearMap);
		});

		// updata file
		List<String[]> outValue = new ArrayList<>();
		outValue.add(new String[] { " year(yyyy) ", "ReturnYear", "rainfallValue(mm)" });

		// convert date from map to list
		originalMap.keySet().forEach(year -> {
			Map<Integer, String> temptYearMap = originalMap.get(year);
			temptYearMap.keySet().forEach(duration -> {
				outValue.add(
						new String[] { String.valueOf(year), String.valueOf(duration), temptYearMap.get(duration) });
			});
		});
		this.temptYearMaxData.clear();
		new AtFileWriter(outValue.parallelStream().toArray(String[][]::new), this.yearMaxPath).csvWriter();
	}

	private class YearMax {
		int year;
		int duration;
		String value;

		public YearMax(int year, int duration, String value) {
			this.year = year;
			this.duration = duration;
			this.value = value;
		}
	}

}
