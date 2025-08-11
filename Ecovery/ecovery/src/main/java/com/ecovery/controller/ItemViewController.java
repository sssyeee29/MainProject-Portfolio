package com.ecovery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 * 에코마켓 View Controller
 * @author : sehui
 * @fileName : ItemViewController
 * @since : 250721
 * @history
 *  - 250721 | sehui | View 반환을 위해 컨트롤러 생성
 */


@Controller
@RequiredArgsConstructor
@RequestMapping("/eco")
public class ItemViewController {

    //상품 목록 페이지
    @GetMapping("/list")
    public String itemListPage(){

        return "eco/eco-market";
    }

    //상품 상세 페이지
    @GetMapping("/{itemId}")
    public String itemDtlPage(@PathVariable String itemId, Model model){

        model.addAttribute("itemId", itemId);

        return "eco/eco-market-detail";
    }

    //상품 등록 페이지
    @GetMapping("/new")
    public String itemFormPage(){
        return "eco/eco-market-register";
    }

    //상품 수정 페이지
    @GetMapping("/modify/{itemId}")
    public String itemModifyPage(@PathVariable String itemId, Model model){

        model.addAttribute("itemId", itemId);

        return "eco/eco-market-edit";
    }
}
