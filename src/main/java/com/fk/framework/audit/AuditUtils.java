package com.fk.framework.audit;


import com.fk.framework.audit.annotations.AuditModel;
import com.fk.framework.audit.annotations.AuditModelProperty;
import com.fk.framework.audit.beans.AuditVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * 审计追踪工具类
 */
@Slf4j
@SuppressWarnings("all")
public class AuditUtils {

    /**
     * 生成审计追踪日志
     *
     * 入库使用，可读性较差，阅读需转换
     * @param dest
     * @param target
     */
    public static String generateAuditLog(Object dest, Object target) {
        if (target == null) {
            log.error("AuditUtils.generateAuditLog target cannot be null");
            return "";
        }

        String returnStr = "";
        List<String> returnList = Lists.newArrayList();

        Class<?> tClazz = target.getClass();
        AuditModel tAnnotation = tClazz.getAnnotation(AuditModel.class);

        if (tAnnotation == null) {
            log.error("AuditUtils.generateAuditLog target need @AuditModel");
            return "";
        }

        if (dest != null) {
            Class<?> dClazz = dest.getClass();
            AuditModel dAnnotation = dClazz.getAnnotation(AuditModel.class);
            if (!dAnnotation.value().equals(tAnnotation.value())) {
                log.error("AuditUtils.generateAuditLog dest and target not the same object");
                return null;
            }
        }

        if (tAnnotation != null) {
            Field[] fields = tClazz.getDeclaredFields();

            for (Field field : fields) {
                AuditModelProperty auditModelProperty = field.getAnnotation(AuditModelProperty.class);
                List<String> list = Lists.newArrayList();

                if (auditModelProperty != null) {
                    String value = auditModelProperty.value();
                    String className = auditModelProperty.className();
                    boolean isPersistent = auditModelProperty.isPersistent();
                    boolean isUpdatable = auditModelProperty.isUpdatable();
                    field.setAccessible(true);

                    try {
                        String dStr = " ";
                        if (dest != null) {
                            Object dValue = field.get(dest);
                            dStr = getValueByField(className, dValue, auditModelProperty);
                        }

                        Object tValue = field.get(target);
                        String tStr = getValueByField(className, tValue, auditModelProperty);

                        dStr = StringUtils.isNotBlank(dStr) ? dStr.trim() : "";
                        tStr = StringUtils.isNotBlank(tStr) ? tStr.trim() : "";

                        if (!isUpdatable && StringUtils.isBlank(tStr)) {
                            tStr = dStr;
                        }

                        if (isPersistent) {
                            if (dStr.equals(tStr) && StringUtils.isBlank(dStr)) {
                                continue;
                            }
                            list.add(value);
                            list.add(dStr);
                            list.add(tStr);
                        } else if (!dStr.equals(tStr)) {
                            list.add(value);
                            list.add(dStr);
                            list.add(tStr);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (list != null && list.size() > 0) {
                    returnList.add(StringUtils.join(list, ":::"));
                }
            }
        }

        if (returnList != null && returnList.size() > 0) {
            returnStr = StringUtils.join(returnList, "+++");
        }
        return returnStr;
    }

    private static String getValueByField(String className, Object dValue, AuditModelProperty auditModelProperty) {
        String returnStr = " ";
        try {
            if (dValue != null) {
                String pattern = auditModelProperty.pattern();
                String[] include = auditModelProperty.include();

                if (dValue instanceof Date) {
                    if (StringUtils.isBlank(pattern)) {
                        pattern = "yyyy-MM-dd HH:mm:ss";
                    }

                    Date now = new Date();
                    Date dv = dValue != null ? (Date) dValue : now;
                    returnStr = DateFormatUtils.format(dv, pattern);
                } else if (dValue instanceof Boolean) {
                    Boolean dv = (Boolean) dValue;
                    returnStr = dv ? "是" : "否";
                } else if (dValue instanceof Integer || dValue instanceof Double || dValue instanceof String || dValue instanceof Long) {
                    returnStr = String.valueOf(dValue);
                } else if (StringUtils.isNotBlank(className)) {
                    if (dValue instanceof List) {
                        List<Object> dList = (List<Object>) dValue;

                        Class clazz = Class.forName(className);

                        List<String> dStrs = Lists.newArrayList();
                        List<String> tStrs = Lists.newArrayList();

                        if (dList != null && dList.size() > 0) {
                            for (Object obj : dList) {
                                if (obj != null) {
                                    if (clazz == Date.class) {
                                        Date date = (Date) obj;
                                        if (StringUtils.isBlank(pattern)) {
                                            pattern = "yyyy-MM-dd HH:mm:ss";
                                        }
                                        dStrs.add(DateFormatUtils.format(date, pattern));
                                    } else if (clazz == String.class || clazz == Integer.class || clazz == Double.class || clazz == Long.class) {
                                        dStrs.add(String.valueOf(obj));
                                    } else {
                                        List<String> oList = getValueByFields(className, obj, include);
                                        if (oList != null && oList.size() > 0) dStrs.add(StringUtils.join(oList, ","));
                                    }
                                }
                            }
                        }

                        returnStr = dStrs != null && dStrs.size() > 0 ? StringUtils.join(dStrs, ",") : "";
                    } else {
                        List<String> dList = getValueByFields(className, dValue, include);
                        returnStr = dList != null && dList.size() > 0 ? StringUtils.join(dList, ",") : "";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStr;
    }


    private static List<String> getValueByFields(String className, Object obj, String[] fields) throws NoSuchFieldException, ClassNotFoundException {
        List<String> list = Lists.newArrayList();

        Class<?> clazz = Class.forName(className);
        AuditModel dAnnotation = clazz.getAnnotation(AuditModel.class);
        if (dAnnotation != null) {
            for (String f : fields) {
                Field field = clazz.getDeclaredField(f);
                AuditModelProperty auditModelProperty = field.getAnnotation(AuditModelProperty.class);
                if (auditModelProperty != null) {

                    String vlaue = auditModelProperty.value();
                    String pattern = auditModelProperty.pattern();

                    field.setAccessible(true);
                    try {
                        Object val = field.get(obj);
                        if (val != null) {
                            if (val instanceof Date) {
                                if (StringUtils.isBlank(pattern)) {
                                    pattern = "yyyy-MM-dd HH:mm:ss";
                                }
                                Date v = (Date) val;
                                list.add(vlaue + ":" + DateFormatUtils.format(v, pattern));
                            } else if (val instanceof Boolean) {
                                Boolean v = (Boolean) val;
                                list.add(vlaue + ":" + (v ? "是" : "否"));
                            } else if (val instanceof Integer || val instanceof Double || val instanceof String || val instanceof Long) {
                                String v = String.valueOf(val);
                                list.add(vlaue + ":" + v);
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

    /**
     * 根据AuditLog 生成Vo 集合对象
     *
     * @param auditLog
     * @return
     */
    public static List<AuditVo> translateAuditLogToListAuditVo(String auditLog) {
        List<AuditVo> list = Lists.newArrayList();
        if (StringUtils.isNotBlank(auditLog)) {
            String[] splitStrings = auditLog.split("\\+++");
            for (String str : splitStrings) {
                String[] arrays = str.split(":::");
                if (arrays.length >= 3) {
                    list.add(AuditVo.builder().name(arrays[0]).source(arrays[1]).target(arrays[2]).build());
                }
            }
        }
        return list;
    }

    /**
     * 根据Audit Log 生成，可读性高
     *
     * @param auditLog
     * @return
     */
    public static String translateAuditLogToString(String auditLog) {
        List<String> list = Lists.newArrayList();
        if (StringUtils.isNotBlank(auditLog)) {
            String[] splitStrings = auditLog.split("\\+++");
            for (String str : splitStrings) {
                String[] strs = str.split(":::");
                if (strs.length >= 3) {
                    String s = strs[0] + ":'" + strs[1] + "','" + strs[2] + "'";
                    list.add(s);
                }
            }
        }
        return StringUtils.join(list, " ; ");
    }
}
