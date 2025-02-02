package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ChattingDAO;
import com.example.demo.dto.ChattingRoom;
import com.example.demo.dto.Message;
import com.example.demo.util.Member;
import com.example.demo.dto.Member;

@Service
public class ChattingServiceImpl implements ChattingService{

    @Autowired
    private ChattingDAO dao;

    @Override
    public List<ChattingRoom> selectRoomList(int memberNo) {
        return dao.selectRoomList(memberNo);
    }
    
    @Override
    public int checkChattingNo(Map<String, Integer> map) {
        return dao.checkChattingNo(map);
    }

    @Override
    public int createChattingRoom(Map<String, Integer> map) {
        return dao.createChattingRoom(map);
    }


    @Override
    public int insertMessage(Message msg) {
        msg.setMessageContent(Util.XSSHandling(msg.getMessageContent()));
        return dao.insertMessage(msg);
    }

    @Override
    public int updateReadFlag(Map<String, Object> paramMap) {
        return dao.updateReadFlag(paramMap);
    }

    @Override
    public List<Message> selectMessageList( Map<String, Object> paramMap) {
        System.out.println(paramMap);
        
        List<Message> messageList = dao.selectMessageList(  Integer.parseInt( String.valueOf(paramMap.get("chattingNo") )));
        
        if(!messageList.isEmpty()) {
            int result = dao.updateReadFlag(paramMap);
        }
        
        return messageList;
    }

    // 채팅 상대 검색
   @Override
   public List<Member> selectTarget(Map<String, Object> map) {
      return dao.selectTarget(map);
   }
    
}