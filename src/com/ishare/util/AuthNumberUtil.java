package com.ishare.util;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ishare.integration.text.SendAuthSMS;


public class AuthNumberUtil implements Nightly {

	public final static Logger logger = LoggerFactory
			.getLogger(AuthNumberUtil.class);

	public static Map<String, List<AuthNumberEntry>> authNumberEntrysMap = new HashMap<String, List<AuthNumberEntry>>();

	public static final int authNumberLength = 6;

	public static final long validPeriod = 1000 * 60 * 30;

	public static final int MaxAllowedPerDay = 7;

	private static boolean validateUsername(List<AuthNumberEntry> matchedEntrys) {
		if (matchedEntrys.size() <= MaxAllowedPerDay) {
			return true;
		}
		// int todayCount = 0;
		// Date today = new Date();
		// for (AuthNumberEntry entry : matchedEntrys) {
		// Date entryDate = entry.getStartTime();
		// if (today.getYear() == entryDate.getYear()
		// && today.getMonth() == entryDate.getMonth()
		// && today.getDay() == entryDate.getDay()) {
		// todayCount++;
		// }
		// }
		// if (todayCount > MaxAllowedPerDay) {
		// return false;
		// }
		return false;
	}

	public static int saveAuthNumber(String username, String authNumber) {
		// construct entry
		AuthNumberEntry entry = new AuthNumberEntry();
		entry.setUsername(username);
		entry.setAuthNumber(authNumber);
		entry.setStartTime(new Date());
		// add entry to map
		List<AuthNumberEntry> matchedEntrys = authNumberEntrysMap.get(username);
		if (matchedEntrys == null) {
			matchedEntrys = new LinkedList<AuthNumberEntry>();
			matchedEntrys.add(entry);
			authNumberEntrysMap.put(username, matchedEntrys);
		} else {
			if (!validateUsername(matchedEntrys)) {
				return MessageUtil.SEND_AUTH_NUMBER_EXCEED_MAX_ALLOWED_TODAY;
			}
			matchedEntrys.add(entry);
		}
		logger.info("username[%s] authNumber[%s] saved", username, authNumber);
		return MessageUtil.SUCCESS;
	}

	public static boolean auth(String username, String authNumber, Date date) {
		// construct temEntry
		AuthNumberEntry temEntry = new AuthNumberEntry();
		temEntry.setUsername(username);
		temEntry.setAuthNumber(authNumber);
		// ...
		List<AuthNumberEntry> matchedEntrys = authNumberEntrysMap.get(username);
		if (matchedEntrys == null || !matchedEntrys.contains(temEntry)) {
			return false;
		}
		for (AuthNumberEntry entry : matchedEntrys) {
			if (entry.equals(temEntry)) {
				Date startTime = entry.getStartTime();
				Date validTime = new Date(startTime.getTime() + validPeriod);
				if (date.after(validTime)) {
					return false;
				} else {
					// clear list
					matchedEntrys.clear();
					return true;
				}
			}
		}
		return false;
	}

	// generate random auth number
	public static String generateAuthNumber(int length) {
		String target = "";
		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			target += String.valueOf(rand.nextInt(10));
		}
		return target;
	}

	public static void sendAuthNumberToUser(String username, String authNumber) {
		new SendAuthSMS(username, authNumber);
	}

	private static class AuthNumberEntry {

		private String username;

		private String authNumber;

		private Date startTime;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getAuthNumber() {
			return authNumber;
		}

		public void setAuthNumber(String authNumber) {
			this.authNumber = authNumber;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((authNumber == null) ? 0 : authNumber.hashCode());
			result = prime * result
					+ ((username == null) ? 0 : username.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AuthNumberEntry other = (AuthNumberEntry) obj;
			if (authNumber == null) {
				if (other.authNumber != null)
					return false;
			} else if (!authNumber.equals(other.authNumber))
				return false;
			if (username == null) {
				if (other.username != null)
					return false;
			} else if (!username.equals(other.username))
				return false;
			return true;
		}
	}

	@Override
	public void nigthlyDo() {
		logger.info(String.format("clearing authNumberEntrysMap with size[%s]",
				AuthNumberUtil.authNumberEntrysMap.size()));
		logger.info(String.format("authNumberEntrysMap as json:%s",
				TransformerUtil.ObjectToJson(authNumberEntrysMap)));
		AuthNumberUtil.authNumberEntrysMap.clear();
		logger.info(String.format("authNumberEntrysMap cleared with size[%s]",
				AuthNumberUtil.authNumberEntrysMap.size()));
	}
}
