package AtRework.Rainfall.Original.Adaptor;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

import org.jsoup.nodes.Element;

import AtRework.Global;
import AtRework.GridModel.Grid;
import usualTool.AtCommonMath;
import usualTool.AtXmlReader;
import usualTool.TimeTranslate;

public class RainfallAdaptor {

	public RainfallAdaptor(String sourceFileFolder) throws IOException {
		for (String pixmlFile : new File(sourceFileFolder).list()) {
			AtXmlReader temptPiXml = new AtXmlReader(sourceFileFolder + pixmlFile, "UTF-8");

			temptPiXml.getNodeByTag("series").forEach(seriesElement -> {

				// get properties
				Element headerElement = seriesElement.getElementsByTag("header").get(0);
				String x = headerElement.getElementsByTag("x").get(0).text();
				String y = headerElement.getElementsByTag("y").get(0).text();
				RainfallModel rainfall = new RainfallModel(x, y);

				// get event(rainfall) value
				seriesElement.getElementsByTag("event").forEach(eventElement -> {
					String date = eventElement.getElementsByAttribute("date").text();
					String time = eventElement.getElementsByAttribute("time").text();
					double value = Double.parseDouble(eventElement.getElementsByAttribute("value").text());
					try {
						rainfall.addValueFromPiXml(date, time, value);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				});

				// update to grid floder
				Grid grid = new Grid(rainfall.getName());

				// check for folder is exist first
				if (grid.checkExist()) {
					try {
						grid.createFolder();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// add original value to grid object
				grid.addOriginalData(rainfall.getRainfall());

				// update
				try {
					grid.updateOriginalRainfall();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

		}
	}

	public class RainfallModel {

		private String name;
		private Map<String, String> temptValues;

		public RainfallModel(String x, String y) {
			this.name = AtCommonMath.getDecimal_String(x, Global.dataDecimal) + "_"
					+ AtCommonMath.getDecimal_String(y, Global.dataDecimal);
		}

		public void addValueFromPiXml(String date, String time, double value) throws ParseException {
			String dateString = TimeTranslate.getDateStringTranslte(date, "yyyy-MM-dd", Global.dateFormat);
			String timeString = time.split(":")[0];
			String timeKey = dateString + " " + timeString;

			if (value > Global.minRainfallPerHour && value < Global.maxRainfallPerHour) {
				this.temptValues.put(timeKey, AtCommonMath.getDecimal_String(value, Global.dataDecimal));
			}
		}

		public String getName() {
			return this.name;
		}

		public Map<String, String> getRainfall() {
			return this.temptValues;
		}
	}

}
