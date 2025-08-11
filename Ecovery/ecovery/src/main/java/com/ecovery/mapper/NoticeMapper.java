package com.ecovery.mapper;

import com.ecovery.domain.NoticeVO;
import com.ecovery.dto.Criteria;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
 * 공지사항 게시글 Mapper 인터페이스
 * DB 연동을 통해 공지사항의 등록, 조회, 수정, 삭제, 페이징 및 검색, 조회수 증가 기능을 수행
 * 주요 기능:
 * - 게시글 등록 (insert, insertSelectKey)
 * - 게시글 단건 조회 (read)
 * - 게시글 목록 조회 (getList, getListWithPaging)
 * - 게시글 수정 및 삭제 (update, delete)
 * - 게시글 수 조회 (getTotalCount)
 * - 조회수 증가 (updateViewCount)
 *
 * @author  : yukyeong
 * @fileName: NoticeMapper
 * @since   : 250723
 * @history
     - 250723 | yukyeong | 공지사항 Mapper 인터페이스 최초 생성 및 CRUD, 페이징, 조회수 관련 메서드 정의
 */

@Mapper
public interface NoticeMapper {

    // 게시글 전체 목록 조회
    public List<NoticeVO> getList();

    // 게시글 단건 조회
    public NoticeVO read(Long noticeId);

    // 게시글 등록
    public void insert(NoticeVO notice);

    // 게시글 삭제
    public int delete(Long noticeId);

    // 게시글 수정
    public int update(NoticeVO notice);

    // 게시글 목록 조회 (페이징 + 검색)
    public List<NoticeVO> getListWithPaging(Criteria cri);

    // 게시글 총 개수 조회 (페이징 + 검색)
    public int getTotalCount(Criteria cri);

    // 게시글 조회수 증가
    public void updateViewCount(Long noticeId);

}
