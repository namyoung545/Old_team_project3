package com.example.demo.dto;

import java.sql.Date;
import java.sql.Time;

import com.fasterxml.jackson.annotation.JsonFormat;

public class asReceptionDTO {
	private Integer requestId; // 자동 증가 값

	private String userId;
	private int authorityId;
	private String name;
	private String phoneNumber; // "010-XXXX-XXXX" 형식
	private String email;

	// 시설물 정보
	private String postcode;
	private String address;
	private String detailAddress;
	private String facilityType; // lighting, power, communication 등

	// 신고 상세
	private String issueTitle;
	private String issueDetails;
	private Date preferredDate;
	private Time preferredTime;

	// 나머지
	@JsonFormat(pattern = "yyyy년 MM월 dd일 HH시 mm분 ss초")
	private String receptionDate;
	private String receptionStatus;

	public Integer getRequestId() {
		return requestId;
	}

	public void setRequestId(Integer requestId) {
		this.requestId = requestId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(int authorityId) {
		this.authorityId = authorityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDetailAddress() {
		return detailAddress;
	}

	public void setDetailAddress(String detailAddress) {
		this.detailAddress = detailAddress;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getIssueTitle() {
		return issueTitle;
	}

	public void setIssueTitle(String issueTitle) {
		this.issueTitle = issueTitle;
	}

	public String getIssueDetails() {
		return issueDetails;
	}

	public void setIssueDetails(String issueDetails) {
		this.issueDetails = issueDetails;
	}

	public Date getPreferredDate() {
		return preferredDate;
	}

	public void setPreferredDate(Date preferredDate) {
		this.preferredDate = preferredDate;
	}

	public Time getPreferredTime() {
		return preferredTime;
	}

	public void setPreferredTime(Time preferredTime) {
		this.preferredTime = preferredTime;
	}

	public String getReceptionDate() {
		return receptionDate;
	}

	public void setReceptionDate(String receptionDate) {
		this.receptionDate = receptionDate;
	}

	public String getReceptionStatus() {
		return receptionStatus;
	}

	public void setReceptionStatus(String receptionStatus) {
		this.receptionStatus = receptionStatus;
	}

	@Override
	public String toString() {
		return "asReceptionDTO [requestId=" + requestId + ", userId=" + userId + ", authorityId=" + authorityId
				+ ", name=" + name + ", phoneNumber=" + phoneNumber + ", email=" + email + ", postcode=" + postcode
				+ ", address=" + address + ", detailAddress=" + detailAddress + ", facilityType=" + facilityType
				+ ", issueTitle=" + issueTitle + ", issueDetails=" + issueDetails + ", preferredDate=" + preferredDate
				+ ", preferredTime=" + preferredTime + ", receptionDate=" + receptionDate + ", receptionStatus="
				+ receptionStatus + "]";
	}

}
