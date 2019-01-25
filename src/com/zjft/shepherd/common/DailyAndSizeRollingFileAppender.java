package com.zjft.shepherd.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;

/**
 * DailyAndSizeRollingFileAppender
 * 
 * @author huangyong;
 */
public class DailyAndSizeRollingFileAppender extends FileAppender
{

	// The code assumes that the following constants are in a increasing
	// sequence.
	static final int TOP_OF_TROUBLE = -1;
	static final int TOP_OF_MINUTE = 0;
	static final int TOP_OF_HOUR = 1;
	static final int HALF_DAY = 2;
	static final int TOP_OF_DAY = 3;
	static final int TOP_OF_WEEK = 4;
	static final int TOP_OF_MONTH = 5;
	static final LinkedList<String> jobQueue = new LinkedList<String>();

	int type = -1;
	protected long maxFileSize = 10 * 1024 * 1024;
	protected boolean pkgLog = false;
	protected int maxTimeNum = -1;
	protected int maxFileNum = -1;

	/**
	 * The date pattern. By default, the pattern is set to "'.'yyyy-MM-dd"
	 * meaning daily rollover.
	 */
	private String datePattern = "'.'yyyy-MM-dd";

	Date now = new Date();

	SimpleDateFormat sdf;

	RollingCalendar rc = new RollingCalendar();
	private long nextCheck = System.currentTimeMillis() - 1;

	int checkPeriod = TOP_OF_TROUBLE;

	// The gmtTimeZone is used only in computeCheckPeriod() method.
	static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");

	private String scheduledFilename;

	public DailyAndSizeRollingFileAppender()
	{
	}

	public DailyAndSizeRollingFileAppender(Layout layout, String filename, String datePattern) throws IOException
	{
		super(layout, filename, true);
		this.datePattern = datePattern;
		activateOptions();
	}

	public long getMaximumFileSize()
	{
		return maxFileSize;
	}

	public void setMaxFileSize(String value)
	{
		maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
	}

	public void setMaximumFileSize(long maxFileSize)
	{
		this.maxFileSize = maxFileSize;
	}

	public void setDatePattern(String pattern)
	{
		datePattern = pattern;
	}

	public String getDatePattern()
	{
		return datePattern;
	}

	public boolean isPkgLog()
	{
		return pkgLog;
	}

	public void setPkgLog(boolean pkgLog)
	{
		this.pkgLog = pkgLog;
	}

	public int getMaxTimeNum()
	{
		return maxTimeNum;
	}

	public void setMaxTimeNum(int maxTimeNum)
	{
		this.maxTimeNum = maxTimeNum;
	}

	public int getMaxFileNum()
	{
		return maxFileNum;
	}

	public void setMaxFileNum(int maxFileNum)
	{
		this.maxFileNum = maxFileNum;
	}

	protected void setQWForFiles(Writer writer)
	{
		this.qw = new CountingQuietWriter(writer, errorHandler);
	}

	public void activateOptions()
	{
		super.activateOptions();
		if (datePattern != null && fileName != null)
		{
			now.setTime(System.currentTimeMillis());
			sdf = new SimpleDateFormat(datePattern);
			type = computeCheckPeriod();
			printPeriodicity(type);
			rc.setType(type);
			nextCheck = rc.getNextCheckMillis(now);
			File file = new File(fileName);
			scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));
			if (isLastCheck(file.lastModified()))
			{
				this.rollOver();
			}
		} else
		{
			LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
		}
	}

	public boolean isLastCheck(long lastTime)
	{
		GregorianCalendar nowTime = new GregorianCalendar();
		switch (type)
		{
		case TOP_OF_MINUTE:
			nowTime.set(Calendar.SECOND, 0);
			nowTime.set(Calendar.MILLISECOND, 0);
			nowTime.add(Calendar.MINUTE, 1);
			break;
		case TOP_OF_HOUR:
			nowTime.set(Calendar.MINUTE, 0);
			nowTime.set(Calendar.SECOND, 0);
			nowTime.set(Calendar.MILLISECOND, 0);
			nowTime.add(Calendar.HOUR_OF_DAY, 1);
			break;
		case TOP_OF_DAY:
			nowTime.set(Calendar.HOUR_OF_DAY, 0);
			nowTime.set(Calendar.MINUTE, 0);
			nowTime.set(Calendar.SECOND, 0);
			nowTime.set(Calendar.MILLISECOND, 0);
			nowTime.add(Calendar.DATE, 1);
			break;
		case TOP_OF_MONTH:
			nowTime.set(Calendar.DATE, 1);
			nowTime.set(Calendar.HOUR_OF_DAY, 0);
			nowTime.set(Calendar.MINUTE, 0);
			nowTime.set(Calendar.SECOND, 0);
			nowTime.set(Calendar.MILLISECOND, 0);
			nowTime.add(Calendar.MONTH, 1);
			break;
		default:
			throw new IllegalStateException("Unknown periodicity type.");
		}
		if (nowTime.getTime().getTime() > lastTime)
			return true;
		return false;
	}

	void printPeriodicity(int type)
	{
		switch (type)
		{
		case TOP_OF_MINUTE:
			LogLog.debug("Appender [" + name + "] to be rolled every minute.");
			break;
		case TOP_OF_HOUR:
			LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");
			break;
		case HALF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");
			break;
		case TOP_OF_DAY:
			LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
			break;
		case TOP_OF_WEEK:
			LogLog.debug("Appender [" + name + "] to be rolled at start of week.");
			break;
		case TOP_OF_MONTH:
			LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");
			break;
		default:
			LogLog.warn("Unknown periodicity for appender [" + name + "].");
		}
	}

	/**
	 * This method computes the roll over period by looping over the periods,
	 * starting with the shortest, and stopping when the r0 is different from
	 * from r1, where r0 is the epoch formatted according the datePattern
	 * (supplied by the user) and r1 is the epoch+nextMillis(i) formatted
	 * according to datePattern. All date formatting is done in GMT and not
	 * local format because the test logic is based on comparisons relative to
	 * 1970-01-01 00:00:00 GMT (the epoch).
	 * 
	 * @return
	 */
	int computeCheckPeriod()
	{
		RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.ENGLISH);
		// set sate to 1970-01-01 00:00:00 GMT
		Date epoch = new Date(0);
		if (datePattern != null)
		{
			for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++)
			{
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
				simpleDateFormat.setTimeZone(gmtTimeZone); // do all date
															// formatting in GMT
				String r0 = simpleDateFormat.format(epoch);
				rollingCalendar.setType(i);
				Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
				String r1 = simpleDateFormat.format(next);
				if (r0 != null && r1 != null && !r0.equals(r1))
				{
					return i;
				}
			}
		}
		return TOP_OF_TROUBLE; // Deliberately head for trouble...
	}

	/**
	 * Rollover the current file to a new file.
	 */
	public void rollOver()
	{

		if (this.getDatePattern() == null)
		{
			errorHandler.error("Missing DatePattern option in rollOver().");
			return;
		}
		String datedFilename = fileName + sdf.format(now);
		// It is too early to roll over because we are still within the
		// bounds of the current interval. Rollover will occur once the
		// next interval is reached.
		if (scheduledFilename.equals(datedFilename) && ((CountingQuietWriter) qw).getCount() < maxFileSize)
		{
			return;
		}

		File target;
		File file;

		if (qw != null)
		{
			LogLog.debug("rolling over count=" + ((CountingQuietWriter) qw).getCount());
		}

		int i = 1;
		for (i = 1;; i++)
		{
			file = new File(scheduledFilename + "." + i);
			if (file.exists())
				continue;
			else
				break;
		}

		// if file numbers greater than maxFileNum,then delete the first file of
		// current time interval and build new log file in last place
		if (this.getMaxFileNum() > 0 && i > this.getMaxFileNum())
		{
			i = this.getMaxFileNum();
			new File(scheduledFilename + ".1").delete();
			// take a step forward of the log files
			for (int n = 1; n < this.getMaxFileNum(); n++)
			{
				file = new File(scheduledFilename + "." + (n + 1));
				if (file.exists())
				{
					target = new File(scheduledFilename + "." + n);
					LogLog.debug("Renaming file " + file + " to " + target);
					file.renameTo(target);
				}
			}
		}

		target = new File(scheduledFilename + "." + i);

		this.closeFile();

		file = new File(fileName);
		LogLog.debug("Renaming file " + file + " to " + target);
		file.renameTo(target);

		try
		{
			// This will also close the file. This is OK since multiple
			// close operations are safe.
			this.setFile(fileName, false, bufferedIO, bufferSize);
		} catch (IOException e)
		{
			LogLog.error("setFile(" + fileName + ", false) call failed.", e);
		}

		if (this.isPkgLog())
		{
			final String path = getDirByPath(fileName);
			File dir = new File(path);
			if (dir.exists())
			{
				final File[] files = dir.listFiles();
				final String currentTime = sdf.format(now);
				new Thread()
				{
					public void run()
					{
						super.run();
						Map<String, List<String>> logMap = filterFiles(files);
						for (Entry<String, List<String>> entry : logMap.entrySet())
						{
							String key = entry.getKey();
							List<String> value = entry.getValue();
							// if key time is latter than current time , not
							// handle
							if (key.compareTo(currentTime) > -1)
								continue;

							if (jobQueue.contains(key))
								continue;

							if (value != null && value.size() > 0)
							{
								jobQueue.add(key);
								zipLogs(path, key, value);
								jobQueue.remove(key);
							}
						}
					}
				}.start();
			}

		}

		// if maxTimeNum is not equal '-1',means it needs to delete earlier zip
		// files
		if (!"-1".equals(maxTimeNum))
		{
			String earlist = getEarlistTime();
			if (earlist != null)
			{
				String path = getDirByPath(fileName);
				File dir = new File(path);
				if (dir.exists())
				{
					File[] files = dir.listFiles();
					for (File f : files)
					{
						String logName = fileName + f.getName();
						File tempFile = new File(logName);
						logName = tempFile.getAbsolutePath();
						if (isZip(logName))
						{
							String zipDate = matchTime(logName);
							// zip file name contains the time before the
							// earlist time , then delete the zip file
							if (!"-1".equals(zipDate) && earlist.compareTo(zipDate) > 0)
							{
								f.delete();
							}
						}
					}
				}
			}
		}
		scheduledFilename = datedFilename;
	}

	public synchronized void setFile(String file, boolean append, boolean bufferedIO, int bufferSize) throws IOException
	{
		super.setFile(file, append, this.bufferedIO, this.bufferSize);
		if (append)
		{
			File f = new File(file);
			((CountingQuietWriter) qw).setCount(f.length());
		}
	}

	/**
	 * <p>
	 * Before actually logging, this method will check whether it is time to do
	 * a rollover. If it is, it will schedule the next rollover time and then
	 * rollover.
	 * */
	protected void subAppend(LoggingEvent event)
	{
		long n = System.currentTimeMillis();
		if (n >= nextCheck)
		{
			now.setTime(n);
			nextCheck = rc.getNextCheckMillis(now);
			this.rollOver();
		} else if ((fileName != null) && ((CountingQuietWriter) qw).getCount() >= maxFileSize)
		{
			this.rollOver();
		}
		super.subAppend(event);
	}

	/**
	 * package files to a zip file name by time
	 * 
	 * @param dir
	 * @param date
	 * @param fileNames
	 */
	private void zipLogs(String dir, String date, List<String> fileNames)
	{
		int i = 1;
		File zipFile = null;
		// use date to name a zip file ,if zip file is existed before,add
		// parentheses with number behind,until find a new name.
		while (true)
		{
			zipFile = new File(dir + File.separator + date + (i == 1 ? "" : "(" + i + ")") + ".zip");
			if (zipFile.exists())
				i++;
			else
			{
				break;
			}
		}

		// zip log files
		if (zipFiles(zipFile.getAbsolutePath(), fileNames))
		{
			// if zip success,delete the original log file
			for (String path : fileNames)
				new File(path).delete();
		}
	}

	/**
	 * filter and group log files,group all log files in the direction by time,
	 * filter dirs,zips,files without time in it's name
	 * 
	 * @param files
	 *            the file list in the dir
	 * @return a map use time as key and log list as value
	 */
	private Map<String, List<String>> filterFiles(File[] files)
	{
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isDirectory())
				continue;

			String logPath = files[i].getAbsolutePath();
			String time = matchTime(logPath);
			if (!isZip(logPath) && !"-1".equals(time))
			{
				List<String> logList = result.get(time);
				if (logList == null)
				{
					logList = new ArrayList<String>();
					logList.add(logPath);
					result.put(time, logList);
				} else
				{
					logList.add(logPath);
					result.put(time, logList);
				}
			}
		}
		return result;
	}

	/**
	 * get the earlist time
	 * 
	 * @return
	 */
	public String getEarlistTime()
	{
		String earlist = null;
		Calendar date = Calendar.getInstance();
		if (type == TOP_OF_MINUTE)
		{
			date.add(Calendar.MINUTE, -maxTimeNum);
		} else if (type == TOP_OF_HOUR)
		{
			date.add(Calendar.HOUR_OF_DAY, -maxTimeNum);
		} else if (type == HALF_DAY)
		{
			date.add(Calendar.DATE, -maxTimeNum);
		} else if (type == TOP_OF_DAY)
		{
			date.add(Calendar.DAY_OF_MONTH, -maxTimeNum);
		} else if (type == TOP_OF_WEEK)
		{
			date.add(Calendar.WEEK_OF_YEAR, -maxTimeNum);
		} else if (type == TOP_OF_MONTH)
		{
			date.add(Calendar.MONTH, -maxTimeNum);
		} else
		{
			return null;
		}
		earlist = sdf.format(date.getTime()).toString();
		return earlist;
	}

	/**
	 * match the time in filepath,if there is no match return -1,else return the
	 * match
	 * 
	 * @param regEx
	 * @param content
	 * @return
	 */
	private String matchTime(String content)
	{
		try
		{
			File file = new File(fileName);
			String path = file.getAbsolutePath();
			content = new File(content).getAbsolutePath();
			int start = content.indexOf(path);
			int end = content.lastIndexOf(".");
			if (start > -1 && end > -1 && start < end)
			{
				String result = content.substring(start + path.length(), end);
				result = result.replace("\\", "");
				result = result.replace("/", "");
				return result;
			}
			return "-1";
		} catch (Exception e)
		{
			LogLog.warn("get time error:[filepath:" + content + "][fileName:" + fileName + "]\r\n" + e);
			return "-1";
		}
	}

	private boolean isZip(String fileName)
	{
		if (fileName.toLowerCase().lastIndexOf(".zip") > 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * zip some file to a zip file
	 * 
	 * @param zipPath
	 * @param files
	 * @return
	 */
	public boolean zipFiles(String zipPath, List<String> files)
	{
		try
		{
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipPath));

			byte b[] = new byte[1024];
			for (int i = 0; i < files.size(); i++)
			{
				out.putNextEntry(new ZipEntry(getNameByPath(files.get(i))));
				FileInputStream in = new FileInputStream(new File(files.get(i)));
				int len = 0;
				while ((len = in.read(b)) != -1)
				{
					out.write(b, 0, len);
				}
				in.close();
				out.closeEntry();
			}
			out.close();
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	public String getNameByPath(String path)
	{
		int index = path.lastIndexOf(File.separator);
		if (index > 0)
			return path.substring(index + 1, path.length());
		else
			return path;
	}

	public static String getDirByPath(String path)
	{
		int index1 = path.lastIndexOf("\\");
		int index2 = path.lastIndexOf("/");
		int index = index1 > index2 ? index1 : index2;
		if (index > 0)
			return path.substring(0, index);
		else
			return path;
	}
}

/**
 * RollingCalendar is a helper class to DailyRollingFileAppender. Given a
 * periodicity type and the current time, it computes the start of the next
 * interval.
 * */
@SuppressWarnings("serial")
class RollingCalendar extends GregorianCalendar
{

	int type = DailyAndSizeRollingFileAppender.TOP_OF_TROUBLE;

	RollingCalendar()
	{
		super();
	}

	RollingCalendar(TimeZone tz, Locale locale)
	{
		super(tz, locale);
	}

	void setType(int type)
	{
		this.type = type;
	}

	public long getNextCheckMillis(Date now)
	{
		return getNextCheckDate(now).getTime();
	}

	public Date getNextCheckDate(Date now)
	{
		this.setTime(now);

		switch (type)
		{
		case DailyAndSizeRollingFileAppender.TOP_OF_MINUTE:
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MINUTE, 1);
			break;
		case DailyAndSizeRollingFileAppender.TOP_OF_HOUR:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.HOUR_OF_DAY, 1);
			break;
		case DailyAndSizeRollingFileAppender.HALF_DAY:
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			int hour = get(Calendar.HOUR_OF_DAY);
			if (hour < 12)
			{
				this.set(Calendar.HOUR_OF_DAY, 12);
			} else
			{
				this.set(Calendar.HOUR_OF_DAY, 0);
				this.add(Calendar.DAY_OF_MONTH, 1);
			}
			break;
		case DailyAndSizeRollingFileAppender.TOP_OF_DAY:
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.DATE, 1);
			break;
		case DailyAndSizeRollingFileAppender.TOP_OF_WEEK:
			this.set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek());
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.WEEK_OF_YEAR, 1);
			break;
		case DailyAndSizeRollingFileAppender.TOP_OF_MONTH:
			this.set(Calendar.DATE, 1);
			this.set(Calendar.HOUR_OF_DAY, 0);
			this.set(Calendar.MINUTE, 0);
			this.set(Calendar.SECOND, 0);
			this.set(Calendar.MILLISECOND, 0);
			this.add(Calendar.MONTH, 1);
			break;
		default:
			throw new IllegalStateException("Unknown periodicity type.");
		}
		return getTime();
	}
}
