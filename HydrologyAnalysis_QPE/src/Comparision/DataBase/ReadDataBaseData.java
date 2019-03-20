package Comparision.DataBase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import Comparision.DataBase.RetrunPeriodRainfall.Comparision_HydrologicAnalysis_Rainfall24;
import DataBase.Connection.JDBCConnectionStatement;
import DataBase.Connection.Result.ResultSetSection;
import DataBase.Setting.BasicSetting;
import Comparision.DataBase.YearMaxRainfall.Comparision_HydrologicAnalysis_YearMaxRainfall;

public class ReadDataBaseData {

	/*
	 * compare for rainfall24
	 */
	public static Map<Integer, Double> getCatchmentReturnPeriodValue(String chatchment, int returnPeriod,
			int eventDelay) throws SQLException, ClassNotFoundException {
		Map<Integer, Double> temptMap = new TreeMap<Integer, Double>();

		StringBuilder sqlCommand = new StringBuilder();
		sqlCommand.append("Select ");
		sqlCommand.append("DistributionID , ");
		sqlCommand.append("Rainfall" + String.format("%03d", eventDelay) + " ");
		sqlCommand.append("from WRA_QPESUMS.FrequencyAnalysis ");
		sqlCommand.append("where ");
		sqlCommand.append("Station = \"" + chatchment + "\" AND ");
		sqlCommand.append("CreateYear = \"2017\" AND ");
		sqlCommand.append("ReturnPeriod = \"" + returnPeriod + "\" AND ");
		sqlCommand.append("ReturnPeriod = \"" + returnPeriod + "\"");

		ResultSet result = Comparision_HydrologicAnalysis_Rainfall24.dataBaseConnection
				.prepareStatement(sqlCommand.toString()).executeQuery();
		List<String[]> resultArray = new ResultSetSection().getSectionResult(result);
		for (String[] temptLine : resultArray) {
			temptMap.put(Integer.parseInt(temptLine[0]),
					new BigDecimal(temptLine[1]).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
		}
		return temptMap;
	}

	/*
	 * compare for year max rainfall
	 */
	public static double getCatchmentReturnYearMaxValue(String chatchment, int year, int eventDelay)
			throws SQLException, ClassNotFoundException {

		StringBuilder sqlCommand = new StringBuilder();
		sqlCommand.append("Select ");
		sqlCommand.append("Rainfall" + String.format("%03d", eventDelay) + " ");
		sqlCommand.append("from WRA_QPESUMS.ExtractAnnualMax ");
		sqlCommand.append("where ");
		sqlCommand.append("Station = \"" + chatchment + "\" AND ");
		sqlCommand.append("CreateYear = \"" + year + "\"");

		ResultSet result = Comparision_HydrologicAnalysis_YearMaxRainfall.dataBaseConnection
				.prepareStatement(sqlCommand.toString()).executeQuery();
		List<String[]> resultArray = new ResultSetSection().getSectionResult(result);

		try {
			return Double.parseDouble(resultArray.get(0)[0]);
		} catch (Exception e) {
			return -99;
		}

	}

	public static final Connection getDataBaseConnection() throws ClassNotFoundException {
		JDBCConnectionStatement connectionStatement = new JDBCConnectionStatement();
		connectionStatement.setDriver(BasicSetting.SQLSERVER_DRIVER);
	
		return connectionStatement.getConnection();
	}
}
