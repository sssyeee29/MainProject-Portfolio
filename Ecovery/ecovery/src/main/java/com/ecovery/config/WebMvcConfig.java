package com.ecovery.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * 업로드한 파일 경로 설정 클래스
 * @author : sehui
 * @fileName : WebMvcConfig
 * @since : 250716
 * @history
 *  - 250716 | sehui | 업로드한 파일을 읽어올 경로 설정 기능 추가
 *  - 250730 | sehui | 에코마켓 상품 이미지가 저장된 폴더 접근 권한 추가
 *  - 250731 | yukyeong | 환경톡톡 이미지가 저장된 폴더 접근 권한 추가
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    //uploadPath = file:///C:/ecovery/
    @Value("${uploadPath}")
    String uploadPath;

    //"images/파일명"으로 접근하면 uploadPath에 있는 파일 제공
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
        registry.addResourceHandler("/ecovery/disposalImg/**")
                .addResourceLocations("file:///C:/ecovery/disposalImg/");
        registry.addResourceHandler("/ecovery/free/**")
                .addResourceLocations("file:///C:/ecovery/free/");
        registry.addResourceHandler("/ecovery/item/**")      //에코마켓 이미지 폴더 접근 권한
                .addResourceLocations("file:///C:/ecovery/item/");
        registry.addResourceHandler("/ecovery/env/**")
                .addResourceLocations("file:///C:/ecovery/env/");
    }
}
