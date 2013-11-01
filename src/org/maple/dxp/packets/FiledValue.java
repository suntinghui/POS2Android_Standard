package org.maple.dxp.packets;

import java.math.BigDecimal;
import java.util.Date;
/**
 *  *字段域所存储的数据
 * @author maple
 */
public class FiledValue<T>
{
	/** 数据类型*/
  private FiledType datatype;
  
   /**数值*/
  private T value;
  
  /** 长度*/
  private int length;

  //精度
  private int  precision;
  
  public FiledValue(FiledType t, T value)
  {
    if (t.needsLength()) {
      throw new RuntimeException("Fixed-value types must use constructor that specifies length");
    }
    this.datatype = t;
    this.value = value;
    if ( this.datatype == FiledType.LLLVAR) {
    	
    		this.length = value.toString().getBytes().length; 
    }
    else {
    		this.length = this.datatype.getLength();
    }
  }

  public FiledValue(FiledType t, T val, int len)
  {
    this.datatype = t;
    this.value = val;
    this.length = len;
    if ((this.length == 0) && (t.needsLength()))
      throw new RuntimeException(t+"   Length must be greater than zero");
    if (t == FiledType.LLLVAR) {
    	//设置变长域的长度
      this.length = val.toString().getBytes().length;
    }
  }
  
  public FiledValue(FiledType t, T val, int len,int  precision)
  {
    this.datatype = t;
    this.value = val;
    this.length = len;
    this.precision=precision;
    if ((this.length == 0) && (t.needsLength()))
      throw new RuntimeException("Length must be greater than zero");
    if (t == FiledType.LLLVAR) {
    	//设置变长域的长度
      this.length = val.toString().getBytes().length;
    }
  }

  public FiledType getType()
  {
    return this.datatype;
  }

  public int getLength()
  {
    return this.length;
  }

  public T getValue()
  {
    return this.value;
  }

	public String toString() {
		if (value == null) {
			return "ISOValue<null>";
		}
		if (datatype == FiledType.NUMERIC || datatype == FiledType.AMOUNT) {
			if (datatype == FiledType.AMOUNT) {
				return datatype.format((BigDecimal)value, 12,precision);
			} else if (value instanceof Number) {
				return datatype.format(((Number)value).longValue(), length);
			} else {
				return datatype.format(value.toString(), length);
			}
		} else if (datatype == FiledType.ALPHA) {
			return datatype.format(value.toString(), length);
		} else if (datatype == FiledType.LLLVAR) {
			return value.toString();
		} else if (value instanceof Date) {
			return datatype.formatDate((Date)value);
		}
		return value.toString();
	}
 
  public FiledValue<T> clone()
  {
    return new FiledValue(this.datatype, this.value, this.length);
  }

  public boolean equals(Object other)
  {
    if ((other == null) || !(other instanceof FiledValue)) {
      return false;
    }
    FiledValue comp = (FiledValue)other;
    return (comp.getType() == getType()) && (comp.getValue().equals(getValue())) && (comp.getLength() == getLength());
  }

 
}