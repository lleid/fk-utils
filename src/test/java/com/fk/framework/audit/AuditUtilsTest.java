package com.fk.framework.audit;

import com.fk.framework.audit.annotations.AuditModel;
import com.fk.framework.audit.annotations.AuditModelProperty;
import com.fk.framework.audit.beans.AuditVo;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;


public class AuditUtilsTest {

    public static void main(String[] args) {
        Operator operator1 = new Operator();
        operator1.setName("张三");
        operator1.setAge(10);

        List<String> list = Lists.newArrayList();
        list.add("1");
        list.add("2");
        list.add("3");
        operator1.setList(list);
        operator1.setIsMan(true);

        operator1.setBirthday(new Date());
        Operator operator2 = new Operator();
        operator2.setName("李四");
        operator2.setAge(10);
        operator2.setEmployee(Lists.newArrayList(operator1));

        operator1.setEmployee(Lists.newArrayList(operator2));

        String auditLog = AuditUtils.generateAuditLog(null, operator2);
//        System.out.println(auditLog);

        List<AuditVo> auditVoList = AuditUtils.translateAuditLogToListAuditVo(auditLog);
        for (AuditVo v : auditVoList) {
            System.out.println(v.getName() + "," + v.getSource() + "," + v.getTarget());
        }
        String auditLog2 = AuditUtils.translateAuditLogToString(auditLog);
        System.out.println(auditLog2);
    }
}

@Getter
@Setter
@AuditModel
class Operator {
    @AuditModelProperty(name = "姓名")
    private String name;
    @AuditModelProperty(name = "年纪")
    private Integer age;
    @AuditModelProperty(name = "生日")
    private Date birthday;
    @AuditModelProperty(name = "是否男生")
    private Boolean isMan = false;
    @AuditModelProperty(name = "标签", className = "java.lang.String")
    private List<String> list;
    @AuditModelProperty(name = "下属", className = "com.fk.framework.audit.Operator", include = {"name"})
    private List<com.fk.framework.audit.Operator> employee;
}

