package com.github.markusbernhardt.xmldoclet.simpledata;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Field1
 */
public class Field1 {
	/** field */
	public String field0;

	/** field */
	public String field1;

	/** field */
	@SuppressWarnings("unused")
	private String field2;

	/** field */
	String field3;

	/** field */
	protected String field4;

	/** field */
	public volatile String field5;

	/** field */
	public static String field6;

	/** field */
	public transient String field7;

	/** field */
	public final String field8 = "mer";

	/** field */
	public final String field9 = "testy";

	/** field */
	public final int field10 = 10;

	/** field */
	@Deprecated
	public String field11;

	/** field */
	@Deprecated
	@Annotation12("mister")
	public String field12;

	/** field */
	public String field13;

	/** field */
	public ArrayList<?> field14;

	/** field */
	public HashMap<String, Integer> field15;

	/** field */
	public String[] field16;

}