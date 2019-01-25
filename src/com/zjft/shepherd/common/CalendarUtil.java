package com.zjft.shepherd.common;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author shp
 * @since 2005.12.12
 */


public final class CalendarUtil {

	private static Log log = LogFactory.getLog(CalendarUtil.class);
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：yyyy-mm-dd hh:mm:ss
	 */
	
	public static String getSysTimeYMDHMS(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
	}
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：MM-dd HH:mm:ss SSS
	 * case_trace使用
	 */
	public static String getSysTimeYMDHMS11(){
		return new SimpleDateFormat("MM-dd HH:mm:ss SSS").format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：MM-dd HH:mm:ss SSS
	 * case_trace使用
	 */
	public static String getSysTimeYMDHMS111(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：yyyyMMddHHmmss
	 */
	
	public static String getSysTimeYMDHMS1(){
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
	}
		
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：yyyy-mm-dd hh:mm
	 */
	
	public static String getSysTimeYMDHM(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis()));
	}

	
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：yyyy-mm-dd
	 */
	
	public static String getSysTimeYMD(){
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
	}  
	
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：yyyy-mm
	 */
	
	public static String getSysTimeYM(){
		return new SimpleDateFormat("yyyy-MM").format(new Date(System.currentTimeMillis()));
	} 
	
	/**
	 * 获取系统时间
	 * @return 返回系统当前时间字符串，字符串格式为：hh:mm:ss
	 */
	
	public static String getSysTimeHMS(){
		return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
	} 
       
	/**取系统时间hhmmss
	 * @return String--hhmmss格式的时间字符串
	 */
	
	public static String getTime(){
		return new SimpleDateFormat("HHmmss").format(new Date().getTime());
	}
	
	/**取系统日期yyyymmdd
	 * @return String--yyyymmdd格式的日期字符串
	 */
	
	public static String getDate(){
		return new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
	}
	
	/**
	 * 根据传入的时间转为YYYYMMDDHHMMSS格式的字符串
	 * @param time 代表时间的长整数
	 * @return 代表时间的字符串
	 */
	
	public static String getDateTime(long time){		
		return new SimpleDateFormat("yyyyMMddHHmmss").format(time);
	}
	
	/**
	 * 根据传入的时间转为YYYY-MM-DD HH:MM:SS格式的字符串
	 * @param time 代表时间的长整数
	 * @return 代表时间的字符串
	 */
	public static String getYMDHMS(long time)
	{
		Calendar cal=Calendar.getInstance();
		cal.setTimeInMillis(time);
		int yearFile=cal.get(Calendar.YEAR);
		int monthFile=cal.get(Calendar.MONTH)+1;
		int dayFile=cal.get(Calendar.DATE);
		int hourFile=cal.get(Calendar.HOUR_OF_DAY);
		int minuteFile=cal.get(Calendar.MINUTE);
		int secondFile=cal.get(Calendar.SECOND);
		String ss=String.format("%04d",yearFile)+"-"
					+String.format("%02d",monthFile)+"-"+
					String.format("%02d",dayFile)+" "+
					String.format("%02d",hourFile)+":"+
					String.format("%02d",minuteFile)+":"+
					String.format("%02d",secondFile);
		return ss;
	}
	
	/**将字符型yyyy-MM-DD转成Date类型日期
	 * @since 2006.01.19
	 * @param stringdate 以yyyyMMdd表示的日期字符串
	 * @return date类型日期
	 */
	public static Date str2Date(String stringdate)
	{	
		if(stringdate==null) return null;
		SimpleDateFormat format=null;
		if(stringdate!=null&&stringdate.length()<8) return null;
		
		if(stringdate!=null&&stringdate.length()==8) format = new SimpleDateFormat("yyyyMMdd");
		if(stringdate!=null&&stringdate.length()==10) format = new SimpleDateFormat("yyyy-MM-dd");
		if(stringdate!=null&&stringdate.length()==14) format = new SimpleDateFormat("yyyyMMddHHmmss");
		if(stringdate!=null&&stringdate.length()==19) format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try{
			date = format.parse(stringdate);
			return date;
		}
		catch(Exception e){
			log.info("日期格式错。");
			//e.printStackTrace();
			return null;		
		}
	}
	/**转换 date型数据为简要格式
	 * @since 2007.09.19
	 * @param dateTime date类型日期
	 * @return "MM-dd HH:mm"字符型日期
	 */

	public static String  fmtMDHM(Date dateTime)
	{	
		
		String fmtTime="";
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
			if (dateTime!=null) fmtTime=sdf.format(dateTime);
			return fmtTime;
		}
		catch(Exception e){
			log.info("转换日期错。");
			return null;		
		}
	}
	/**转换 date型数据为简要格式
	 * @since 2007.09.19
	 * @param dateTime date类型日期
	 * @return "yyyy-MM-dd HH:mm"字符型日期
	 */
	public static String  fmtYMDHM(Date dateTime)
	{	
		String fmtTime="";
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			if (dateTime!=null) fmtTime=sdf.format(dateTime);
			return fmtTime;
		}
		catch(Exception e){
			log.info("转换日期错。");
			return null;		
		}
	}
	
	/**转换 date型数据为简要格式
	 * @since 2007.09.19
	 * @param dateTime date类型日期
	 * @return "yyyy-MM-dd HH:mm:ss"字符型日期
	 */
	public static String  fmtYMDHMS(Date dateTime)
	{	
		String fmtTime="";
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (dateTime!=null) fmtTime=sdf.format(dateTime);
			return fmtTime;
		}
		catch(Exception e){
			log.info("转换日期错。");
			return null;		
		}
	}
	/**
	 * 计算预期响应时间
	 * @param openTime  开始时间点
	 * @param interval 时间间隔
	 * @param respondGrade  响应级别(1-工作时间相应,2-7x24小时响应)
	 * @return 预期响应时间
	 */
	public static Date getPreWorkTime(Date openTime,int interval,int respondGrade)
	{	
		
		
		try{
			Date preExpireTime=new Date(openTime.getTime()+interval*60000);
			return preExpireTime;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;		
		}
	}
	/**
	 * 计算预期响应时间
	 * @param interval 时间间隔
	 * @param respondGrade  响应级别(1-工作时间相应,2-7x24小时响应)
	 * @return 预期响应时间
	 */
	public static Date getPreWorkTime2(int interval,int respondGrade)
	{	
		try{
			Calendar temp=Calendar.getInstance();
			temp.add(Calendar.MINUTE, interval);
			return temp.getTime();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;		
		}
	}
	/**
	 * 获取当前日期前一天的日期，日期格式yyyy-mm-dd
	 * @return 当前日期前一天的日期，日期格式yyyy-MM-dd
	 */
	public static String getPreviousDate()
	{
		Calendar date = Calendar.getInstance();	
		date.add(Calendar.DAY_OF_MONTH,-1);						
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate = formatter.format(date.getTime()).toString();		
		return sysDate;
	}
	
	/**
	 * 获取与指定的日期相差i月的日期,日期格式为"yyyy-MM-dd"
	 * @param fixDate 指定的日期（格式为"yyyy-MM-dd"）
	 * @param i 相隔月数（i为整数）
	 * @return 计算后的日期
	 */
	public static String getFixedMonth(String fixDate,int i)
	{		
		int year=Integer.parseInt(fixDate.substring(0, 4));
		int month=Integer.parseInt(fixDate.substring(5, 7));
		int day=Integer.parseInt(fixDate.substring(8, 10));
		
		Calendar date = Calendar.getInstance();	
		date.set(year,month-1,day);
		date.add(Calendar.MONTH,i);						
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate = formatter.format(date.getTime()).toString();		
		return sysDate;
	}
	
	/**
	 * 获取与指定的日期相差i天的日期,日期格式为"yyyy-MM-dd"
	 * @param fixDate 指定的日期（格式为"yyyy-MM-dd"）
	 * @param i 相隔天数（i为整数）
	 * @return 计算后的日期
	 */
	public static String getFixedDate(String fixDate,int i)
	{		
		int year=Integer.parseInt(fixDate.substring(0, 4));
		int month=Integer.parseInt(fixDate.substring(5, 7));
		int day=Integer.parseInt(fixDate.substring(8, 10));
		
		Calendar date = Calendar.getInstance();	
		date.set(year,month-1,day);
		date.add(Calendar.DAY_OF_MONTH,i);						
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String sysDate = formatter.format(date.getTime()).toString();		
		return sysDate;
	}

	/**
	 * 获取与指定的日期相差i天的日期,日期格式为"yyyy-MM-dd"
	 * @param fixDate 指定的日期（格式为"yyyy-MM-dd"）
	 * @param i 相隔天数（i为整数）
	 * @return 计算后的日期
	 */
	public static String getFixedDateYYYYMMDD(String fixDate,int i)
	{		
		int year=Integer.parseInt(fixDate.substring(0, 4));
		int month=Integer.parseInt(fixDate.substring(5, 7));
		int day=Integer.parseInt(fixDate.substring(8, 10));
		
		Calendar date = Calendar.getInstance();	
		date.set(year,month-1,day);
		date.add(Calendar.DAY_OF_MONTH,i);						
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String sysDate = formatter.format(date.getTime()).toString();		
		return sysDate;
	}
	
	/**
	 * 获取前一个月前日期，日期格式yyyy-mm
	 * @return 前一个月前日期，日期格式yyyy-MM
	 */
	public static String getPreviousMonth()
	{
		Calendar date = Calendar.getInstance();	
		date.add(Calendar.MONTH,-1);						
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		String sysDate = formatter.format(date.getTime()).toString();		
		return sysDate;
	}
	
	/**
	 * 获取当前日期与参数间隔（分钟）的历史日期
	 * @param minute 分钟
	 * @return 历史日期，日期格式yyyyMMddHHmmss
	 */
	public static String getPreMinuteDateTime(String minute)
	{		
		try
		{			
			int min = Integer.parseInt(minute);
			Calendar date = Calendar.getInstance();				
			date.add(Calendar.MINUTE, -min);			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			String sysDate = formatter.format(date.getTime()).toString();			
			return sysDate;
		}
		catch(Exception e)
		{
			return null;
		}
	}
	/**
	 * 获取当前日为周几
	 * @return 当前为周几
	 * */
	public static String getDayOfWeek()
	{		
		
		Calendar cal = Calendar.getInstance();
		switch(cal.get(Calendar.DAY_OF_WEEK))
		{
			case 2:return "Monday";
			case 3:return "Tuesday";
			case 4:return "Wednesday";
			case 5:return "Thursday";
			case 6:return "Friday";
			case 7:return "Staturday";
			case 1:return "Sunday";				
		}
		return null;
	}
	
	/**
	 * 获取时间差
	 * @return 时间差
	 * */
	public static String getDifOfTime(String time)
	{				
		long diftime = System.currentTimeMillis() - str2Date(time).getTime();
		String mark="";
		if(diftime<0)
		{
			mark="-";
			diftime = Math.abs(diftime);
		}
		long days = (long)Math.floor(diftime/(1000*3600*24));
		long hours = (long)Math.floor((diftime - days*1000*3600*24)/(1000*3600));
		long minitues = (long)Math.floor((diftime - days*1000*3600*24 - hours*1000*3600)/(1000*60));
		
		String difofTime =mark + days + "天" + hours + "小时" + minitues +"分";
		return difofTime;
	}
	public static void main(String[] args)
	{	
		
		//CalendarUtil.getPreMinuteDateTime("0");
		System.out.println(getDifOfTime("20000419164044"));
		//String previousDate = getPreviousMonth();
		//System.out.println("previousDate = "+previousDate);
	}	

}