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
import usualTool.TimeTranslate;

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
		this.originalDataPath = this.folderPath + "\\originalData";
	}

	public Grid(String gridName) {
		this.x = AtCommonMath.getDecimal_Double(gridName.split("_")[0], Global.dataDecimal);
		this.y = AtCommonMath.getDecimal_Double(gridName.split("_")[1], Global.dataDecimal);
		this.name = gridName;
		this.folderPath = Global.rainfallFolder + this.name;
		this.yearMaxPath = this.folderPath + "\\yearMax.csv";
		this.originalDataPath = this.folderPath + "\\originalData";
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

	public boolean checkYearOriginalExist(String year) throws IOException {
		if (!new File(this.originalDataPath + year).exists()) {
			// initial folder datas
			new AtFileWriter("DateTime(yyyy/MM/dd HH),rainfallValue(mm)", this.originalDataPath + year).csvWriter();
			System.out.println("*NOTICE* file created, " + this.originalDataPath + year);
		}
		return true;
	}

	public boolean checkGridFolderExist() {
		if (!new File(this.folderPath).exists()) {
			createFolder();
		}
		return true;
	}

	public void createFolder() {
		try {
			// create folder
			AtFileFunction.createFolder(this.folderPath);
			new AtFileWriter("year(yyyy),duration,rainfallValue(mm)", this.yearMaxPath).csvWriter();
			System.out.println("*NOTICE* folder create, " + this.yearMaxPath);
		} catch (Exception e) {
			new Exception("*WARN* folder create faild");
		}
	}

	// Original data
	// <=======================================================>
	public Map<String, String> getOriginalRainfall(String year) throws Exception {
		this.checkYearOriginalExist(year);

		Map<String, String> outMap = new TreeMap<>();
		String[][] originalContent = new AtFileReader(this.originalDataPath + year).getCsv(1, 0);

		for (String temptLine[] : originalContent) {
			try {
				outMap.put(temptLine[0], temptLine[1]);
			} catch (Exception e) {
			}
		}

		return outMap;
	}

	public void addOriginalData(Map<String, String> dateValue) {
		dateValue.keySet().forEach(key -> {
			try {
				this.addOriginalData(key, dateValue.get(key));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public void addOriginalData(String date, String value) throws Exception {
		try {
			TimeTranslate.getDateLong(date, Global.timeFormat);
			this.temptOriginalData.put(date, value);
		} catch (Exception e) {
			throw new Exception("*ERROR* not allowable date formate");
		}
	}

	public void updateOriginalRainfall() throws Exception {

		// year , date , value
		Map<String, Map<String, String>> originalRainfall = new HashMap<>();
		for (String date : this.temptOriginalData.keySet()) {

			// get the year original rainfall
			String year = TimeTranslate.getDateStringTranslte(date, Global.dateFormat, "yyyy");
			Map<String, String> yearRainfall = Optional.ofNullable(originalRainfall.get(year))
					.orElse(this.getOriginalRainfall(year));

			yearRainfall.put(date, this.temptOriginalData.get(date));
			originalRainfall.put(year, yearRainfall);
		}
		this.temptOriginalData.clear();

		// output each year of original
		originalRainfall.keySet().forEach(year -> {
			Map<String, String> temptRainfall = originalRainfall.get(year);
			List<String[]> outValue = new ArrayList<>();
			outValue.add(new String[] { "DateTime(yyyy/MM/dd HH)", "rainfallValue(mm)" });

			temptRainfall.keySet().forEach(date -> {
				outValue.add(new String[] { date, temptRainfall.get(date) });
			});
			try {
				new AtFileWriter(outValue.parallelStream().toArray(String[][]::new), this.originalDataPath + year)
						.csvWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	// YearMax
	// <=======================================================>
	public Map<Integer, Map<Integer, String>> getYearMaxRainfall() throws Exception {
		this.checkGridFolderExist();

		Map<Integer, Map<Integer, String>> outMap = new TreeMap<>();
		String[][] originalContent = new AtFileReader(this.yearMaxPath).getCsv(1, 0);

		for (String temptLine[] : originalContent) {
			try {
				int year = Integer.parseInt(temptLine[0]);
				int duration = Integer.parseInt(temptLine[1]);
				String rainfall = temptLine[2];

				Map<Integer, String> yearMap = Optional.ofNullable(outMap.get(year)).orElse(new HashMap<>());
				yearMap.put(duration, rainfall);
				outMap.put(year, yearMap);
			} catch (Exception e) {
			}
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
			Map<Integer, String> temptYearMap = Optional.ofNullable(originalMap.get(yearMax.year))
					.orElse(new HashMap<>());
			temptYearMap.put(yearMax.duration, yearMax.value);
			originalMap.put(yearMax.year, temptYearMap);
		});

		// updata file
		List<String[]> outValue = new ArrayList<>();
		outValue.add(new String[] { "year(yyyy) ", "duration", "rainfallValue(mm)" });

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
