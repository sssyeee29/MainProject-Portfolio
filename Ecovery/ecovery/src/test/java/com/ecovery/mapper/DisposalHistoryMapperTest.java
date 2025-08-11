package com.ecovery.mapper;

import com.ecovery.dto.Criteria;
import com.ecovery.dto.DisposalHistoryDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class DisposalHistoryMapperTest {

    @Autowired
    private DisposalHistoryMapper mapper;

    @Test
    public void testRead() {
        Long targetId=57L;

        DisposalHistoryDto dto = mapper.findByDisposalHistoryId(targetId);

        if(dto!=null) {
            log.info("조회 결과 : {}", dto.toString());
        }else {
            log.info("조회 실패했습니다.");
        }
    }

    @Test
    public void testReadByMemberId() {
        Long targetId=1L;

        List<DisposalHistoryDto> dto = mapper.findDisposalHistoryWithImgByMemberId(targetId);

        if(dto!=null) {
            log.info("조회 결과 : {}", dto.toString());
        }else {
            log.info("조회 실패했습니다.");
        }
    }

    @Test
    public void testReadAll() {
        Criteria cri = new Criteria();
        cri.setPageNum(1); // 1페이지
        cri.setAmount(3);  // 페이지당 5개

        int total = mapper.getTotalCount(cri);

        List<DisposalHistoryDto> dtos = mapper.findAllDisposalHistory(cri);

        for(DisposalHistoryDto dto : dtos) {
            log.info("<UNK> <UNK> : {}", dto.toString());
        }
        /*if(dtos!=null) {
            log.info("조회 결과 : {}", dtos.toString());
        }else {
            log.info("조회 실패했습니다.");
        }*/
    }
}
