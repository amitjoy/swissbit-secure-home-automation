/*******************************************************************************
 * Copyright 2015 Amit Kumar Mondal <admin@amitinside.com>, Subburam <subburam.rb@gmail.com>, Gaurav Srivastava <gaurav.srivastava7@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
