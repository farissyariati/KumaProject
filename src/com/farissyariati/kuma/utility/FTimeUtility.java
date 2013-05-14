package com.farissyariati.kuma.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FTimeUtility {
	private Calendar calendar;
	private long millis;

	public FTimeUtility() {
		this.calendar = Calendar.getInstance();
	}

	public FTimeUtility(long unixTime) {
		this.calendar = Calendar.getInstance();
		this.millis = unixTimeToMilis(unixTime);
		calendar.setTimeInMillis(millis);
	}

	private long unixTimeToMilis(long unixTime) {
		return unixTime * 1000;
	}

	public String collabtiveDateFormat(long milis, String format) {
		milis = unixTimeToMilis(milis);
		DateFormat formatter = new SimpleDateFormat(format);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milis);
		return formatter.format(calendar.getTime()).toString();
	}

	public long getUnixTimestamp(int day, int month, int year) {
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return (long) (calendar.getTimeInMillis() / 1000);
	}

	public long getMilisFromDate(int day, int month, int year) {
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.YEAR, year);
		return (long) (calendar.getTimeInMillis());
	}

	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getMonth() {
		return calendar.get(Calendar.MONTH);
	}

	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	public int getDayFromMillis() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getMonthFromMillis() {
		return calendar.get(Calendar.MONTH);
	}

	public int getYearFromMillis() {
		return calendar.get(Calendar.YEAR);
	}

	public boolean overdue(long dueTime, long now) {
		if (now > dueTime)
			return true;
		else
			return false;
	}

	public boolean onTimeRange(long controlEndTime, long activityEndTime) {
		if (activityEndTime < controlEndTime)
			return true;
		else
			return false;
	}

	public long getDayDiff(long now, long endProject) {
		if(!overdue(endProject, now)){
			Calendar calNow = Calendar.getInstance();
			calNow.setTimeInMillis(now);
			Calendar calEndProject = Calendar.getInstance();
			calEndProject.setTimeInMillis(endProject);
			Calendar date = (Calendar) calNow.clone();
			long daysBetween = 0;
			while (date.before(calEndProject)) {
				date.add(Calendar.DAY_OF_MONTH, 1);
				daysBetween++;
			}
			return daysBetween + 1;
		}else{
			long diff = endProject - now;
			return (diff / (24 * 60 * 60 * 1000))+1;
		}
		
		
	}

}
