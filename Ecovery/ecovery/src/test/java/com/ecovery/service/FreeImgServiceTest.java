package com.ecovery.service;

import com.ecovery.domain.FreeImgVO;
import com.ecovery.dto.FreeImgDto;
import com.ecovery.mapper.FreeImgMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * 무료나눔 이미지 서비스 테스트 클래스
 * 이미지 등록, 수정, 삭제, 조회 등의 기능에 대한 단위 테스트 수행
 * 실제 FreeImgService의 기능 검증을 위한 테스트 코드
 * @author : yeonsu
 * @fileName : FreeImgServiceTest
 * @since : 250724
 */

@SpringBootTest
@Slf4j
class FreeImgServiceTest {

    @Autowired
    private FreeImgService freeImgService;

    @Autowired
    private FreeImgMapper freeImgMapper;

    @Test
    @DisplayName("이미지 등록 테스트")
    @Transactional
    void testRegisterImage() throws Exception {
        // Given
        Long testFreeId = 9L; // 실제 존재하는 게시글 ID로 설정

        // 가짜 MultipartFile 생성 (Mock)
        String originalFileName = "original.png";
        byte[] content = "dummy image content".getBytes();
        MultipartFile mockFile = new MockMultipartFile(
                "imgFile",             // name
                originalFileName,      // originalFilename
                "image/png",           // contentType
                content                // file content
        );

        FreeImgVO imgVO = FreeImgVO.builder()
                .freeId(testFreeId)
                .repImgYn("Y") // 첫 번째 이미지로 가정
                .build();

        // When
        freeImgService.saveFreeImg(imgVO, mockFile);

        // Then
        assertThat(imgVO.getFreeImgId()).isNotNull(); // DB에 저장된 후 자동 생성된 ID가 세팅되어야 함
        log.info("등록된 이미지 ID: {}", imgVO.getFreeImgId());
    }

    @Test
    @DisplayName("이미지 수정 테스트")
    @Transactional
    void testUpdateImage() throws Exception {
        // Given: 기존 이미지 하나 등록
        MultipartFile originalFile = new MockMultipartFile("imgFile", "old.png", "image/png", "old-content".getBytes());
        FreeImgVO vo = FreeImgVO.builder().freeId(9L).repImgYn("N").build();
        freeImgService.saveFreeImg(vo, originalFile);
        Long imgId = vo.getFreeImgId();

        // 기존 이미지 정보 → FreeImgDto 리스트 생성
        FreeImgDto existingDto = new FreeImgDto();
        existingDto.setFreeImgId(imgId);
        existingDto.setFreeId(9L);  // 게시글 ID도 설정
        existingDto.setImgName(vo.getImgName());
        existingDto.setOriImgName(vo.getOriImgName());
        existingDto.setImgUrl(vo.getImgUrl());
        existingDto.setRepImgYn("N");

        List<FreeImgDto> deletedImgList = List.of(existingDto); // 삭제할 이미지로 가정

        // 새 이미지 파일 준비
        MultipartFile newFile = new MockMultipartFile("imgFile", "new.png", "image/png", "new-content".getBytes());
        List<MultipartFile> newFileList = List.of(newFile);

        // When: 새 이미지로 수정
        freeImgService.updateFreeImg(9L, deletedImgList, newFileList);

        // Then: 대표 이미지가 새 파일로 설정되었는지 확인
        FreeImgDto repImg = freeImgMapper.getRepImg(9L);
        assertThat(repImg).isNotNull();
        assertThat(repImg.getOriImgName()).isEqualTo("new.png");

        log.info("대표 이미지 변경 완료: {}", repImg.getImgUrl());
    }

    @Test
    @DisplayName("이미지 전체 조회 테스트")
    void testGetAllImages() {
        // Given
        Long freeId = 7L;

        // When
        List<FreeImgVO> imageList = freeImgService.getAll(freeId);

        // Then
        assertThat(imageList).isNotNull();
        imageList.forEach(img -> log.info("이미지: {}", img));
    }

    @Test
    @DisplayName("대표 이미지 조회 테스트")
    void testGetImage() {
        // Given
        Long freeImgId = 8L;

        // When
        FreeImgDto img = freeImgService.getRepImg(freeImgId);

        // Then
        if (img != null) {
            assertThat(img.getRepImgYn()).isEqualTo("Y");
            log.info("대표 이미지: {}", img);
        } else {
            log.info("대표 이미지가 없습니다.");
        }
    }


    @Test
    @DisplayName("이미지 삭제 테스트")
    void testDeleteImage() throws Exception {
        // Given: 이미지 먼저 등록
        MultipartFile mockFile = new MockMultipartFile(
                "imgFile",
                "delete.png",
                "image/png",
                "to be deleted".getBytes()
        );

        FreeImgVO imgVO = FreeImgVO.builder()
                .freeId(8L)
                .repImgYn("N")
                .build();

        freeImgService.saveFreeImg(imgVO, mockFile);
        Long freeImgId = imgVO.getFreeImgId();

        // When
        freeImgService.deleteFreeImg(freeImgId);

        // Then
        assertThat(freeImgMapper.getById(freeImgId)).isNull();
        log.info("이미지 삭제 성공: {}", freeImgId);
    }
}
