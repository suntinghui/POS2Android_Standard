package com.dhcc.pos.packets;

/**
 *系统跟踪号 ，范围  1 and 999999
 */
public interface cnSystemTraceNumGenerator {

	/** Returns the next trace number. */
	public int nextTrace();

	/** Returns the last number that was generated. */
	public int getLastTrace();

}