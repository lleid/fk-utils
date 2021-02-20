package com.fk.framework.beans;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.servlet.http.HttpServletRequest;

@Data
@Accessors(chain = true)
public class UploadReq {
    private HttpServletRequest request;
    private String uploadPath;
    private String prefix;
    private String fileRequestNames;
}
