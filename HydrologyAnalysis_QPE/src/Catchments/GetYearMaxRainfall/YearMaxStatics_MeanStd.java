package Catchments.GetYearMaxRainfall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import GlobalProperty.GlobalProperty;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.AtCommonMath;

public class YearMaxStatics_MeanStd {

	private static String saveAdd = "H:\\RainfallData\\Polygon\\MaxRainfall024_";

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		List<SpatialReader> shpFileList = new ArrayList<SpatialReader>();
		for (int year = GlobalProperty.startYear; year <= GlobalProperty.endYear; year++) {
			shpFileList.add(new SpatialReader(saveAdd + year + "year.shp"));
		}

		List<Geometry> outGeoList = shpFileList.get(0).getGeometryList();
		List<Map<String, Object>> outAttrTable = new ArrayList<>();
		Map<String, String> outAttrType = new TreeMap<>();
		outAttrType.put("ID", "String");
		outAttrType.put("mean", "Double");
		outAttrType.put("std", "Double");

		for (int index = 0; index < outGeoList.size(); index++) {
			Map<String, Object> outFeature = new TreeMap<>();
			List<Double> valueList = new ArrayList<>();

			try {

				for (int year = 0; year < shpFileList.size(); year++) {
					Map<String, String> temptFeature = shpFileList.get(year).getAttributeTable().get(index);
					valueList.add(Double.parseDouble(temptFeature.get("dif_value")));
				}

				AtCommonMath math = new AtCommonMath(valueList);
				outFeature.put("ID", shpFileList.get(0).getAttributeTable().get(index).get("ID"));
				outFeature.put("mean", math.getMean());
				outFeature.put("std", math.getStd());
				outAttrTable.add(outFeature);

			} catch (Exception e) {
				outFeature.put("ID", shpFileList.get(0).getAttributeTable().get(index).get("ID"));
				outAttrTable.add(outFeature);
			}
		}

		SpatialWriter shpWriter = new SpatialWriter();
		shpWriter.setFieldType(outAttrType);
		shpWriter.setGeoList(outGeoList);
		shpWriter.setAttribute(outAttrTable);
		shpWriter.saveAsShp(saveAdd + "_Statics.shp");
	}

}
