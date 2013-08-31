package com.logim.vo;

public class SocialVo {
	
	int logo;
	String title;
	String summary;
	String isused;
	
	public SocialVo(int logo, String title, String summary, String isused) {
		super();
		this.logo = logo;
		this.title = title;
		this.summary = summary;
		this.isused = isused;
	}
	
	public int getLogo() {
		return logo;
	}
	public void setLogo(int logo) {
		this.logo = logo;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getIsused() {
		return isused;
	}
	public void setIsused(String isused) {
		this.isused = isused;
	}
}
