package com.fk.framework.barcode.beans;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class BarcodeReq {
    private String msg;
    private Double width;
    private Double height;
    private Boolean hideText = false;
    private String prefix;
    private String uploadPath;
    private String fileType = "png";
}
