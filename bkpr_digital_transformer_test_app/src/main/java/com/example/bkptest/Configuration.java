package com.example.bkptest;

/**
 * Configuration the AWS to run the services
 * @author yinjueqian
 *
 */
public class Configuration {
	
    private String awsCredential;
	private String awsRegion;
	private String s3BucketName;
	
	public Configuration() {
		super();
	}
	
	public Configuration (String awsCredential, String awsRegion) {
		super();
		this.awsCredential = awsCredential;
		this.awsRegion = awsRegion;
		
	}

	public Configuration(String awsCredential, String awsRegion, String s3BucketName) {
		super();
		this.awsCredential = awsCredential;
		this.awsRegion = awsRegion;
		this.s3BucketName = s3BucketName;
	}

	public String getAwsCredential() {
		return awsCredential;
	}

	public void setAwsCredential(String awsCredential) {
		this.awsCredential = awsCredential;
	}

	public String getAwsRegion() {
		return awsRegion;
	}

	public void setAwsRegion(String awsRegion) {
		this.awsRegion = awsRegion;
	}

	public String getS3BucketName() {
		return s3BucketName;
	}

	public void setS3BucketName(String s3BucketName) {
		this.s3BucketName = s3BucketName;
	}

	
	
	
	

}
