package com.ecovery.mapper;

import com.ecovery.domain.DisposalHistoryImgVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 대형폐기물 이미지 테이블(disposal_history_img)
 * @author : jihye Lee
 * @fileName : DisposalHistoryImgMapper
 * @since : 20250714
 */
@Mapper
public interface DisposalHistoryImgMapper {

    //사용자가 올린 이미지 정보
    public void insertDisposalImg(DisposalHistoryImgVO disposalImgVO);

    //위에서 입력한 이미지를 disposalHistoryId를 통해 불러옴.
    public DisposalHistoryImgVO findDisposalImgByDisposalHistoryId(Long disposalHistoryId);

}
