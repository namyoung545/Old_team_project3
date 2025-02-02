package com.example.demo.dto;

public class ChattingRoom {
    private int chattingNo; // 채팅방 번호
    private String lastMessage; // 최근 메시지
    private String sendTime; // 메시지 보낸 시간
    private int targetNo; // 받는 회원 번호
    private String targetNickName; // 받는 회원 닉네임
    private String targetProfile; // 받는 회원 프로필 사진
    private int notReadCount; // 읽지 않은 메시지 개수

    // Getter and Setter methods

    public int getChattingNo() {
        return chattingNo;
    }

    public void setChattingNo(int chattingNo) {
        this.chattingNo = chattingNo;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getTargetNo() {
        return targetNo;
    }

    public void setTargetNo(int targetNo) {
        this.targetNo = targetNo;
    }

    public String getTargetNickName() {
        return targetNickName;
    }

    public void setTargetNickName(String targetNickName) {
        this.targetNickName = targetNickName;
    }

    public String getTargetProfile() {
        return targetProfile;
    }

    public void setTargetProfile(String targetProfile) {
        this.targetProfile = targetProfile;
    }

    public int getNotReadCount() {
        return notReadCount;
    }

    public void setNotReadCount(int notReadCount) {
        this.notReadCount = notReadCount;
    }

    // toString method
    @Override
    public String toString() {
        return "Chatting{" +
               "chattingNo=" + chattingNo +
               ", lastMessage='" + lastMessage + '\'' +
               ", sendTime='" + sendTime + '\'' +
               ", targetNo=" + targetNo +
               ", targetNickName='" + targetNickName + '\'' +
               ", targetProfile='" + targetProfile + '\'' +
               ", notReadCount=" + notReadCount +
               '}';
    }
}
