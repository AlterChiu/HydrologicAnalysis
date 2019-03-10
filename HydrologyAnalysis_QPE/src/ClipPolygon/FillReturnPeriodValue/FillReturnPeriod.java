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
		List<Map<String, String>> shpAttribute = shpFile.getAttributeTable();
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

				// get the boundary of the polygon
				Rectangle temptPath = GdalGlobal.GeomertyToPath2D(temptPolygon).getBounds();

				double maxX = temptPath.getMaxX();
				double minX = temptPath.getMinX();
				double maxY = temptPath.getMaxY();
				double minY = temptPath.getMinY();

				double startCoordinate_WGS84[] = CoordinateTranslate.Twd97ToWgs84(maxX, maxY);
				double endCoordinate_WGS84[] = CoordinateTranslate.Twd97ToWgs84(minX, minY);
				// translate from twd97 to wgs84
				int startPosition[] = rainfallAscii.getPosition(startCoordinate_WGS84[0], endCoordinate_WGS84[1]);
				int endPosition[] = rainfallAscii.getPosition(endCoordinate_WGS84[0], endCoordinate_WGS84[1]);

				// scanning the grid which contacted the polygon
				List<Double> valueList = new ArrayList<>();
				for (int row = startPosition[1]; row <= endPosition[1]; row++) {
					for (int column = startPosition[0]; column <= endPosition[0]; column++) {
						// check the grid center is inside the polygon or not
						double[] gridCoordinate = rainfallAscii.getCoordinate(column, row);
						if (temptPath.contains(gridCoordinate[0], gridCoordinate[1])) {
							valueList.add(Double.parseDouble(rainfallAscii.getValue(column, row)));
						}
					}
				}

				// if there is no grid center in the polygon
				if (valueList.size() == 0) {
					feature.put(returnPeriod,
							Double.parseDouble(rainfallAscii.getValue((maxX + minX) / 2, (minY + maxY) / 2)));
				} else {
					feature.put(returnPeriod, (Double) new AtCommonMath(valueList).getMean());
				}
			}

			// set the output attribute feature of shape file
			attribute.add(feature);

		}

		// output the shape file
		SpatialWriter spWriter = new SpatialWriter();
		spWriter.setField(attributeType);
		spWriter.setAttributeTable(attribute);
		spWriter.setGeoList(geoList);
		spWriter.saveAsShp(polygonSaveAdd);
	}
}