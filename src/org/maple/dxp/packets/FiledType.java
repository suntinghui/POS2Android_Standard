package org.maple.dxp.packets;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 字段存储类型
 * @author maple
 *
 */
public enum FiledType
{
	//数字长度不足左补0
	  NUMERIC(true, 0), 
	//字符或数字长度不足右补空格
	  ALPHA(true, 0), 
	//LL 可变长域的长度值(二位数)
	  LLVAR(false, 0), 
	//可变长域的长度值(三位数)
	  LLLVAR(false, 0), 
	/**
	 * MMddHHmmss
	 */
	  DATE10(false, 10), 
	/**
	 * MMdd
	 */
	  DATE4(false, 4), 
	  /**
	 * yyyyMMdd
	 */
	  DATE8(false, 8),
	/**
	 * yyMM
	 */
	  DATE_EXP(false, 4), 
	/**
	 * HHmmss
	 */
	  TIME(false, 6), 
	/**
	 * 金额
	 */
	  AMOUNT(false, 12, 4);
//长度是否必须
  private boolean needsLen;
//长度
  private int length;
  //精度
  private int  precision;

  private FiledType(boolean flag, int len) {
    this.needsLen = flag;
    this.length = len;
  }
  private FiledType(boolean flag, int len,int  precision) {
	    this.needsLen = flag;
	    this.length = len;
	    this.precision=precision;
	  }
  public boolean needsLength()
  {
    return this.needsLen;
  }

  public int getLength()
  {
    return this.length;
  }

  //日期格式化
  public String formatDate(Date value)
  {
	if (this == DATE8)
      return new SimpleDateFormat("yyyyMMdd").format(value);  
    if (this == DATE10)
      return new SimpleDateFormat("MMddHHmmss").format(value);
    if (this == DATE4)
      return new SimpleDateFormat("MMdd").format(value);
    if (this == DATE_EXP)
      return new SimpleDateFormat("yyMM").format(value);
    if (this == TIME) {
      return new SimpleDateFormat("HHmmss").format(value);
    }
    throw new IllegalArgumentException("Cannot format date as " + this);
  }


  //格式化数值
	public String format(String value, int length) {
		if (this == ALPHA) {
	    	if (value == null) {
	    		value = "";
	    	}
	        if (value.length() > length) {
	        	throw new IllegalArgumentException("ALPHA value is larger than intended length: " + value + " LEN " + length);
	        }
	     
	        return value;
		} else if (this == LLVAR) {
			 if (value.length() < length) {
		        	throw new IllegalArgumentException("LLLVAR value is Less than the minimum length: " + value + " LEN " + length);
		        }
			 
			 if (value.length() >99) {
		        	throw new IllegalArgumentException("LLLVAR value is larger than intended length:: " + value + " LEN " + length);
		        }
			return value;
		} else if (this == LLLVAR) {
			 if (value.length() < length) {
		        	throw new IllegalArgumentException("LLLVAR value is Less than the minimum length: " + value + " LEN " + length);
		        }
			 
			 if (value.length() >999) {
		        	throw new IllegalArgumentException("LLLVAR value is larger than intended length:: " + value + " LEN " + length);
		        }
			return value;
		} else if (this == NUMERIC) {
	
	        char[] x = value.toCharArray();
	        if (x.length > length) {
	        	throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
	        }
	      
	        return new String(x);
		}
		throw new IllegalArgumentException("Cannot format String as " + this);
	}

	/** Formats the integer value as a NUMERIC, an AMOUNT, or a String. */
	public String format(long value, int length) {
		if (this == NUMERIC) {
	  
	        char[] x = Long.toString(value).toCharArray();
	        if (x.length > length) {
	        	throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
	        }

	        return new String(x);
		} else if (this == ALPHA || this == LLLVAR) {
			return format(Long.toString(value), length);
		} else if (this == AMOUNT) {
			String v = Long.toString(value);
			
			return new String(v);
		}
		throw new IllegalArgumentException("Cannot format number as " + this);
	}


	public String format(BigDecimal value, int length,int precision) {
	
		if (this == AMOUNT) {
			int post=length-precision;
			String FORMAT="0000000000000000";
			FORMAT=FORMAT.substring(0,post)+"."+FORMAT.substring(0,precision);
		//	System.out.println("$$$$$$$$$$$$$$$$$$"+FORMAT);
			//金额格式化
			String v = new DecimalFormat(FORMAT).format(value);
			//return v.substring(post) + v.substring(post+1);
			return v;
		} else if (this == NUMERIC) {
			return format(value.longValue(), length);
		} else if (this == ALPHA || this == LLLVAR) {
			return format(value.toString(), length);
		}
		throw new IllegalArgumentException("Cannot format BigDecimal as " + this);
	}

}