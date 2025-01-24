package com.example.demo.dto;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PHG_AsReceptionDTO {
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
	private String receptionDelivery;
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
	public String getReceptionDelivery() {
		return receptionDelivery;
	}
	public void setReceptionDelivery(String receptionDelivery) {
		this.receptionDelivery = receptionDelivery;
	}
	@Override
	public int hashCode() {
		return Objects.hash(address, authorityId, detailAddress, email, facilityType, issueDetails, issueTitle, name,
				phoneNumber, postcode, preferredDate, preferredTime, receptionDate, receptionDelivery, receptionStatus,
				requestId, userId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PHG_AsReceptionDTO other = (PHG_AsReceptionDTO) obj;
		return Objects.equals(address, other.address) && authorityId == other.authorityId
				&& Objects.equals(detailAddress, other.detailAddress) && Objects.equals(email, other.email)
				&& Objects.equals(facilityType, other.facilityType) && Objects.equals(issueDetails, other.issueDetails)
				&& Objects.equals(issueTitle, other.issueTitle) && Objects.equals(name, other.name)
				&& Objects.equals(phoneNumber, other.phoneNumber) && Objects.equals(postcode, other.postcode)
				&& Objects.equals(preferredDate, other.preferredDate)
				&& Objects.equals(preferredTime, other.preferredTime)
				&& Objects.equals(receptionDate, other.receptionDate)
				&& Objects.equals(receptionDelivery, other.receptionDelivery)
				&& Objects.equals(receptionStatus, other.receptionStatus) && Objects.equals(requestId, other.requestId)
				&& Objects.equals(userId, other.userId);
	}
	@Override
	public String toString() {
		return "PHG_AsReceptionDTO [requestId=" + requestId + ", userId=" + userId + ", authorityId=" + authorityId
				+ ", name=" + name + ", phoneNumber=" + phoneNumber + ", email=" + email + ", postcode=" + postcode
				+ ", address=" + address + ", detailAddress=" + detailAddress + ", facilityType=" + facilityType
				+ ", issueTitle=" + issueTitle + ", issueDetails=" + issueDetails + ", preferredDate=" + preferredDate
				+ ", preferredTime=" + preferredTime + ", receptionDate=" + receptionDate + ", receptionStatus="
				+ receptionStatus + ", receptionDelivery=" + receptionDelivery + "]";
	}

	

}
