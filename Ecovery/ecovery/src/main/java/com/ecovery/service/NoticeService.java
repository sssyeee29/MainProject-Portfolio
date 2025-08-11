package com.ecovery.service;

import com.ecovery.dto.Criteria;
import com.ecovery.dto.NoticeDto;

import java.util.List;

/*
 * 공지사항 게시글 서비스 인터페이스
 * 공지사항 등록, 조회, 수정, 삭제, 페이징 목록 조회, 조회수 증가 등의
 * 핵심 비즈니스 로직을 정의하며, Controller와 Mapper 사이의 중간 계층 역할 수행
 * @author   : yukyeong
 * @fileName : NoticeService.java
 * @since    : 250723
 * @history
     - 250723 | yukyeong | 공지사항 서비스 인터페이스 최초 작성 (CRUD, 페이징, 조회수, DTO 적용)
 */

public interface NoticeService {

    public void register(NoticeDto noticeDto); // 게시글 등록 (DTO로 등록)

    public NoticeDto get(Long noticeId); // 게시글 단건 조회 (DTO 반환)

    public boolean modify(NoticeDto noticeDto); // 게시글 수정 (DTO로 수정)

    public boolean remove(Long noticeId); // 게시글 삭제 (삭제는 ID만으로)

    public List<NoticeDto> getList(Criteria cri); // 게시글 목록 조회 (페이징 포함) (목록도 DTO로)

    public int getTotal(Criteria cri); // 게시글 총 개수 조회 (페이징 처리에 사용)

    public void increaseViewCount(Long noticeId); // 게시글 조회수 증가
}
