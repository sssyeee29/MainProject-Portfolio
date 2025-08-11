package com.ecovery.mapper;

import com.ecovery.domain.FreeReplyVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeReplyDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/*
 * 무료나눔 댓글 Mapper
 * 댓글 및 대댓글 DB 연동 인터페이스
 * @author : yeonsu
 * @fileName : FreeReplyMapper
 * @since : 250711
 */

@Mapper
public interface FreeReplyMapper {

    public void insert(FreeReplyVO freeReply);             // 댓글 등록

    public FreeReplyDto read(Long replyId);                // 댓글 조회(특정댓글 1개)

    public int update(FreeReplyVO freeReply);              // 댓글 수정

    public int delete(Long replyId);                       // 댓글 삭제
    
    public List<FreeReplyDto> getParentReplies(@Param("freeId") Long freeId,
                                               @Param("criteria") Criteria criteria,
                                               @Param("sortType") String sortType); // 게시글의 부모 댓글 목록 조회(정렬 기준: 최신순/등록순)

    public List<FreeReplyDto> getChildReplies(@Param("parentId") Long parentId); // 특정 댓글의 대댓글 목록 조회

    public int getTotalReplyCount(Long freeId);            // 해당 게시글의 전체 댓글 수 (페이징)

    public int getChildReplyCount(Long parentId);           //특정 부모 댓글의 대댓글 수 조회

}
