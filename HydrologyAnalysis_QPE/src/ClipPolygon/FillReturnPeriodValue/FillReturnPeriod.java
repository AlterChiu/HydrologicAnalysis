package ClipPolygon.FillReturnPeriodValue;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gdal.ogr.Geometry;

import asciiFunction.AsciiBasicControl;
import geo.common.CoordinateTranslate;
import geo.gdal.GdalGlobal;
import geo.gdal.SpatialReader;
import geo.gdal.SpatialWriter;
import usualTool.AtCommonMath;

public class FillReturnPeriod {

	public static String polygonAdd = "H:\\SHP\\dongman_merge(Sobek).shp";
	public static String rainfallDataAdd = "H:\\RainfallData\\createRainfall\\掃描式事件篩選方法\\Hour\\24\\";
	public static String polygonSaveAdd = "H:\\RainfallData\\createRainfall\\掃描式事件篩選方法\\Hour\\24\\dongman_merge(Sobek).shp";

	public static String[] rainfallReturenPeriod = new String[] { "EV1_200", "LN3_200", "LPT3_200", "PT3_200" };

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		// shape file function
		SpatialReader shpFile = new SpatialReader(polygonAdd);
		List<Map<String, Object>> shpAttribute = shpFile.getAttributeTable();
		/* get the polygon list */
		List<Geometry> geoList = shpFile.getGeometryList();
		/* set the attribute table */
		List<Map<String, Object>> attribute = new ArrayList<>();
		/* set the attribute field type */
		Map<String, String> attributeType = new TreeMap<>();
		for (String returnPeriod : rainfallReturenPeriod) {
			attributeType.put(returnPeriod, "Double");
		}

		// get the each polygon in the shape file

		for (int index = 0; index < geoList.size(); index++) {
			Geometry temptPolygon = geoList.get(index);

			Map<String, Object> feature = new TreeMap<>();
			for (String returnPeriod : rainfallReturenPeriod) {
				AsciiBasicControl rainfallAscii = new AsciiBasicControl(rainfallDataAdd + returnPeriod + ".asc");
				String temptValue = rainfallAscii.getValue(temptPolygon);

				if (!temptValue.equals(rainfallAscii.getNullValue())) {
					feature.put(returnPeriod, (Double) Double.parseDouble(temptValue));
				} else {
					feature.put(returnPeriod, (Double) 0.0);
				}

			}

			// set the output attribute feature of shape file
			attribute.add(feature);

		}

		// output the shape file
		SpatialWriter spWriter = new SpatialWriter();
		spWriter.setFieldType(attributeType);
		for (int index = 0; index < geoList.size(); index++) {
			spWriter.addFeature(geoList.get(index), attribute.get(index));
		}
		spWriter.saveAsShp(polygonSaveAdd);
	}
}