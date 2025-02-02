package com.example.demo.service;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.ChattingRoom;
import com.example.demo.dto.Message;
import com.example.demo.dto.Member;

public interface ChattingService {
    
    List<ChattingRoom> selectRoomList(int memberNo);

    int checkChattingNo(Map<String, Integer> map);

    int createChattingRoom(Map<String, Integer> map);

    int insertMessage(Message msg);

    int updateReadFlag(Map<String, Object> paramMap);

    List<Message> selectMessageList(Map<String, Object> paramMap);

   /** 채팅 상대 검색
    * @param map 
    * @return memberList
    */
   List<Member> selectTarget(Map<String, Object> map);

}