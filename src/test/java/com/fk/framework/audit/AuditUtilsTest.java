package com.fk.framework.audit;

import com.fk.framework.audit.annotations.AuditModel;
import com.fk.framework.audit.annotations.AuditModelProperty;
import com.fk.framework.audit.beans.AuditVo;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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
        System.out.println("1" + " ".trim() + "1");

    }
}

@Getter
@Setter
@AuditModel(value = "operator")
class Operator {
    @AuditModelProperty(value = "姓名", isUpdatable = false)
    private String name;
    @AuditModelProperty(value = "年纪")
    private Integer age;
    @AuditModelProperty(value = "生日")
    private Date birthday;
    @AuditModelProperty(value = "是否男生")
    private Boolean isMan = false;
    @AuditModelProperty(value = "标签", className = "java.lang.String")
    private List<String> list;
    @AuditModelProperty(value = "下属", className = "com.fk.framework.audit.Operator", include = {"name"})
    private List<Operator> employee;
    @AuditModelProperty(value = "下属名称", className = "java.lang.String")
    public List<String> employeeNames;

    public List<String> getEmployeeNames() {
        List<String> list = getEmployee().stream().map(s -> s.getName()).collect(Collectors.toList());
        return list;
    }
}

