package com.ecovery.service;

import com.ecovery.mapper.FreeImgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

/*
* 파일(이미지) 처리 서비스
* 이미지 업로드 및 삭제 기능을 담당하는 공통 유틸 서비스
* UUID를 이용해 파일명 중복을 방지하며, 서버에 이미지 저장
* 저장된 파일 삭제 기능 제공
* 게시판, 에코마켓, 대형폐기물 등 다양한 모듈에서 사용 가능
*
* @author : yeonsu
* @fileName : FileService
* @since : 250715
*/

@Service
@Slf4j
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception {

        // 1. UUID 생성 (파일명 중복 방지)
        UUID uuid = UUID.randomUUID();

        // 2. 원본 파일명에서 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        log.info("originalFileName.substring : {}", extension);

        // 3. 저장할 파일명 생성 (uuid + 확장자 조합)
        String savedFileName = uuid.toString() + extension;


        // 4. 전체 저장 경로 조합
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;

        // 5. 바이트 데이터를 해당 경로에 실제로 저장
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();

        // 6. 저장된 파일명 변환 (DB에 저장할 값)
        return savedFileName;
    }

    // 저장된 파일을 삭제하는 메소드
    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);

        if(deleteFile.exists()) {
            deleteFile.delete(); // 파일 존재시 삭제
            log.info("파일을 삭제하였습니다");
        }else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
