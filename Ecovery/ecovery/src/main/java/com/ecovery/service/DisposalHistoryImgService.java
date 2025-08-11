package com.ecovery.service;

import com.ecovery.domain.DisposalHistoryImgVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DisposalHistoryImgService {

    //업로드 된 이미지 파일을 서버, db에 저장
    public DisposalHistoryImgVO saveDisposalImageFile(Long disposalHistoryId, MultipartFile disposalImgFile) throws Exception;

    //disposalHistoryId를 통해 이미지 데이터를 불러옴.
    public DisposalHistoryImgVO getDisposalImg(Long disposalHistoryId);

}
