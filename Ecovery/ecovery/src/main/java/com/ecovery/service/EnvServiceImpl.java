package com.ecovery.service;

import com.ecovery.domain.EnvVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.EnvDto;
import com.ecovery.dto.EnvFormDto;
import com.ecovery.dto.EnvImgDto;
import com.ecovery.mapper.EnvImgMapper;
import com.ecovery.mapper.EnvMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 환경톡톡 게시글 서비스 구현 클래스
 * 게시글 CRUD 및 목록 조회(페이징)을 DTO로 처리하며, 내부에서는 EnvVO를 사용하여 EnvMapper와 연결
 * Service 계층에서 비즈니스 로직을 담당하며 컨트롤러에 결과를 반환
 * @author : yukyeong
 * @fileName : EnvServiceImpl
 * @since : 250715
 * @history
     - 250715 | yukyeong | EnvServiceImpl 클래스 최초 작성 (CRUD 구현)
     - 250716 | yukyeong | 게시글 목록 조회 (페이징 포함), 게시글 총 개수 조회, 조회수 증가 추가
     - 250717 | yukyeong | DTO ↔ VO 변환 메서드 추가
     - 250718 | yukyeong | DTO 기반 서비스로 전환
     - 250725 | yukyeong | 게시글 category 필드 DTO/VO 매핑 처리 추가
     - 250728 | yukyeong | 이미지 포함 게시글 등록/수정 기능 추가 (EnvFormDto, MultipartFile 활용), 이미지까지 함께 삭제되도록 추가
     - 250731 | yukyeong | 이미지 리스트를 별도 파라미터가 아닌 EnvFormDto 내부에서 처리하도록 수정
     - 250801 | yukyeong | register()에 본문 이미지 URL 등록 처리 추가
                           modify()에 본문 이미지 URL 등록 및 삭제 처리 추가
                           deleteContentImg(String imgUrl) 메서드 추가 (본문 이미지 개별 삭제용)
     - 250802 | yukyeong | remove() 삭제 로직 개선:
                           - DB 삭제 전에 첨부/본문 이미지 파일을 파일 시스템에서도 삭제하도록 추가
                           - imgName → 첨부 이미지, imgUrl → 본문 이미지로 분기하여 경로 생성
                           - FileService.deleteFile(fullPath) 통해 로컬 폴더에서도 삭제되도록 처리
     - 250805 | yukyeong | 본문 이미지 등록 시 중복 이미지 필터링 로직 추가 (envImgService.existsByImgUrlAndEnvId)
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class EnvServiceImpl implements EnvService {

    // EnvMapper는 DB 연동을 위한 MyBatis 인터페이스
    private final EnvMapper envMapper;

    // EnvImgService를 통해 이미지 등록/삭제를 위임 처리
    private final EnvImgService envImgService;

    private final FileService fileService;

    @Value("${uploadPath}")
    private String uploadPath;

    private final String envImgFolder = "env";

    // DTO를 VO로 변환하는 메서드 (DB 작업용으로 변환)
    private EnvVO dtoToVo(EnvDto envDto) {
        EnvVO env = new EnvVO();
        env.setEnvId(envDto.getEnvId());
        env.setTitle(envDto.getTitle());
        env.setContent(envDto.getContent());
        env.setMemberId(envDto.getMemberId());
        env.setCategory(envDto.getCategory()); // 카테고리 추가
        return env;
    }

    // VO를 DTO로 변환하는 메서드 (컨트롤러 또는 뷰로 전달용)
    private EnvDto voToDto(EnvVO env) {
        EnvDto envDto = new EnvDto();
        envDto.setEnvId(env.getEnvId());
        envDto.setTitle(env.getTitle());
        envDto.setContent(env.getContent());
        envDto.setMemberId(env.getMemberId());
        envDto.setCategory(env.getCategory()); // 카테고리 추가
        envDto.setNickname(env.getNickname());
        envDto.setViewCount(env.getViewCount());
        envDto.setCreatedAt(env.getCreatedAt());
        envDto.setUpdatedAt(env.getUpdatedAt());
        return envDto;
    }

    // 게시글 등록 (DTO → VO 변환 후 Mapper 호출)
    // EnvFormDto 내부의 EnvDto를 꺼내 VO로 변환 후 Mapper를 통해 DB에 저장
    // 등록된 게시글의 ID를 다시 DTO에 설정하고, 이미지가 있을 경우 함께 저장
    @Override
    @Transactional
    public void register(EnvFormDto envFormDto) throws Exception{
        log.info("register() - 게시글 등록 (이미지 포함)");

        // 1. 본문 내용 등록
        EnvDto envDto = envFormDto.getEnvDto(); // 클라이언트에서 전달받은 게시글 본문 DTO
        EnvVO env = dtoToVo(envDto); // DB 저장을 위한 VO로 변환
        envMapper.insert(env); // 게시글 DB 등록
        envDto.setEnvId(env.getEnvId()); // DB에서 생성된 ID를 DTO에 반영

        // 2. 이미지가 있는 경우 등록 처리
        List<MultipartFile> envImgFiles = envFormDto.getEnvImgFiles();
        if (envImgFiles != null) {
            for (MultipartFile envImgFile : envImgFiles) {
                if (!envImgFile.isEmpty()) {
                    EnvImgDto envImgDto = EnvImgDto.builder()
                            .envId(env.getEnvId())
                            .build();
                    envImgService.register(envImgDto, envImgFile);
                }
            }
        }

        // 3. 본문 이미지 URL 등록 처리
        List<String> contentImgUrls = envFormDto.getContentImgUrls(); // ★ FormDto에서 가져옴
        if (contentImgUrls != null && !contentImgUrls.isEmpty()) {
            for (String url : contentImgUrls) {
                EnvImgDto contentImgDto = EnvImgDto.builder()
                        .envId(env.getEnvId()) // 게시글 ID 설정
                        .imgUrl(url)
                        .build();
                envImgService.register(contentImgDto); // 본문 이미지 등록 (파일 없이 URL만)
            }
        }
    }

    // 게시글 단건 조회
    // 조회된 VO를 DTO로 변환하여 반환
    @Override
    public EnvDto get(Long envId) {
        log.info("get() - 게시글 단건 조회");
        EnvVO env = envMapper.read(envId);
        if (env == null) return null; // null 체크 추가
        return voToDto(env);
    }

    // 게시글 수정 (본문 수정 + 기존 이미지 삭제 + 새 이미지 등록)
    // 게시글 본문은 VO로 변환 후 업데이트, 삭제할 이미지 ID는 delete 처리, 새 파일은 등록 처리
    @Override
    @Transactional
    public boolean modify(EnvFormDto envFormDto) throws Exception{
        log.info("modify() - 게시글 수정 (이미지 포함)");

        // 1. 게시글 본문 내용 수정
        EnvDto envDto = envFormDto.getEnvDto(); // 클라이언트 전달 DTO
        EnvVO env = dtoToVo(envDto); // VO로 변환
        boolean updated = envMapper.update(env) == 1; // 수정 성공 여부 확인

        if (!updated) return false; // 실패 시 중단

        // 2. 삭제할 이미지 ID 목록이 있다면 반복 삭제
        List<Long> deleteImgIds = envFormDto.getDeleteImgIds();
        if (deleteImgIds != null) {
            for (Long imgId : deleteImgIds) {
                envImgService.deleteById(imgId); // 개별 이미지 삭제
            }
        }

        // 3. 새 이미지 파일이 있다면 추가 등록 처리
        List<MultipartFile> envImgFiles = envFormDto.getEnvImgFiles();
        if (envImgFiles != null) {
            for (MultipartFile envImgFile : envImgFiles) {
                if (!envImgFile.isEmpty()) {
                    EnvImgDto envImgDto = EnvImgDto.builder()
                            .envId(env.getEnvId())
                            .build();
                    envImgService.register(envImgDto, envImgFile);
                }
            }
        }

        // 4. 삭제할 본문 이미지 URL이 있다면 반복 삭제
        List<String> deleteContentImgUrls = envFormDto.getDeleteContentImgUrls();
        if (deleteContentImgUrls != null && !deleteContentImgUrls.isEmpty()) {
            for (String url : deleteContentImgUrls) {
                envImgService.deleteByImgUrl(url); // URL 기반 삭제
            }
        }

        // 5. 본문에 새로 추가된 이미지 URL 등록 처리
        List<String> contentImgUrls = envFormDto.getContentImgUrls();
        if (contentImgUrls != null && !contentImgUrls.isEmpty()) {
            for (String imgUrl : contentImgUrls) {
                if (imgUrl != null && !imgUrl.isBlank()) {
                    imgUrl = imgUrl.trim(); // 공백 제거

                    // 중복 등록 방지
                    boolean exists = envImgService.existsByImgUrlAndEnvId(imgUrl, env.getEnvId());
                    if (exists) {
                        log.info("이미 등록된 본문 이미지이므로 건너뜀: {}", imgUrl);
                        continue;
                    }

                    EnvImgDto imgDto = EnvImgDto.builder()
                            .envId(env.getEnvId())
                            .imgUrl(imgUrl)
                            .build();

                    envImgService.register(imgDto); // DB 등록
                }
            }
        }

        return true;
    }



    // 게시글 삭제
    // 전달받은 ID로 delete 쿼리 실행, 결과가 1이면 true 반환
    @Override
    @Transactional
    public boolean remove(Long envId) {
        log.info("remove() - 게시글 및 이미지 삭제 시작, envId = {}", envId);

        // 1. DB에서 해당 게시글의 모든 이미지 URL 조회 (본문 이미지 포함)
        List<EnvImgDto> imgList = envImgService.getListByEnvId(envId);

        // 2. 파일 시스템에서 모든 이미지 삭제 먼저 수행
        for (EnvImgDto img : imgList) {
            try {
                String imgUrl = img.getImgUrl();
                String imgName = img.getImgName();
                String basePath = uploadPath.endsWith("/") ? uploadPath : uploadPath + "/";
                String fullPath = basePath + envImgFolder + "/" + null;

                // 첨부 이미지
                if (imgName != null && !imgName.isBlank()) {
                    fullPath = basePath + envImgFolder + "/" + imgName;
                }
                // 본문 이미지
                else if (imgUrl != null && !imgUrl.isBlank()) {
                    String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
                    fullPath = basePath + envImgFolder + "/" + fileName;
                }

                fileService.deleteFile(fullPath);
                log.info("파일 삭제 완료: {}", fullPath);
            } catch (Exception e) {
                log.warn("이미지 파일 삭제 실패: {}", imgList, e);
            }
        }

        // 3. 이미지 DB 일괄 삭제
        envImgService.deleteByEnvId(envId); // 모든 레코드 삭제

        // 4. 게시글 삭제
        int deleted = envMapper.delete(envId);
        log.info("게시글 삭제 결과: {}", deleted);

        return deleted == 1;
    }

    // 게시글 목록 조회 (페이징 포함)
    // VO 목록을 DTO 목록으로 변환하여 반환
    @Override
    public List<EnvDto> getList(Criteria cri) {
        log.info("getList() - 게시글 목록 조회");
        List<EnvVO> envList = envMapper.getListWithPaging(cri);

        // Stream API로 VO 리스트 → DTO 리스트 변환
        return envList.stream()
                .map(this::voToDto)
                .collect(Collectors.toList());
    }

    // 전체 게시글 수 조회 (페이징 처리에 필요)
    @Override
    public int getTotal(Criteria cri) {
        log.info("getTotal() - 게시글 총 개수 조회");
        return envMapper.getTotalCount(cri);
    }

    // 게시글 조회수 증가
    // 주어진 ID에 해당하는 게시글의 viewCount +1 처리
    @Override
    public void increaseViewCount(Long envId){

        envMapper.updateViewCount(envId);
    }

    // 이미지 URL 삭제
    @Override
    public int deleteContentImg(String imgUrl) {
        return envImgService.deleteByImgUrl(imgUrl); // 내부적으로 trim 및 삭제 수행됨
    }
}