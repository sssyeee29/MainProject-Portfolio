package com.ecovery.mapper;

import com.ecovery.domain.DisposalHistoryVO;
import com.ecovery.domain.MemberVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.DisposalHistoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
/**
 * 대형폐기물 분류 이력 테이블(disposal_history)
 * @author : jihye Lee
 * @fileName : DisposalHistoryMapper
 * @since : 20250714
 */
@Mapper
public interface DisposalHistoryMapper {

    //사용자가 올린 지역구 정보 등
    public void insertDisposalHistory(DisposalHistoryVO disposalHistoryVO);

    //위에서 입력한 지역, 폐기물 정보 등을 이미지와 함께 불러옴.
    public DisposalHistoryDto findByDisposalHistoryId(Long disposalHistoryId);

    //관리자 페이지에서 볼 수 있는 모든 disposalhistory+img 내역
    public List<DisposalHistoryDto> findAllDisposalHistory(Criteria cri);

    //페이지당 존재하는 게시물 개수
    public int getTotalCount(Criteria cri);

    //마이페이지에서 볼 수 있는 해당 회원의 모든 disposalhistory+img 내역
    public List<DisposalHistoryDto> findDisposalHistoryWithImgByMemberId(Long memberId);

    public int updateDisposalHistory(DisposalHistoryVO disposalHistoryVO);
}
