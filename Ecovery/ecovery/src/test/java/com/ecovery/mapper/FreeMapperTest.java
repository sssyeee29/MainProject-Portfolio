package com.ecovery.mapper;

import com.ecovery.constant.DealStatus;
import com.ecovery.constant.ItemCondition;
import com.ecovery.domain.FreeVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.FreeDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
 * 무료나눔 게시글 Mapper 테스트
 * FreeMapper의 게시글 CRUD 기능이 정상 동작하는지 단위 테스트 진행
 * 게시글 등록, 조회(단건/전체), 수정, 삭제 기능 검증
 * @author : yeonsu
 * @fileName : FreeMapperTest
 * @since : 250715
 */

@SpringBootTest
@Slf4j
class FreeMapperTest {

    @Autowired
    private FreeMapper freeMapper;

    @Test
    @DisplayName("게시글 등록 테스트")
    public void testInsert() {

        // Given (준비)
        FreeVO vo = FreeVO.builder()
                .title("금요일2")
                .memberId(1L)
                .category("기타")
                .regionGu("송파구")
                .regionDong("방이동")
                .content("금요일2 폐기물")
                .itemCondition(ItemCondition.MEDIUM)
                .dealStatus(DealStatus.ONGOING)
                .build();

        // When (실행)
        freeMapper.insert(vo);

        // Then (검증)
        // 1) insert 후 FreeId가 자동으로 채워졌는지
        assertNotNull(vo.getFreeId(), "id는 null값이면 안됩니다.");

        // 2) 방금 insert한 데이터 조회 (PK로 단건 조회 -> DTO로)
        FreeVO inserted = freeMapper.read(vo.getFreeId());
        assertNotNull(inserted, "insert 이후에는 null값이면 안됩니다.");

        // 3) DB에 저장된 데이터가 내가 입력한 값이랑 같은지 검증 (예상괎과 실제값 비교)
        assertEquals("금요일2", inserted.getTitle());
        assertEquals("금요일2 폐기물", inserted.getContent());


        log.info("삽입된 게시글 : {}", inserted);
    }

    @Test
    @DisplayName("게시글 전체 목록 조회")
    public void testGetList(){

        // Given : 페이징 및 검색 조건 설정
        Criteria cri = new Criteria();
        cri.setPageNum(1); // 1페이지
        cri.setAmount(8);  // 페이지당 8개

        // 전체 게시글 수 가져오기
        int total = freeMapper.getTotalCount(cri);
        log.info("전체 게시글 수 (DB 기준): {}", total);

        // When : mapper 호출
        List<FreeDto> list = freeMapper.getListWithPaging(cri);
        log.info("조회된 게시글 수 (현재 페이지) : {}", list.size());

        // Then : 결과 출력
        for (FreeDto dto : list) {
            log.info("게시글 : {}", dto);
        }
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void testRead(){

        // Given - 조회할 게시글 Id
        Long targetId = 8L;

        // When
        FreeVO vo = freeMapper.read(targetId);

        // Then
        if (vo != null) {
            log.info("조회된 게시글 : {}", vo.toString());
        }else {
            log.info("조회 결과가 없습니다. (freeId = {})", targetId);
        }
    }


    @Test
    @DisplayName("게시글 삭제 테스트")
    public void testDelete() {

        // Given : 삭제할 게시글의 ID와 작성자 ID 설정
        Long targetId = 22L;
        Long memberId = 2L; // 실제 작성자의 memberId로 수정해야 함

        // FreeVO 객체 생성 후 ID 설정
        FreeVO vo = new FreeVO();
        vo.setFreeId(targetId);
        vo.setMemberId(memberId); // 삭제 조건에 작성자 ID도 포함된다면 꼭 필요함

        // When : 게시글 삭제 요청 실행
        int deletedCount = freeMapper.delete(vo);

        // Then : 삭제 결과 확인
        if (deletedCount == 1) {
            log.info("게시글 삭제 성공 : {}", targetId);
        } else {
            log.info("게시글 삭제 실패 또는 존재하지 않음 : {}", targetId);
        }
    }


//// Given (준비)
//        FreeVO vo = FreeVO.builder()
//                .title("수요일")
//                .memberId(2L)
//                .category("기타")
//                .regionGu("송파구")
//                .regionDong("방이동")
//                .content("수요일 폐기물")
//                .itemCondition(ItemCondition.MEDIUM)
//                .build();
    @Test
    @DisplayName("게시글 수정 테스트")
    public void testUpdate(){

        // Given : 수정할 게시글 정보 준비
        FreeVO vo = new FreeVO();
        vo.setFreeId(23L);  // 수정 대상 ID
        vo.setTitle("수정된 제목");
        vo.setContent("수정된 내용");
        vo.setCategory("가전");
        vo.setRegionGu("서초구");
        vo.setRegionDong("반포동");
        vo.setItemCondition(ItemCondition.HIGH);
        vo.setDealStatus(DealStatus.DONE);

        // When: 수정 메서드 실행
        int updatedCount = freeMapper.update(vo);

        // Then: 결과 확인
        if (updatedCount == 1) {
            log.info("게시글 수정 성공: {}", vo);
        } else {
            log.info("게시글 수정 실패: {}", vo);
        }
    }
}




























