package com.example.bkptest;

/**
 * ShouldBeExcutedFile is the file which by user uploaded 
 * and should be excuted by textract service
 * @author yinjueqian
 *
 */
public class ShouldBeExcutedFile {
	
	private String prefix;
	private String[] suffixList;
	
	public ShouldBeExcutedFile() {
		super();
	}
	
	public ShouldBeExcutedFile(String prefix, String[] suffixList) {
		super();
		this.prefix = prefix;
		this.suffixList = suffixList;
	}
	
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String[] getSuffixList() {
		return suffixList;
	}
	public void setSuffixList(String[] suffixList) {
		this.suffixList = suffixList;
	}

	
}
