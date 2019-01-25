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
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��yyyy-mm-dd hh:mm:ss
	 */
	
	public static String getSysTimeYMDHMS(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
	}
	/**
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��MM-dd HH:mm:ss SSS
	 * case_traceʹ��
	 */
	public static String getSysTimeYMDHMS11(){
		return new SimpleDateFormat("MM-dd HH:mm:ss SSS").format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��MM-dd HH:mm:ss SSS
	 * case_traceʹ��
	 */
	public static String getSysTimeYMDHMS111(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(System.currentTimeMillis()));
	}
	
	/**
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��yyyyMMddHHmmss
	 */
	
	public static String getSysTimeYMDHMS1(){
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
	}
		
	/**
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��yyyy-mm-dd hh:mm
	 */
	
	public static String getSysTimeYMDHM(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(System.currentTimeMillis()));
	}

	
	/**
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��yyyy-mm-dd
	 */
	
	public static String getSysTimeYMD(){
		return new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
	}  
	
	/**
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��yyyy-mm
	 */
	
	public static String getSysTimeYM(){
		return new SimpleDateFormat("yyyy-MM").format(new Date(System.currentTimeMillis()));
	} 
	
	/**
	 * ��ȡϵͳʱ��
	 * @return ����ϵͳ��ǰʱ���ַ������ַ�����ʽΪ��hh:mm:ss
	 */
	
	public static String getSysTimeHMS(){
		return new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis()));
	} 
       
	/**ȡϵͳʱ��hhmmss
	 * @return String--hhmmss��ʽ��ʱ���ַ���
	 */
	
	public static String getTime(){
		return new SimpleDateFormat("HHmmss").format(new Date().getTime());
	}
	
	/**ȡϵͳ����yyyymmdd
	 * @return String--yyyymmdd��ʽ�������ַ���
	 */
	
	public static String getDate(){
		return new SimpleDateFormat("yyyyMMdd").format(new Date().getTime());
	}
	
	/**
	 * ���ݴ����ʱ��תΪYYYYMMDDHHMMSS��ʽ���ַ���
	 * @param time ����ʱ��ĳ�����
	 * @return ����ʱ����ַ���
	 */
	
	public static String getDateTime(long time){		
		return new SimpleDateFormat("yyyyMMddHHmmss").format(time);
	}
	
	/**
	 * ���ݴ����ʱ��תΪYYYY-MM-DD HH:MM:SS��ʽ���ַ���
	 * @param time ����ʱ��ĳ�����
	 * @return ����ʱ����ַ���
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
	
	/**���ַ���yyyy-MM-DDת��Date��������
	 * @since 2006.01.19
	 * @param stringdate ��yyyyMMdd��ʾ�������ַ���
	 * @return date��������
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
			log.info("���ڸ�ʽ��");
			//e.printStackTrace();
			return null;		
		}
	}
	/**ת�� date������Ϊ��Ҫ��ʽ
	 * @since 2007.09.19
	 * @param dateTime date��������
	 * @return "MM-dd HH:mm"�ַ�������
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
			log.info("ת�����ڴ�");
			return null;		
		}
	}
	/**ת�� date������Ϊ��Ҫ��ʽ
	 * @since 2007.09.19
	 * @param dateTime date��������
	 * @return "yyyy-MM-dd HH:mm"�ַ�������
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
			log.info("ת�����ڴ�");
			return null;		
		}
	}
	
	/**ת�� date������Ϊ��Ҫ��ʽ
	 * @since 2007.09.19
	 * @param dateTime date��������
	 * @return "yyyy-MM-dd HH:mm:ss"�ַ�������
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
			log.info("ת�����ڴ�");
			return null;		
		}
	}
	/**
	 * ����Ԥ����Ӧʱ��
	 * @param openTime  ��ʼʱ���
	 * @param interval ʱ����
	 * @param respondGrade  ��Ӧ����(1-����ʱ����Ӧ,2-7x24Сʱ��Ӧ)
	 * @return Ԥ����Ӧʱ��
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
	 * ����Ԥ����Ӧʱ��
	 * @param interval ʱ����
	 * @param respondGrade  ��Ӧ����(1-����ʱ����Ӧ,2-7x24Сʱ��Ӧ)
	 * @return Ԥ����Ӧʱ��
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
	 * ��ȡ��ǰ����ǰһ������ڣ����ڸ�ʽyyyy-mm-dd
	 * @return ��ǰ����ǰһ������ڣ����ڸ�ʽyyyy-MM-dd
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
	 * ��ȡ��ָ�����������i�µ�����,���ڸ�ʽΪ"yyyy-MM-dd"
	 * @param fixDate ָ�������ڣ���ʽΪ"yyyy-MM-dd"��
	 * @param i ���������iΪ������
	 * @return ����������
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
	 * ��ȡ��ָ�����������i�������,���ڸ�ʽΪ"yyyy-MM-dd"
	 * @param fixDate ָ�������ڣ���ʽΪ"yyyy-MM-dd"��
	 * @param i ���������iΪ������
	 * @return ����������
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
	 * ��ȡ��ָ�����������i�������,���ڸ�ʽΪ"yyyy-MM-dd"
	 * @param fixDate ָ�������ڣ���ʽΪ"yyyy-MM-dd"��
	 * @param i ���������iΪ������
	 * @return ����������
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
	 * ��ȡǰһ����ǰ���ڣ����ڸ�ʽyyyy-mm
	 * @return ǰһ����ǰ���ڣ����ڸ�ʽyyyy-MM
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
	 * ��ȡ��ǰ�����������������ӣ�����ʷ����
	 * @param minute ����
	 * @return ��ʷ���ڣ����ڸ�ʽyyyyMMddHHmmss
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
	 * ��ȡ��ǰ��Ϊ�ܼ�
	 * @return ��ǰΪ�ܼ�
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
	 * ��ȡʱ���
	 * @return ʱ���
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
		
		String difofTime =mark + days + "��" + hours + "Сʱ" + minitues +"��";
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