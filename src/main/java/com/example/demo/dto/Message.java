package com.example.demo.dto;

public class Message {
    private int messageNo; // 메시지 번호
    private String messageContent; // 메시지 내용
    private String readFlag; // 읽음 여부
    private int senderNo; // 보낸 회원 번호
    private int targetNo; // 받는 회원 번호
    private int chattingNo; // 채팅방 번호
    private String sendTime; // 메시지 보낸 시간

    // Getter and Setter methods

    public int getMessageNo() {
        return messageNo;
    }

    public void setMessageNo(int messageNo) {
        this.messageNo = messageNo;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public int getSenderNo() {
        return senderNo;
    }

    public void setSenderNo(int senderNo) {
        this.senderNo = senderNo;
    }

    public int getTargetNo() {
        return targetNo;
    }

    public void setTargetNo(int targetNo) {
        this.targetNo = targetNo;
    }

    public int getChattingNo() {
        return chattingNo;
    }

    public void setChattingNo(int chattingNo) {
        this.chattingNo = chattingNo;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    // toString method
    @Override
    public String toString() {
        return "Message{" +
               "messageNo=" + messageNo +
               ", messageContent='" + messageContent + '\'' +
               ", readFlag='" + readFlag + '\'' +
               ", senderNo=" + senderNo +
               ", targetNo=" + targetNo +
               ", chattingNo=" + chattingNo +
               ", sendTime='" + sendTime + '\'' +
               '}';
    }
}
