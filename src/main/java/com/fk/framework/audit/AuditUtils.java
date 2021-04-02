package com.fk.framework.audit;


import com.fk.framework.audit.annotations.AuditModel;
import com.fk.framework.audit.annotations.AuditProperty;
import com.fk.framework.audit.beans.Operator;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * 审计追踪工具类
 */
@SuppressWarnings("all")
public class AuditUtils {

    /**
     * 比对 对象属性值
     *
     * @param dest
     * @param target
     */
    public static String audit(Object dest, Object target) {
        String returnStr = "";
        List<String> returnList = Lists.newArrayList();

        Class<?> dClazz = dest.getClass();
        Class<?> tClazz = target.getClass();

        AuditModel dAnnotation = dClazz.getAnnotation(AuditModel.class);
        AuditModel tAnnotation = tClazz.getAnnotation(AuditModel.class);

        if (dAnnotation != null && tAnnotation != null && dAnnotation.name().equals(tAnnotation.name())) {
            Field[] fields = dClazz.getDeclaredFields();

            for (Field field : fields) {
                AuditProperty auditProperty = field.getAnnotation(AuditProperty.class);
                List<String> list = Lists.newArrayList();

                if (auditProperty != null) {
                    String name = auditProperty.name();
                    String pattern = auditProperty.pattern();
                    String[] include = auditProperty.include();

                    field.setAccessible(true);

                    try {
                        Object dValue = field.get(dest);
                        Object tValue = field.get(target);

                        if (dValue instanceof Date) {
                            if (StringUtils.isBlank(pattern)) {
                                pattern = "yyyy-MM-dd HH:mm:ss";
                            }

                            Date now = new Date();
                            Date dv = dValue != null ? (Date) dValue : now;
                            Date tv = tValue != null ? (Date) tValue : now;

                            if (dv.compareTo(tv) != 0) {
                                list.add(name);
                                list.add(DateFormatUtils.format(dv, pattern));
                                list.add(DateFormatUtils.format(tv, pattern));
                            }
                        } else if (dValue instanceof Boolean) {
                            Boolean dv = (Boolean) dValue;
                            Boolean tv = (Boolean) tValue;

                            if (dv != tv) {
                                list.add(name);
                                list.add(dv ? "是" : "否");
                                list.add(tv ? "是" : "否");
                            }
                        } else if (dValue instanceof Integer || dValue instanceof Double || dValue instanceof String || dValue instanceof Long) {
                            String dv = String.valueOf(dValue);
                            String tv = String.valueOf(tValue);

                            if (!dv.equals(tv)) {
                                list.add(name);
                                list.add(dv);
                                list.add(tv);
                            }
                        } else if (dValue instanceof List) {
                            List<Object> dList = (List<Object>) dValue;
                            List<Object> tList = (List<Object>) tValue;

                            Type type = getActualType(dValue, 0);
                            List<String> dStrs = Lists.newArrayList();
                            List<String> tStrs = Lists.newArrayList();

                            if (dList != null && dList.size() > 0) {
                                for (Object obj : dList) {
                                    if (obj != null) {
                                        if (type.equals(Date.class) && obj != null) {
                                            Date date = (Date) obj;
                                            if (StringUtils.isBlank(pattern)) {
                                                pattern = "yyyy-MM-dd HH:mm:ss";
                                            }
                                            dStrs.add(DateFormatUtils.format(date, pattern));
                                        } else if (type.equals(String.class) || type.equals(Integer.class) || type.equals(Double.class) || type.equals(Long.class)) {
                                            dStrs.add(String.valueOf(obj));
                                        } else {
                                            List<String> oList = getFields(obj, include);
                                            if (oList != null && oList.size() > 0) dStrs.add(StringUtils.join(oList, ","));
                                        }
                                    }
                                }
                            }

                            if (dList != null && dList.size() > 0) {
                                for (Object obj : tList) {
                                    if (obj != null) {
                                        if (type.equals(Date.class)) {
                                            Date date = (Date) obj;
                                            if (StringUtils.isBlank(pattern)) {
                                                pattern = "yyyy-MM-dd HH:mm:ss";
                                            }
                                            tStrs.add(DateFormatUtils.format(date, pattern));
                                        } else if (type.equals(String.class) || type.equals(Integer.class) || type.equals(Double.class) || type.equals(Long.class)) {
                                            tStrs.add(String.valueOf(obj));
                                        } else {
                                            List<String> oList = getFields(obj, include);
                                            if (oList != null && oList.size() > 0) dStrs.add(StringUtils.join(oList, ","));
                                        }
                                    }
                                }
                            }

                            String dStr = dStrs != null && dStrs.size() > 0 ? StringUtils.join(dStrs, ",") : "";
                            String tStr = tStrs != null && tStrs.size() > 0 ? StringUtils.join(tStrs, ",") : "";

                            if (!dStr.equals(tStr)) {
                                list.add(name);
                                list.add(dStr);
                                list.add(tStr);
                            }
                        } else {
                            List<String> dList = getFields(dValue, include);
                            List<String> tList = getFields(dValue, include);

                            String dStr = dList != null && dList.size() > 0 ? StringUtils.join(dList, ",") : "";
                            String tStr = tList != null && tList.size() > 0 ? StringUtils.join(tList, ",") : "";

                            if (!dStr.equals(tStr)) {
                                list.add(name);
                                list.add(dStr);
                                list.add(tStr);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (list != null && list.size() > 0) {
                    returnList.add(StringUtils.join(list, "|||"));
                }
            }
        }

        if (returnList != null && returnList.size() > 0) {
            returnStr = StringUtils.join(returnList, "||||");
        }
        return returnStr;
    }

    public static Type getActualType(Object o, int index) {
        Type clazz = o.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) clazz;
        return pt.getActualTypeArguments()[index];
    }

    public static List<String> getFields(Object obj, String[] fields) throws NoSuchFieldException {
        List<String> list = Lists.newArrayList();

        Class<?> clazz = obj.getClass();
        AuditModel dAnnotation = clazz.getAnnotation(AuditModel.class);
        if (dAnnotation != null) {
            for (String f : fields) {
                Field field = clazz.getField(f);
                AuditProperty auditProperty = field.getAnnotation(AuditProperty.class);
                if (auditProperty != null) {

                    String name = auditProperty.name();
                    String pattern = auditProperty.pattern();

                    field.setAccessible(true);
                    try {
                        Object value = field.get(obj);
                        if (value != null) {
                            if (value instanceof Date) {
                                if (StringUtils.isBlank(pattern)) {
                                    pattern = "yyyy-MM-dd HH:mm:ss";
                                }
                                Date v = (Date) value;
                                list.add(name + ":" + DateFormatUtils.format(v, pattern));
                            } else if (value instanceof Boolean) {
                                Boolean v = (Boolean) value;
                                list.add(name + ":" + (v ? "是" : "否"));
                            } else if (value instanceof Integer || value instanceof Double || value instanceof String || value instanceof Long) {
                                String v = String.valueOf(value);
                                list.add(name + ":" + v);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        Operator operator1 = new Operator();
        operator1.setName("张三");
        operator1.setAge(10);
        Operator operator2 = new Operator();
        operator2.setName("李四");
        operator2.setAge(10);
        System.out.println(audit(operator1, operator2));
    }

}
