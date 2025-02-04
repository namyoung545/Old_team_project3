package com.example.demo.dto;

import java.util.Objects;

public class memberDTO {
	private String userId;
	private String userPw;
	private String email;
	private String name;
	private String phoneNumber;
	private String address;
	private int authorityId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPw() {
		return userPw;
	}
	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getAuthorityId() {
		return authorityId;
	}
	public void setAuthorityId(int authorityId) {
		this.authorityId = authorityId;
	}
	@Override
	public int hashCode() {
		return Objects.hash(address, authorityId, email, name, phoneNumber, userId, userPw);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		memberDTO other = (memberDTO) obj;
		return Objects.equals(address, other.address) && authorityId == other.authorityId
				&& Objects.equals(email, other.email) && Objects.equals(name, other.name)
				&& Objects.equals(phoneNumber, other.phoneNumber) && Objects.equals(userId, other.userId)
				&& Objects.equals(userPw, other.userPw);
	}
	@Override
	public String toString() {
		return "memberDTO [userId=" + userId + ", userPw=" + userPw + ", email=" + email + ", name=" + name
				+ ", phoneNumber=" + phoneNumber + ", address=" + address + ", authorityId=" + authorityId + "]";
	}
    
    
}
