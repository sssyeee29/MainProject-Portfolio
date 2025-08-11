package com.ecovery.controller;

import com.ecovery.constant.Role;
import com.ecovery.dto.*;
import com.ecovery.security.CustomUserDetails;
import com.ecovery.service.CategoryService;
import com.ecovery.service.ItemService;
import com.ecovery.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * 에코마켓 상품 Api Controller
 * @author : sehui
 * @fileName : ItemApiController
 * @since : 250711
 * @history
 *  - 250711 | sehui | 상품 상세 페이지 요청 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청 추가
 *  - 250715 | sehui | 상품 목록 페이지 요청에 단일 조건 검색 기능 추가
 *  - 250716 | sehui | 상품 등록 페이지 요청 추가
 *  - 250716 | sehui | 상품 등록 요청 추가
 *  - 250718 | sehui | 상품 수정 페이지 요청 추가
 *  - 250718 | sehui | 상품 수정 요청 추가
 *  - 250718 | sehui | REST API 방식으로 변경
 *  - 250718 | sehui | 상품 삭제 요청 추가
 *  - 250721 | sehui | 상품 등록, 수정, 삭제 요청에 관리자 권한 확인 기능 추가
 *  - 250724 | sehui | 상품 삭제 요청 제거
 *  - 250731 | sehui | 상품 상세 페이지 요청의 응답객체에 카테고리 목록 추가
 *  - 250731 | sehui | 상품 상세 페이지 요청 예외 처리 추가
 *  - 250731 | sehui | 상품 상세 페이지 관리자 권한 확인 코드 수정
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/eco")
@Slf4j
public class ItemApiController {

    private final ItemService itemService;
    private final MemberService memberService;
    private final CategoryService categoryService;

    //상품 목록 조회 요청
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> itemList(@RequestParam(required = false) String itemNm,
                                        @RequestParam(required = false) String category,
                                        Criteria cri) {

        //전체 상품 조회
        List<ItemListDto> itemList = itemService.getItemList(itemNm, category, cri);

        //전체 상품의 개수
        int totalCount = itemService.getTotalCount(itemNm, category);

        //응답 객체
        Map<String, Object> response = new HashMap<>();
        response.put("itemList", itemList);
        response.put("pageMaker", new PageDto(cri, totalCount));
        response.put("itemNm", itemNm);
        response.put("category", category);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //상품 상세 페이지 요청
    @GetMapping("/{itemId}")
    public ResponseEntity<?> itemDtl(@PathVariable Long itemId, Authentication auth) {

        try{
            Role role = null;

            //로그인한 사용자일 경우 권한 확인
            if(auth != null && auth.isAuthenticated()) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                String email = userDetails.getEmail();
                role = memberService.getMemberByEmail(email).getRole();
            }
            
            //상품 단건 조회
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);

            //응답 객체
            Map<String, Object> response = new HashMap<>();
            response.put("item", itemFormDto);
            response.put("role", role);     //비회원이면 null, 회원이면 권한(USER, ADMIN)
            response.put("categories", categoryService.findAllCategories());    //카테고리 목록

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (IllegalArgumentException e){
            log.error("상품 조회 중 오류 : {}",e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("알수 없는 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    //상품 등록 페이지 요청
    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> itemForm(Authentication auth) {

        //로그인한 사용자의 email 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getEmail();

        //관리자 권한 확인
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("itemFormDto", new ItemFormDto());         //기본 폼 객체
        response.put("categories", categoryService.findAllCategories());    //카테고리 목록

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //상품 등록 요청
    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> saveItem(@Valid @RequestPart("itemFormDto") ItemFormDto itemFormDto,
                                                        BindingResult bindingResult,
                                                        @RequestPart("itemImgFile") List<MultipartFile> itemImgFileList,
                                                        Authentication auth) {

        //로그인한 사용자의 email 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getEmail();

        //관리자 권한 확인
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        //유효성 검사 확인
        if(bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            Map<String, String> fieldErrors = new HashMap<>();

            for(FieldError fieldError : bindingResult.getFieldErrors()) {
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            }

            errorResponse.put("errorMessage", "입력값 오류");
            errorResponse.put("fieldErrors", fieldErrors);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        //대표 이미지 확인
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getItemId() == null){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errorMessage", "첫번째 상품 이미지는 필수 입력 값입니다.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        //상품 등록
        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        }catch (Exception e){
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errorMessage", "상품 등록 중 에러가 발생하였습니다.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    //상품 수정 페이지 요청
    @GetMapping("/modify/{itemId}")
    public ResponseEntity<Map<String, Object>> itemModify(@PathVariable Long itemId, Authentication auth) {

        //로그인한 사용자의 email 가져오기
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getEmail();

        //관리자 권한 확인
        Role role = memberService.getMemberByEmail(email).getRole();

        if(role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        Map<String, Object> response = new HashMap<>();

        try{
            //상품 단건 조회
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            response.put("item", itemFormDto);
            List<CategoryDto> categories = categoryService.findAllCategories();  // 전체 카테고리 목록 불러오기
            response.put("categories", categories);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }catch (Exception e){
            response.put("errorMessage", "상품을 조회할 수 없습니다.");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    //상품 수정 요청
    @PutMapping("/modify/{itemId}")
    public ResponseEntity<String> itemModify(@PathVariable Long itemId,
                                             @Valid @RequestPart("itemFormDto") ItemFormDto itemFormDto,
                                             BindingResult bindingResult,
                                             @RequestPart(value = "itemImgFile", required = false) List<MultipartFile> itemImgFileList,
                                             Authentication auth) {

        // 1. 로그인 사용자 정보 확인
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String email = userDetails.getEmail();

        // 2. 관리자 권한 확인
        Role role = memberService.getMemberByEmail(email).getRole();
        if (role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("관리자 권한이 필요합니다.");
        }

        // 3. 유효성 검사
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력값이 유효하지 않습니다.");
        }

        // 4. itemId를 DTO에 설정 (강제 일치)
        itemFormDto.setItemId(itemId);

        // 5. 대표 이미지 누락 체크 (기존 이미지 모두 삭제 후 새 이미지가 없으면 에러)
        boolean hasImagesToKeep = itemFormDto.getItemImgDtoList().stream()
                .anyMatch(dto -> !dto.isToBeDeleted());

        boolean hasNewImages = itemImgFileList != null && !itemImgFileList.isEmpty()
                && itemImgFileList.stream().anyMatch(file -> !file.isEmpty());

        if (!hasImagesToKeep && !hasNewImages) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("최소 한 개 이상의 상품 이미지를 유지 또는 추가해야 합니다.");
        }

        // 6. 상품 수정 처리
        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
            return ResponseEntity.ok("상품이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            e.printStackTrace(); // 또는 log.error(e)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상품 수정 중 오류가 발생했습니다.");
        }
    }

}
