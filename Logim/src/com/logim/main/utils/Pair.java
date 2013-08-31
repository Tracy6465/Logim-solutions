package com.logim.main.utils;

/**
 * 
 * @author David Prieto Rivera 
 *   Temporarily stores in memory the contents of the database of identities
 * 
 */
public class Pair {

	String first;
	String second;
	Object osecond;

	public Pair(String x, String y) {
		first = x;
		second = y;
	}
	
	public Pair(String x, Object y) {
		first = x;
		osecond = y;
	}

	public void setCodigo(String x) {
		first = x;
	}

	public void setNombre(String x) {
		second = x;
	}

	public String getCodigo() {
		return first;
	}

	public String getNombre() {
		return second;
	}
}
