/*
 * Decompiled with CFR 0.138.
 * 
 * Could not load the following classes:
 *  com.unace.server.UtilClass
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.bouncycastle.util.encoders.Base64
 *  org.slf4j.Logger
 */
package com.aroasoft.core.tcpserver.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.bouncycastle.util.encoders.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/*
 * Exception performing whole class analysis ignored.
 */
public class UtilClass {
	private static final Logger logger = LoggerFactory.getLogger(UtilClass.class);
	
    public UtilClass() {
    }
    
    public static String exceptionToString(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionAsString = "";
        if (sw.toString().length()>500)
        	exceptionAsString = sw.toString().substring(0,500);
        else	
        	exceptionAsString= sw.toString();
        
        try {
        	exceptionAsString = sw.toString();
	        sw.close();
	        pw.close();
	        sw = null;
	        pw = null;
        }catch (Exception ex1) {
        	
        }
        return exceptionAsString;
    }
    
    
    public static String getLongStringToTimeStr(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(time));
        return sdf.format(calendar.getTime());
    }

    public static Date getDateTimeFromTimestamp(Long value) {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Date d = new Date(value * 1000L + (long)TimeZone.getDefault().getRawOffset());
        return d;
    }

    public static String md5(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(source.getBytes("UTF-8"));
            return UtilClass.getString((byte[])bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String SHA256(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(source.getBytes("UTF-8"));
            return UtilClass.getString((byte[])bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String Base64Encode(byte[] bytes)
    {
    	return new String(Base64.encode(bytes));
    }
    public static byte[] Base64Decode(String data)
    {
    	return Base64.decode(data);
    }
    public static String SHA256_Base64(String source) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(source.getBytes("UTF-8"));
            return new String(Base64.encode((byte[])bytes));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            String hex = Integer.toHexString(255 & b);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String generateRandom(int len) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return UtilClass.getString((byte[])bytes);
    }

    public static String generateRandomNum(int len) {
        Random rand = new Random();
        String id = String.format("%04d", rand.nextInt(10000));
        return id;
    }
    public static String generateRandomNum2(int len) {
        Random rand = new Random();
        
        StringBuffer buf = new StringBuffer();
        
        for(int i = 0; i < len; i++){
            buf.append((rand.nextInt(10)));
        }
        
        return buf.toString();
    }

    public static HashMap<String, String> queryToMap(String query)
    {
    	HashMap<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
    
    public static String getNowMilSendFormat()
	{
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		  Date dd = new Date(System.currentTimeMillis());
		  return sdf.format(dd);
	}
    public static String getUUID()
    {
    	return UUID.randomUUID().toString();
    }
    
    public static void sort(File[] filterResult){

		// 파일명으로 정렬한다. 

		Arrays.sort(filterResult, new Comparator() {

			public int compare(Object arg0, Object arg1) {

				File file1 = (File)arg0;
	
				File file2 = (File)arg1;
	
				return file1.getName().compareToIgnoreCase(file2.getName());

			}

		});

	}
    
    public static String numberGen(int len, int dupCd ) {
        
        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수
        
        for(int i=0;i<len;i++) {
            
            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));
            
            if(dupCd==1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            }else if(dupCd==2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if(!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                }else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i-=1;
                }
            }
        }
        return numStr;
    }
    
    public static DecimalFormatSymbols getDecimalFormatSymbols(String lang)
	{
		Locale locale = Locale.KOREAN;
		if (lang!=null && !lang.equals("ko_KR"))
			locale = Locale.ENGLISH;
		return new DecimalFormatSymbols(locale);
	}
    public static HashMap emailmasking(String email)
    {
		String femail = "";
		String domain = "";
		String fdomain = "";
		String mdomain = "";
		String bdomain = "";
		int idx = 0;
		int result = -1;
		
		HashMap map = new HashMap();
		idx = email.indexOf("@");
		femail = email.substring(0, 3);
		femail = femail + "*****";
		
		map.put("femail",femail);
		domain = email.substring(idx + 1);
		StringTokenizer temp = new StringTokenizer(domain, ".");
		int cnt = temp.countTokens();
		if (cnt == 2) {
			// domain => test.com
			result = 1;

			fdomain = temp.nextToken();
			mdomain = temp.nextToken();
			fdomain = fdomain.substring(0,1);
			fdomain = fdomain + "****";
			mdomain = mdomain.substring(mdomain.length() -1,mdomain.length());
			mdomain = "**" + mdomain;
			map.put("fdomain",fdomain);
			map.put("mdomain",mdomain);
			map.put("result",result);
		} else {
			// domain => test.co.kr
			result = 2;

			fdomain = temp.nextToken();
			mdomain = temp.nextToken();
			bdomain = temp.nextToken();
			fdomain = fdomain.substring(0,1);
			fdomain = fdomain + "****";
			mdomain = mdomain.substring(mdomain.length() -1,mdomain.length());
			mdomain = "**" + mdomain;
			bdomain = bdomain.substring(bdomain.length() -1,bdomain.length());
			bdomain = "*" + bdomain;
			map.put("fdomain",fdomain);
			map.put("mdomain",mdomain);
			map.put("bdomain",bdomain);
			map.put("result",result);
		}
		return map;	
    }
    
    public static String convertToUTC(Date date) {
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
	    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	    return dateFormat.format(date);
	}
    public static String convertToLocalNoRequest_original(Object date, String timezone, Logger logger) throws ParseException {
    	DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	//dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
    	String dateInString = String.valueOf(date);
    	Date dateTime = dateFormat1.parse(dateInString);
    	
    	TimeZone tz =TimeZone.getTimeZone(timezone);
    	
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	dateFormat.setTimeZone(tz);
    	
    	String rtnVal = dateFormat.format(dateTime);
    	return rtnVal;
    }
    public static String convertToLocalNoRequest(Object date, String timezone, Logger logger) throws ParseException {
    	/*DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	dateFormat1.setTimeZone(TimeZone.getTimeZone("UTC"));
    	String dateInString = String.valueOf(date);
    	Date dateTime = dateFormat1.parse(dateInString);
    	
    	TimeZone tz =TimeZone.getTimeZone(timezone);
    	
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	dateFormat.setTimeZone(tz);*/
    	
    	String rtnVal = String.valueOf(date);
    	return rtnVal;
    }

    public static int NumberRoundDown1(int nValue)
    {
    	BigDecimal rtnVal = new BigDecimal (nValue);   
    	rtnVal = rtnVal.setScale(-1, BigDecimal.ROUND_DOWN);    
    	return rtnVal.intValue();
    }
    public static long NumberRoundDown2(float f)
    {
    	BigDecimal rtnVal = new BigDecimal (f);   
    	rtnVal = rtnVal.setScale(-1, BigDecimal.ROUND_DOWN);    
    	return rtnVal.intValue();
    }

    
    public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

    public static void writeToFile1(String filename, byte[] pData) throws Exception

    {

        if(pData == null){
            return;
        }

        int lByteArraySize = pData.length;

        File lOutFile = new File(filename);

        FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);

        lFileOutputStream.write(pData);

        lFileOutputStream.close();

        
    }
    public static ByteBuf strToBytoBuf(String strData)
    {
        return Unpooled.wrappedBuffer(strData.getBytes(StandardCharsets.UTF_8));
    }
    public static String ByteBufToStr(ByteBuf byteBuf)
    {
        return byteBuf.toString(CharsetUtil.UTF_8);
    }
    public static String MakeMessageLen(int nLen)
    {
        String strLen = String.valueOf(nLen);
        for (int i=strLen.length(); i<10; i++)
        {
            strLen = strLen + " ";
        }
        return strLen;
    }
}