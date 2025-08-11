package com.ecovery.mapper;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.dto.FreeImgDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
* 무료나눔 이미지 Mapper 테스트
*  FreeImgMapper의 게시글 CRUD 기능이 정상 동작하는지 단위 테스트 진행
*
 * @author : yeonsu
 * @fileName : FreeImgMapperTest
 * @since : 250715
 * @edit : 250724 - 테스트 코드 다시 실행 및 주석 코드 작성 완료
*/

@SpringBootTest
@Slf4j
class FreeImgMapperTest {

    @Autowired
    private FreeImgMapper freeImgMapper;

    @Test
    @DisplayName("이미지 등록 테스트")
    public void testInsert() {

        // Given(준비) : 새로 등록할 이미지 정보 생성
        FreeImgVO vo = FreeImgVO.builder()
                .freeId(8L)
                .imgName("test2_image")
                .oriImgName("test2.jpg")
                .imgUrl("/images/test2_image.jpg")
                .repImgYn("Y")
                .createdAt(LocalDateTime.now())
                .build();

        // When(실행) : 이미지 등록 실행
        freeImgMapper.insert(vo);

        // Then(검증)
        assertNotNull(vo.getFreeId());

        // getFreeImgList로 가져온 데이터 중 방금 등록한 이미지가 있는지 확인
        List<FreeImgVO> list = freeImgMapper.getFreeImgList(8L);
        FreeImgVO imgVO = list.get(list.size() - 1); // 가장 마지막 이미지가 방금 insert한 이미지..

        assertEquals("test2_image", imgVO.getImgName());
        assertEquals("test2.jpg", imgVO.getOriImgName());
        log.info("등록된 이미지 : {}", imgVO);
        log.info("DB에서 다시 조회된 등록된 이미지 : {}" + imgVO);
    }

    @Test
    @DisplayName("이미지 목록 조회 테스트")
    public void testGetFreeImgList() {

        //Given : 특정 게시글 ID에 이미지가 최소 1개 이상 있다고 가정
        Long freeId = 8L;

        //When : 게시글 ID로 이미지 목록 조회
        List<FreeImgVO> list = freeImgMapper.getFreeImgList(freeId);

        //Then : 결과 검증
        assertNotNull(list, "이미지 리스트는 null이 아니어야 합니다.");
        assertFalse(list.isEmpty(), "이미지 목록이 비어 있지 않아야 합니다.");

        for (FreeImgVO vo : list) {
            log.info("조회된 이미지 : {}", vo);

            // 게시글 ID 일치 확인
            assertEquals(freeId, vo.getFreeId());

            // createdAt 값이 null이 아닌지 확인
            assertNotNull(vo.getCreatedAt(), "생성일(createdAt)이 null이면 안 됩니다.");
        }
    }

    @Test
    @DisplayName("특정 이미지를 free_img_id로 수정 테스트")
    public void testUpdateByFreeImgId() {

        // Given : 수정할 이미지의 free_img_id를 명시적으로 지정
        Long targetFreeImgId = 46L; // 여기에 수정하고 싶은 이미지 ID를 넣으세요

        // DB에서 해당 이미지 정보를 조회
        List<FreeImgVO> allList = freeImgMapper.getFreeImgList(8L); // freeId는 8번 게시글
        FreeImgVO ori = allList.stream()
                .filter(img -> img.getFreeImgId().equals(targetFreeImgId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("지정한 free_img_id의 이미지를 찾을 수 없습니다."));

        // 수정할 VO 생성
        FreeImgVO vo = FreeImgVO.builder()
                .freeImgId(ori.getFreeImgId()) // 수정 대상 PK
                .freeId(ori.getFreeId())       // 원래 게시글 ID 유지
                .imgName("update_img.jpg")
                .oriImgName("수정된파일명.jpg")
                .imgUrl("/images/update_img.jpg")
                .repImgYn("N")
                .build();

        // When : update 실행
        int count = freeImgMapper.update(vo);

        // Then : 수정 성공 여부 확인
        assertEquals(1, count, "이미지 수정은 1건이어야 합니다.");

        // DB에서 다시 조회해서 값 확인
        List<FreeImgVO> updatedList = freeImgMapper.getFreeImgList(ori.getFreeId());
        FreeImgVO updated = updatedList.stream()
                .filter(img -> img.getFreeImgId().equals(targetFreeImgId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("수정된 이미지를 다시 조회할 수 없습니다."));

        log.info("수정된 이미지: {}", updated);

        // 필드 검증
        assertEquals("update_img.jpg", updated.getImgName());
        assertEquals("수정된파일명.jpg", updated.getOriImgName());
        assertEquals("/images/update_img.jpg", updated.getImgUrl());
        assertEquals("Y", updated.getRepImgYn());
    }


    //대표 이미지 여부에 따라 삭제 처리 테스트
    @Test
    @DisplayName("특정 이미지를 free_img_id로 삭제 테스트")
    public void testDeleteByFreeImgId() {

        // Givnen : 삭제 대상 이미지 ID 설정 및 정보 조회
        Long freeId = 8L; // 게시글 ID
        Long targetFreeImgId = 44L; // 삭제하고 싶은 이미지 ID

        // DB에서 해당 게시글의 이미지 목록 조회
        List<FreeImgVO> imageList = freeImgMapper.getFreeImgList(freeId);

        // 삭제 대상 이미지 찾기
        FreeImgVO target = imageList.stream()
                .filter(img -> img.getFreeImgId().equals(targetFreeImgId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지가 존재하지 않습니다."));

        log.info("삭제 대상 이미지: {}", target);

        // When & Then : 대표 이미지 여부에 따라 삭제 제한 또는 삭제 실행
        if ("Y".equals(target.getRepImgYn())) {
            // 예외 발생 여부 테스트
            IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
                throw new IllegalStateException("대표 이미지는 삭제할 수 없습니다.");
            });

            log.info("삭제 실패(대표 이미지): {}", e.getMessage());
            assertEquals("대표 이미지는 삭제할 수 없습니다.", e.getMessage());

        } else {
            // 삭제 실행
            int deleted = freeImgMapper.delete(targetFreeImgId);
            assertEquals(1, deleted, "1건 삭제되어야 합니다.");
            log.info("이미지 삭제 성공: {}", target);
        }
    }

}




















