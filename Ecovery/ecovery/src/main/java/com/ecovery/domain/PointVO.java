package com.ecovery.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*
 * 회원의 포인트 VO
 * @author : sehui
 * @fileName : PointVO
 * @since : 250709
 */

@Getter
@Setter
public class PointVO {

    private Long pointId;
    private Long memberId;
    private int point;
    private Date lastModifiedAt;

}
