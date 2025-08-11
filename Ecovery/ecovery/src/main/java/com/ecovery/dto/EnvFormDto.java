package com.ecovery.dto;

import jakarta.validation.Valid;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 환경톡톡 게시글 등록/수정용 Form DTO
 * - 게시글 정보(EnvDto), 첨부 이미지 파일, 삭제할 이미지 ID 및 본문 이미지 정보 등을 함께 전달
 * - 등록 및 수정 공통 폼 역할 수행
 *
 * @author : yukyeong
 * @fileName : EnvFormDto
 * @since : 250728
 * @history
     - 250728 | yukyeong | 게시글 등록 및 수정용 EnvFormDto 생성 (EnvDto + 이미지 리스트)
     - 250730 | yukyeong | EnvDto만으로 초기화 가능한 생성자 추가, envImgFiles 초기화(null 방지) 포함
 *   - 250801 | yukyeong | contentImgUrls 필드 추가 - 본문 이미지 src 정보 저장용
 *                        deleteContentImgUrls 필드 추가 - 본문 이미지 삭제 요청용
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvFormDto {

    @Valid
    private EnvDto envDto; // 게시글 본문 정보 (제목, 내용, 카테고리 등 포함)

    // 첨부 이미지 수정 시 삭제 대상 이미지 ID 리스트
    private List<Long> deleteImgIds = new ArrayList<>();

    // 새로 추가될 첨부 이미지 파일 리스트
    private List<MultipartFile> envImgFiles = new ArrayList<>();

    public EnvFormDto(EnvDto envDto) {
        this.envDto = envDto;
        this.envImgFiles = new ArrayList<>(); // null 방지 초기화
    }

    // 본문에 삽입된 이미지(src) 목록 - DB 저장 또는 중복 등록 방지를 위해 사용
    private List<String> contentImgUrls = new ArrayList<>();

    // 수정 시 본문에서 제거된 이미지(src) 목록 - 파일 시스템 및 DB에서 삭제 처리에 사용
    private List<String> deleteContentImgUrls = new ArrayList<>();
}
