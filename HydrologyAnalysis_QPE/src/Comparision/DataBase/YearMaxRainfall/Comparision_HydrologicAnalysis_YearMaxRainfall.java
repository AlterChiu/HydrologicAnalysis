package Comparision.DataBase.YearMaxRainfall;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class Comparision_HydrologicAnalysis_YearMaxRainfall {

	public static Connection dataBaseConnection;
	public static int[] years = new int[] { 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017 };
	public static int[] eventDelays = new int[] { 24 };

	public static String shpFileAdd = "H:\\RainfallData\\Polygon\\MergeAll.shp";
	public static String shpFileSaveAdd = "H:\\RainfallData\\Polygon\\MaxRainfall";
	public static String folder_QPE_Analysis = "H:\\RainfallData\\statics\\年最大降雨量\\";

	public static void main(String[] args) throws ClassNotFoundException, IOException, SQLException {
		// TODO Auto-generated method stub
		/*
		 * read database
		 */
		dataBaseConnection = ReadDataBaseData.getDataBaseConnection();
		System.out.println("database connection successed");

		/*
		 * output shpFile
		 */

		// out attribute table type
		Map<String, String> outAttType = new TreeMap<>();
		outAttType.put("ID", "String");
		outAttType.put("error", "String");
		outAttType.put("asc_value", "Double");
		outAttType.put("db_value", "Double");
		outAttType.put("dif_value", "Double");

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
			for (int year : years) {

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
					double polygonDataBaseValue = ReadDataBaseData.getCatchmentReturnYearMaxValue(polygonID, year,
							eventDelay);

					// if database has no data with this polygon
					if (polygonDataBaseValue < 0) {
						polygonOutAttribute.put("error", "Database Error");
						System.out.print("\tdatabase error\t");
					} else {

						// set attribute table value for each polygon
						polygonOutAttribute = setOutAttributeTable(eventDelay, year, geoList.get(index),
								polygonDataBaseValue, polygonOutAttribute);
					}

					// save polygon feature
					outAtt.add(polygonOutAttribute);
					System.out.println("\tend");
				}

				// output the shpFile
				saveShpFile(outAtt, outAttType, geoList, eventDelay, year);
			}

		}

	}

	/*
	 * output the shpFile
	 */
	private static void saveShpFile(List<Map<String, Object>> outAtt, Map<String, String> outAttType,
			List<Geometry> geoList, int eventDelay, int year) {
		// output the shpFile
		SpatialWriter shpWriter = new SpatialWriter();
		shpWriter.setAttributeTable(outAtt);
		shpWriter.setField(outAttType);
		shpWriter.setGeoList(geoList);
		shpWriter.setCoordinateSystem(SpatialWriter.TWD97_121);
		shpWriter.saveAsShp(shpFileSaveAdd + String.format("%03d", eventDelay) + "_" + year + "year.shp");
		System.out.println("Create shpFile successed");
	}

	/*
	 * for eachPolygon
	 */
	private static Map<String, Object> setOutAttributeTable(int eventDelay, int year, Geometry temptPolygon,
			double polygonDataBaseValue, Map<String, Object> polygonOutAttribute) throws IOException {

		// read asciiFile and asciiValue;
		AsciiBasicControl ascii = new AsciiBasicControl(folder_QPE_Analysis + "\\" + eventDelay + "\\" + year + ".asc");
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

		// output the database value
		polygonOutAttribute.put("db_value",
				new BigDecimal(polygonDataBaseValue).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

		// output different between asciiFile and database (asciiFile - dataBase);
		polygonOutAttribute.put("dif_value", new BigDecimal(asciiMeanValue - polygonDataBaseValue)
				.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

		return polygonOutAttribute;
	}

}
