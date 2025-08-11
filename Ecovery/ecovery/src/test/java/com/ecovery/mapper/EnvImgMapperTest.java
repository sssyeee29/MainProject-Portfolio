package com.ecovery.mapper;



import com.ecovery.domain.EnvImgVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 환경톡톡 이미지 Mapper 테스트 클래스
 * EnvImgMapper 인터페이스의 이미지 등록, 조회, 삭제 기능에 대한 단위 테스트 수행
 * 테스트 대상 테이블 : env_img
 *
 * 주요 테스트 시나리오:
 * - 이미지 등록(insert)
 * - 게시글 ID로 이미지 목록 조회(getEnvImgList)
 * - 이미지 ID로 단일 삭제(deleteById)
 * - 게시글 ID로 전체 이미지 삭제(deleteByEnvId)
 *
 * @author : yukyeong
 * @fileName : EnvImgMapperTest
 * @since : 250726
 * @history
      - 250726 | yukyeong | 이미지 등록/조회/삭제 단위 테스트 작성
 */

@SpringBootTest
@Slf4j
class EnvImgMapperTest {

    @Autowired
    private EnvImgMapper envImgMapper;

    @Test
    @DisplayName("이미지 등록 테스트")
    public void testInsert() {
        // Given : 테스트용 이미지 객체 생성
        EnvImgVO vo = EnvImgVO.builder()
                .envId(11L) // 해당 게시글 ID에 이미지 등록
                .imgName("test_image.jpg")
                .oriImgName("원본_test.jpg")
                .imgUrl("/images/test_image.jpg")
                .createdAt(LocalDateTime.now())
                .build();

        // When : 이미지 등록 메서드 호출
        envImgMapper.insert(vo);

        // Then : 해당 게시글의 이미지 목록 조회 후 검증
        List<EnvImgVO> list = envImgMapper.getEnvImgList(11L);
        assertFalse(list.isEmpty());
        EnvImgVO last = list.get(list.size() - 1); // 마지막에 추가된 이미지
        assertEquals("test_image.jpg", last.getImgName());
        assertEquals("원본_test.jpg", last.getOriImgName());
        log.info("등록된 이미지: {}", last);
    }

    @Test
    @DisplayName("게시글의 이미지 목록 조회 테스트")
    public void testGetEnvImgList() {
        // Given : 조회 대상 게시글 ID 설정
        Long envId = 11L;

        // When : 해당 게시글의 이미지 목록 조회
        List<EnvImgVO> list = envImgMapper.getEnvImgList(envId);

        // Then : 결과가 null이 아니고 비어 있지 않아야 함
        assertNotNull(list);
        assertFalse(list.isEmpty(), "이미지 목록이 비어 있지 않아야 합니다.");
        for (EnvImgVO vo : list) {
            assertEquals(envId, vo.getEnvId()); // 게시글 ID 일치 확인
            assertNotNull(vo.getCreatedAt()); // 등록 시간 존재 확인
            log.info("조회된 이미지: {}", vo);
        }
    }

    @Test
    @DisplayName("단일 이미지 조회 테스트 (env_img_id 기준)")
    public void testGetById() {
        // Given : 테스트용 이미지 등록
        EnvImgVO vo = EnvImgVO.builder()
                .envId(11L)
                .imgName("single_test.jpg")
                .oriImgName("원본_single.jpg")
                .imgUrl("/images/single_test.jpg")
                .createdAt(LocalDateTime.now())
                .build();
        envImgMapper.insert(vo);

        // insert 후 생성된 ID
        Long insertedId = vo.getEnvImgId();

        // When : 해당 이미지 ID로 조회
        EnvImgVO result = envImgMapper.getById(insertedId);

        // Then : 결과 검증
        assertNotNull(result, "조회된 이미지가 null이면 안 됩니다.");
        assertEquals("single_test.jpg", result.getImgName());
        assertEquals("원본_single.jpg", result.getOriImgName());
        log.info("단일 이미지 조회 결과: {}", result);
    }


    @Test
    @DisplayName("단일 이미지 삭제 테스트 (env_img_id 기준)")
    public void testDeleteById() {
        // Given : 삭제할 이미지 1건 조회
        Long envId = 11L;
        List<EnvImgVO> list = envImgMapper.getEnvImgList(envId);
        assertFalse(list.isEmpty(), "삭제 테스트 전에 이미지가 최소 1개 있어야 함");
        EnvImgVO target = list.get(0);
        Long envImgId = target.getEnvImgId();

        // When : 해당 이미지 ID로 삭제
        int deleted = envImgMapper.deleteById(envImgId);

        // Then : 삭제 건수가 1이어야 함
        assertEquals(1, deleted, "이미지 1건이 삭제되어야 합니다.");
        log.info("삭제된 이미지 ID: {}", envImgId);
    }

    @Test
    @DisplayName("게시글 ID로 이미지 전체 삭제 테스트")
    public void testDeleteByEnvId() {
        // Given: 테스트용 이미지 3개 등록
        for (int i = 1; i <= 3; i++) {
            EnvImgVO vo = EnvImgVO.builder()
                    .envId(11L)
                    .imgName("delete_test_" + i + ".jpg")
                    .oriImgName("delete_ori_" + i + ".jpg")
                    .imgUrl("/images/delete_test_" + i + ".jpg")
                    .createdAt(LocalDateTime.now())
                    .build();
            envImgMapper.insert(vo);
        }

        // 등록된 이미지 수가 정확히 3개인지 확인
        List<EnvImgVO> beforeDelete = envImgMapper.getEnvImgList(11L);
        assertEquals(3, beforeDelete.size(), "삭제 전 이미지가 3개 등록되어 있어야 합니다.");

        // When : 게시글 ID로 이미지 전체 삭제
        int deletedCount = envImgMapper.deleteByEnvId(11L);

        // Then : 삭제 건수가 3개여야 함, 삭제 후 목록은 비어 있어야 함
        log.info("삭제된 이미지 수: {}", deletedCount);
        assertEquals(3, deletedCount, "삭제된 이미지 수는 3개여야 합니다.");

        List<EnvImgVO> afterDelete = envImgMapper.getEnvImgList(11L);
        assertTrue(afterDelete.isEmpty(), "삭제 후 이미지 목록이 비어 있어야 합니다.");
    }

}