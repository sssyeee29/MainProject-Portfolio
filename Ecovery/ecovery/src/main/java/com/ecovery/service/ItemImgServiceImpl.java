package com.ecovery.service;


import com.ecovery.domain.ItemImgVO;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemImgDto;
import com.ecovery.mapper.ItemImgMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * 에코마켓 상품 이미지 ServiceImpl
 * @author : sehui
 * @fileName : ItemImgServiceImpl
 * @since : 250709
 * @history
 *  - 250716 | sehui | 상품 이미지 등록 기능 추가
 *  - 250717 | sehui | 상품 이미지 수정 기능 추가
 *  - 250724 | sehui | 상품 전체 이미지 조회 기능 추가
 *  - 250724 | sehui | 상품 이미지 단건 조회 기능 추가
 *  - 250724 | sehui | 대표 이미지 조회 Test 추가
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemImgServiceImpl implements ItemImgService {

    private final ItemImgMapper itemImgMapper;
    private final FileService fileService;

    @Value("C:/ecovery/item")
    private String ecoImgLocation;

    //상품 이미지 DB 등록
    @Override
    public void saveItemImg(ItemImgVO itemImgVO, MultipartFile itemImgFile) throws Exception {

        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(ecoImgLocation, oriImgName, itemImgFile.getBytes());
            imgUrl = "/ecovery/item/" + imgName;
        }

        //ItemImgVo에 값 저장
        itemImgVO.setOriImgName(oriImgName);
        itemImgVO.setImgName(imgName);
        itemImgVO.setImgUrl(imgUrl);

        //상품 이미지 정보 저장
        itemImgMapper.insertItemImg(itemImgVO);
        System.out.println("원본 이미지명 >> " + oriImgName);
        System.out.println("저장된 이미지명 >> " + imgName);
        System.out.println("DB 저장 itemId >> " + itemImgVO.getItemId());
        System.out.println("DB 저장 repImgYn >> " + itemImgVO.getRepImgYn());
    }

    //상품 이미지 수정
    //상품 이미지 수정
    @Override
    public void updateItemImg(Long itemId, List<ItemImgDto> itemImgDtoList, List<MultipartFile> newImgFiles) throws Exception {
        if (newImgFiles == null) {
            newImgFiles = new ArrayList<>();
        }

        // 1. 기존 이미지 전부 삭제
        itemImgMapper.deleteImagesByItemId(itemId); // <--- 이 코드를 그대로 유지합니다.

        // 2. 프론트에서 유지하기로 한 기존 이미지를 재등록
        for (ItemImgDto dto : itemImgDtoList) {
            ItemImgVO vo = new ItemImgVO();
            vo.setItemId(itemId);
            vo.setImgName(dto.getImgName());
            vo.setOriImgName(dto.getOriImgName());
            vo.setImgUrl(dto.getImgUrl());
            vo.setRepImgYn(dto.getRepImgYn());

            itemImgMapper.insertItemImg(vo);
        }

        // 3. 새로 추가된 이미지들 업로드 및 insert
        for (MultipartFile file : newImgFiles) {
            if (!file.isEmpty()) {
                String oriImgName = file.getOriginalFilename();
                String imgName = fileService.uploadFile(ecoImgLocation, oriImgName, file.getBytes());
                String imgUrl = "/ecovery/item/" + imgName;

                ItemImgVO vo = new ItemImgVO();
                vo.setItemId(itemId);
                vo.setImgName(imgName);
                vo.setOriImgName(oriImgName);
                vo.setImgUrl(imgUrl);
                vo.setRepImgYn("N");

                itemImgMapper.insertItemImg(vo);
            }
        }
        // 4. 최종 대표 이미지 확인 및 없으면 첫 번째 이미지 자동 지정
        List<ItemImgVO> allImages = itemImgMapper.getItemImgList(itemId);

        boolean hasRep = allImages.stream()
                .anyMatch(img -> "Y".equalsIgnoreCase(img.getRepImgYn()));

        if (!hasRep && !allImages.isEmpty()) {
            ItemImgVO first = allImages.get(0);
            itemImgMapper.updateRepImgYn(first.getItemImgId(), "Y");
        }
    }

    //상품 전체 이미지 조회
    @Override
    public List<ItemImgVO> getItemImgList(Long itemId) {
        return itemImgMapper.getItemImgList(itemId);
    }

    //상품 이미지 단건 조회
    @Override
    public ItemImgVO getItemImg(Long itemImgId) {
        return itemImgMapper.getItemImgById(itemImgId);
    }

    //대표 이미지 조회
    @Override
    public ItemImgVO getRepImgByItemId(Long itemId) {
        return itemImgMapper.findRepImgByItemId(itemId);
    }
}
