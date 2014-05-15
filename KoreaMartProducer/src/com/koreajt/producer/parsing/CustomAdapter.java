package com.koreajt.producer.parsing;

import android.content.Context;

public class CustomAdapter {

	private String m_puName;
	private String m_grandeName;
	private String m_unitqty;
	private String m_unitName;
	private String m_maxPrice;
	private String m_minPrice;
	private String m_avgPrice;
	private String m_insDate;
	
	public CustomAdapter(Context context, String p_puName, String p_grandeName, String p_unitqty, 
			String p_unitName, String p_maxPrice, String p_minPrice, String p_avgPrice, String P_insDate){
		m_puName = p_puName;    
		m_grandeName = p_grandeName;
	    m_unitqty = p_unitqty;   
	    m_unitName = p_unitName;  
        m_maxPrice = p_maxPrice;          m_minPrice = p_minPrice;          m_avgPrice = p_avgPrice;          m_insDate = P_insDate;
	}
	public String getPuName(){
		return m_puName;
	}
	public String getGrandeName(){
		return m_grandeName;
	}
	public String getUnitqty(){
		return m_unitqty;
	}
	public String getUnitName(){
		return m_unitName;
	}
	public String getMaxPrice(){
		return m_maxPrice;
	}
	public String getMinPrice(){
		return m_minPrice;
	}
	public String getAvgPrice(){
		return m_avgPrice;
	}
	public String getInsDate(){
		return m_insDate;
	}
}