package com.fk.framework.audit;

import com.fk.framework.audit.beans.AuditVo;
import com.google.common.collect.Lists;

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

        String auditLog = AuditUtils.audit(operator1, operator2);
//        System.out.println(auditLog);

        List<AuditVo> auditVoList = AuditUtils.translateAuditToList(auditLog);
        for (AuditVo v : auditVoList) {
            System.out.println(v.getName() + "," + v.getSource() + "," + v.getTarget());
        }
        String auditLog2 = AuditUtils.translateAuditToString(auditLog);
        System.out.println(auditLog2);
    }
}