package com.swissbit.server.ws.util;

import java.util.List;

import com.swissbit.server.ws.model.Log;
import com.swissbit.server.ws.model.Logs;

public final class LogUtil {

	public static String generateXML(final List<Log> logs) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<logs type='application'>");
		for (final Log log : logs) {
			stringBuilder
					.append("<log time='" + log.getLogTime() + "' description='" + log.getLogDescription() + "'/>");
			stringBuilder.append(System.lineSeparator());
		}
		stringBuilder.append("</logs>");
		return stringBuilder.toString();
	}

	public static Logs parseLogs(final String logs) {
		final String[] logLines = logs.split(System.lineSeparator());
		final Logs logList = new Logs();
		for (final String log : logLines) {
			final String logTime = log.substring(0, 23);
			final String logDesc = log.substring(24, log.length());
			final Log logData = new Log();
			logData.setLogTime(logTime);
			logData.setLogDescription(logDesc);
			logList.getLogs().add(logData);
		}
		return logList;
	}

}
