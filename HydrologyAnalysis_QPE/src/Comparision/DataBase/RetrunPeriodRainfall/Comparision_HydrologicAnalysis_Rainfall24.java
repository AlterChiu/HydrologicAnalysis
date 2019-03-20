package Comparision.DataBase.RetrunPeriodRainfall;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import Comparision.DataBase.ReadDataBaseData;
import asciiFunction.AsciiBasicControl;
import geo.common.CoordinateTranslate;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;

public class Comparision_HydrologicAnalysis_Rainfall24 {

	public static Connection dataBaseConnection;
	public static String distributionNameList[] = new String[] { "EV1", "LN3", "LPT3", "PT3" };
	public static int[] eventDelays = new int[] { 24};
	public static int returnPeriod = 200;

	public static String shpFileAdd = "H:\\RainfallData\\Polygon\\MergeAll.shp";
	public static String shpFileSaveAdd = "H:\\RainfallData\\Polygon\\Rainfall";
	public static String folder_QPE_Analysis = "H:\\RainfallData\\createRainfall\\掃描式事件篩選方法\\取年最大\\";

	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		/*
		 * read database
		 */
		dataBaseConnection = ReadDataBaseData.getDataBaseConnection();
		System.out.println("database connection successed");

		// out attribute table type
		Map<String, String> outAttType = new TreeMap<>();
		outAttType.put("ID", "String");
		outAttType.put("asc_value", "Double");
		outAttType.put("db_value", "Double");
		outAttType.put("dif_value", "Double");
		outAttType.put("error", "String");

		/*
		 * get shpFile property
		 */
		SpatialReader shpFile = new SpatialReader(shpFileAdd);
		List<Geometry> geoList = shpFile.getGeometryList();
		List<Map<String, String>> attList = shpFile.getAttributeTable();
		System.out.println("read shpFile successed");

		// for each eventDelay
		for (int eventDelay : eventDelays) {

			// for each distribution
			for (String distribution : distributionNameList) {

				// out attribute tale
				List<Map<String, Object>> outAtt = new ArrayList<>();

				// for each polygon
				for (int index = 0; index < geoList.size(); index++) {

					// setting attribute table for output shpFile
					Map<String, Object> polygonOutAttribute = new TreeMap<>();

					// set output shpFile property
					String polygonID = attList.get(index).get("ID");
					polygonOutAttribute.put("ID", polygonID);
					System.out.print(index + "\t" + polygonID);

					// get from database
					Map<Integer, Double> polygonDataBaseValue = ReadDataBaseData
							.getCatchmentReturnPeriodValue(polygonID, returnPeriod, eventDelay);

					// if database has no data with this polygon
					if (polygonDataBaseValue.keySet().size() == 0) {
						polygonOutAttribute.put("error", "Database Error");
						System.out.print("\tdatabase error\t");
					} else {

						// set attribute table value for each polygon
						polygonOutAttribute = setOutAttributeTable(eventDelay, distribution, geoList.get(index),
								polygonDataBaseValue, polygonOutAttribute);
					}

					// save polygon feature
					outAtt.add(polygonOutAttribute);
					System.out.println("\tend");

				}

				// output the shpFile
				saveShpFile(outAtt, outAttType, geoList, eventDelay, distribution);
			}

		}

	}

	/*
	 * output the shpFile
	 */
	private static void saveShpFile(List<Map<String, Object>> outAtt, Map<String, String> outAttType,
			List<Geometry> geoList, int eventDelay, String distribution) {
		// output the shpFile
		SpatialWriter shpWriter = new SpatialWriter();
		shpWriter.setAttributeTable(outAtt);
		shpWriter.setField(outAttType);
		shpWriter.setGeoList(geoList);
		shpWriter.setCoordinateSystem(SpatialWriter.TWD97_121);
		shpWriter.saveAsShp(shpFileSaveAdd + String.format("%03d", eventDelay) + "_Year" + returnPeriod + "_"
				+ distribution + ".shp");
		System.out.println("Create shpFile successed");
	}

	/*
	 * for eachPolygon
	 */
	private static Map<String, Object> setOutAttributeTable(int eventDelay, String distribution, Geometry temptPolygon,
			Map<Integer, Double> polygonDataBaseValue, Map<String, Object> polygonOutAttribute) throws IOException {

		// read asciiFile and asciiValue;
		AsciiBasicControl ascii = new AsciiBasicControl(
				folder_QPE_Analysis + "\\" + eventDelay + "\\" + distribution + "_" + returnPeriod + ".asc");
		String temptAsciiValue = ascii
				.getValue(GdalGlobal.geometryTranlster(temptPolygon, GdalGlobal.TWD97_121, GdalGlobal.WGS84));
		double asciiMeanValue = Double.parseDouble(temptAsciiValue);
		if (temptAsciiValue.equals(ascii.getNullValue())) {
			polygonOutAttribute.put("asc_value", 0.);
			asciiMeanValue = 0;
		} else {
			polygonOutAttribute.put("asc_value",
					new BigDecimal(temptAsciiValue).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		}

		// read from database and output
		double temptDataBaseValue = polygonDataBaseValue.get(distributionTypeIdMapping(distribution));
		polygonOutAttribute.put("db_value", temptDataBaseValue);

		// output different between asciiFile and database (asciiFile - dataBase);
		polygonOutAttribute.put("dif_value", new BigDecimal(asciiMeanValue - temptDataBaseValue)
				.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

		return polygonOutAttribute;
	}

	/*
	 * distribution mapping
	 */
	private static int distributionTypeIdMapping(String distributionType) {
		if (distributionType.toUpperCase().equals("NORMAL")) {
			return 1;
		} else if (distributionType.toUpperCase().equals("LOGNORMAL")) {
			return 2;
		} else if (distributionType.toUpperCase().equals("GAMMA")) {
			return 3;
		} else if (distributionType.toUpperCase().equals("EXPONENTIAL")) {
			return 4;
		} else if (distributionType.toUpperCase().equals("GUMBEL")) {
			return 5;
		} else if (distributionType.toUpperCase().equals("PT3")) {
			return 6;
		} else if (distributionType.toUpperCase().equals("LPT3")) {
			return 7;
		} else if (distributionType.toUpperCase().equals("EV1")) {
			return 8;
		} else if (distributionType.toUpperCase().equals("GPA")) {
			return 9;
		} else if (distributionType.toUpperCase().equals("GENERALISED_LOGISTIC")) {
			return 10;
		} else if (distributionType.toUpperCase().equals("WEL")) {
			return 11;
		} else if (distributionType.toUpperCase().equals("N3")) {
			return 12;
		} else if (distributionType.toUpperCase().equals("LN3")) {
			return 13;
		} else {
			return 0;
		}
	}

}
