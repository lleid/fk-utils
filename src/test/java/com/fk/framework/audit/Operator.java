package com.fk.framework.audit;

import com.fk.framework.audit.annotations.AuditModel;
import com.fk.framework.audit.annotations.AuditProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AuditModel
public class Operator {
    @AuditProperty(name = "姓名")
    private String name;
    @AuditProperty(name = "年纪")
    private Integer age;
    @AuditProperty(name = "生日")
    private Date birthday;
    @AuditProperty(name = "是否男生")
    private Boolean isMan = false;
    @AuditProperty(name = "标签", className = "java.lang.String")
    private List<String> list;
    @AuditProperty(name = "下属", className = "com.fk.framework.audit.Operator", include = {"name"})
    private List<Operator> employee;
}
