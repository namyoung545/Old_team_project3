package com.example.demo.dao;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.demo.dto.ChattingRoom;
import com.example.demo.dto.Message;
import com.example.demo.dto.Member;

@Repository
public class ChattingDAO {
    
    @Autowired
    private SqlSessionTemplate sqlSession;
    
    public List<ChattingRoom> selectRoomList(int memberNo) {
        return sqlSession.selectList("chattingMapper.selectRoomList", memberNo);
    }

    public int checkChattingNo(Map<String, Integer> map) {
        return sqlSession.selectOne("chattingMapper.checkChattingNo", map);
    }

    public int createChattingRoom(Map<String, Integer> map) {
        int result = sqlSession.insert("chattingMapper.createChattingRoom", map);
        int chattingNo = 0;
        if(result > 0)  chattingNo = (int)map.get("chattingNo");
        return chattingNo;
    }


    public int insertMessage(Message msg) {
        return sqlSession.insert("chattingMapper.insertMessage", msg);
    }

    public int updateReadFlag(Map<String, Object> paramMap) {
        return sqlSession.update("chattingMapper.updateReadFlag", paramMap);
    }

    public List<Message> selectMessageList(int chattingNo) {
       return sqlSession.selectList("chattingMapper.selectMessageList", chattingNo);
    }

   public List<Member> selectTarget(Map<String, Object> map) {
      return sqlSession.selectList("chattingMapper.selectTarget", map);
   }
}