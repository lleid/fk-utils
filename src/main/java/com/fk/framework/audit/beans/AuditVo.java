package com.fk.framework.audit.beans;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuditVo {
    /**
     * 名称
     */
    private String name;
    /**
     * 变更前
     */
    private String source;
    /**
     * 变更后
     */
    private String target;
}
