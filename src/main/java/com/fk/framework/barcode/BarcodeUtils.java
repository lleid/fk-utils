package com.fk.framework.barcode;

import com.fk.framework.barcode.beans.BarcodeReq;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@SuppressWarnings("all")
public class BarcodeUtils {

    /**
     * @param req
     * @return
     */
    public static void generateFile(BarcodeReq req) {
        String uploadPath = req.getUploadPath();

        if (StringUtils.isNotBlank(uploadPath)) {
            List<String> fs = Lists.newArrayList();

            fs.add(req.getUploadPath());
            if (StringUtils.isNotBlank(req.getPrefix())) {
                fs.add(req.getPrefix());
            }
//            fs.add(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
//            fs.add;
//
//            String absoluteFilePath = StringUtils.join(fs, "/");
//
//            File f = new File(absoluteFilePath);
//            if (!f.getParentFile().exists()) {
//                f.mkdirs();
//            }
//            File file = new File(path);
//            try {
//                generate(msg, new FileOutputStream(file));
//            } catch (FileNotFoundException e) {
//                throw new RuntimeException(e);
//            }
        }
    }

    /**
     * 生成字节
     *
     * @param msg
     * @return
     */
    public static byte[] generate(String msg) {
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        generate(msg, ous);
        return ous.toByteArray();
    }

    /**
     * 生成到流
     *
     * @param msg
     * @param ous
     */
    public static void generate(String msg, OutputStream ous) {
        if (StringUtils.isEmpty(msg) || ous == null) {
            return;
        }

        Code39Bean bean = new Code39Bean();

        // 精细度
        final int dpi = 150;
        // module宽度
        final double moduleWidth = UnitConv.in2mm(1.0f / dpi);

        // 配置对象
        bean.setModuleWidth(moduleWidth);
        bean.setWideFactor(3);
        bean.doQuietZone(false);

        String format = "image/png";
        try {

            // 输出到流
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(ous, format, dpi,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);

            // 生成条形码
            bean.generateBarcode(canvas, msg);

            // 结束绘制
            canvas.finish();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
