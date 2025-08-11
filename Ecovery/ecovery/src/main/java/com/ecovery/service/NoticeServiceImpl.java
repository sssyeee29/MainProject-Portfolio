package com.ecovery.service;


import com.ecovery.domain.NoticeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.NoticeDto;
import com.ecovery.mapper.NoticeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 공지사항 게시글 서비스 구현 클래스
 * 게시글 등록, 조회, 수정, 삭제, 페이징 목록 조회, 조회수 증가까지의 비즈니스 로직을 구현하며,
 * DTO를 사용해 계층 간 데이터 전달, 내부에서는 VO를 통해 MyBatis Mapper와 연결함
 * @author   : yukyeong
 * @fileName : NoticeServiceImpl
 * @since    : 250723
 * @history
     - 250723 | yukyeong | 공지사항 서비스 구현 클래스 전체 작성 (CRUD, 페이징, DTO 적용, 조회수 증가 포함)
     - 250729 | yukyeong | 게시글 category 필드 DTO/VO 매핑 처리 추가
     - 250802 | yukyeong | VO → DTO 매핑 시 작성자 권한(role) 포함되도록 setRole 추가
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    // NoticeMapper는 DB 연동을 위한 MyBatis 인터페이스
    private final NoticeMapper noticeMapper;

    // DTO를 VO로 변환하는 메서드 (DB 작업용으로 변환)
    private NoticeVO dtoToVo(NoticeDto noticeDto) {
        NoticeVO notice = new NoticeVO();
        notice.setNoticeId(noticeDto.getNoticeId());
        notice.setTitle(noticeDto.getTitle());
        notice.setContent(noticeDto.getContent());
        notice.setMemberId(noticeDto.getMemberId());
        notice.setCategory(noticeDto.getCategory()); // 카테고리 추가
        return notice;
    }

    // VO를 DTO로 변환하는 메서드 (컨트롤러 또는 뷰로 전달용)
    private NoticeDto voToDto(NoticeVO notice) {
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setNoticeId(notice.getNoticeId());
        noticeDto.setTitle(notice.getTitle());
        noticeDto.setContent(notice.getContent());
        noticeDto.setMemberId(notice.getMemberId());
        noticeDto.setCategory(notice.getCategory()); // 카테고리 추가
        noticeDto.setNickname(notice.getNickname());
        noticeDto.setViewCount(notice.getViewCount());
        noticeDto.setCreatedAt(notice.getCreatedAt());
        noticeDto.setUpdatedAt(notice.getUpdatedAt());
        noticeDto.setRole(notice.getRole()); // 작성자 권한 추가
        return noticeDto;
    }

    // 게시글 등록 (DTO → VO 변환 후 Mapper 호출)
    // DTO를 VO로 변환 후 insert 쿼리 실행, 생성된 ID를 다시 DTO에 설정
    @Override
    public void register(NoticeDto noticeDto) {
        log.info("register() - 게시글 등록");
        NoticeVO notice = dtoToVo(noticeDto);
        noticeMapper.insert(notice);
        noticeDto.setNoticeId(notice.getNoticeId()); // DB에서 생성된 ID를 DTO에 반영
    }

    // 게시글 단건 조회
    // 조회된 VO를 DTO로 변환하여 반환
    @Override
    public NoticeDto get(Long noticeId) {
        log.info("get() - 게시글 단건 조회");
        NoticeVO notice = noticeMapper.read(noticeId);
        if (notice == null) return null; // null 체크 추가
        return voToDto(notice);
    }

    // 게시글 수정
    // DTO를 VO로 변환하여 update 쿼리 실행, 결과가 1이면 true 반환
    @Override
    public boolean modify(NoticeDto noticeDto) {
        log.info("modify() - 게시글 수정");
        NoticeVO notice = dtoToVo(noticeDto);
        return noticeMapper.update(notice) == 1; // update() 실행 시 영향받은 행 수가 1이면 true
    }

    // 게시글 삭제
    // 전달받은 ID로 delete 쿼리 실행, 결과가 1이면 true 반환
    @Override
    public boolean remove(Long noticeId) {
        log.info("remove() - 게시글 삭제");
        return noticeMapper.delete(noticeId) == 1;
    }

    // 게시글 목록 조회 (페이징 포함)
    // VO 목록을 DTO 목록으로 변환하여 반환
    @Override
    public List<NoticeDto> getList(Criteria cri) {
        log.info("getList() - 게시글 목록 조회");
        List<NoticeVO> noticeList = noticeMapper.getListWithPaging(cri);

        // Stream API로 VO 리스트 → DTO 리스트 변환
        return noticeList.stream()
                .map(this::voToDto)
                .collect(Collectors.toList());
    }

    // 전체 게시글 수 조회 (페이징 처리에 필요)
    @Override
    public int getTotal(Criteria cri) {
        log.info("getTotal() - 게시글 총 개수 조회");
        return noticeMapper.getTotalCount(cri);
    }

    // 게시글 조회수 증가
    // 주어진 ID에 해당하는 게시글의 viewCount +1 처리
    @Override
    public void increaseViewCount(Long noticeId){

        noticeMapper.updateViewCount(noticeId);
    }
}
