package com.fk.framework.files;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Map;

/**
 * PDF 根据模板生成PDF文件
 */
public class PdfExportUtils {

    /**
     * 导出 PDF
     *
     * @param templatePath 模板地址
     * @param targetPath   目标地址
     * @param properties   数据
     */
    public static void export(String templatePath, String targetPath, Map<String, String> properties) {
        export(templatePath, targetPath, properties, "");
    }

    /**
     * 导出 PDF
     *
     * @param templatePath 模板地址
     * @param targetPath   目标地址
     * @param properties   数据
     * @param tcc          字体
     */
    public static void export(String templatePath, String targetPath, Map<String, String> properties, String tcc) {
        export(templatePath, targetPath, properties, tcc, 1);
    }

    /**
     * 导出 PDF
     *
     * @param templatePath 模板地址
     * @param targetPath   目标地址
     * @param properties   数据
     * @param tcc          字体
     * @param pages        页数
     */
    public static void export(String templatePath, String targetPath, Map<String, String> properties, String tcc, Integer pages) {
        try {
            FileOutputStream outputStream = new FileOutputStream(targetPath);
            export(templatePath, outputStream, properties, tcc, 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出 PDF
     *
     * @param templatePath 模板地址
     * @param outputStream 输出流
     * @param properties   数据
     */
    public static void export(String templatePath, OutputStream outputStream, Map<String, String> properties) {
        export(templatePath, outputStream, properties, "");
    }

    /**
     * 导出 PDF
     *
     * @param templatePath 模板地址
     * @param outputStream 输出流
     * @param properties   数据
     * @param tcc          字体
     */
    public static void export(String templatePath, OutputStream outputStream, Map<String, String> properties, String tcc) {
        export(templatePath, outputStream, properties, tcc, 1);
    }


    /**
     * 导出 PDF
     *
     * @param templatePath 模板地址
     * @param outputStream 输出流
     * @param properties   数据
     * @param tcc          字体
     * @param pages        页数
     */
    public static void export(String templatePath, OutputStream outputStream, Map<String, String> properties, String tcc, Integer pages) {
        PdfReader reader = null;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            reader = new PdfReader(templatePath);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();

            if (StringUtils.isNotBlank(tcc)) {
                BaseFont bf = BaseFont.createFont(tcc, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                form.addSubstitutionFont(bf);
            }

            if (properties != null) {
                for (String key : properties.keySet()) {
                    String value = properties.get(key);
                    String nValue = value.toLowerCase();
                    if (nValue.endsWith(".jpg") || nValue.endsWith(".jpeg") || nValue.endsWith(".png")) {
                        addImages(form, stamper, key, value);
                    } else {
                        form.setField(key, value);
                    }
                }
            }

            stamper.setFormFlattening(true); // 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
            stamper.close();

            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, outputStream);
            doc.open();

            for (int i = 0; i < pages; i++) {
                PdfImportedPage importPage1 = copy.getImportedPage(new PdfReader(bos.toByteArray()), i + 1);
                copy.addPage(importPage1);
            }

            doc.close();
            reader.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            System.out.println(e);
        } finally {
            try {
                if (outputStream != null) outputStream.close();
                if (reader != null) reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addImages(AcroFields form, PdfStamper stamper, String key, String value) throws IOException, DocumentException {
        String imgPath = value;
        int pageNo = form.getFieldPositions(key).get(0).page;
        Rectangle signRect = form.getFieldPositions(key).get(0).position;
        float x = signRect.getLeft();
        float y = signRect.getBottom();

        Image image = Image.getInstance(imgPath);  //根据路径读取图片
        PdfContentByte under = stamper.getOverContent(pageNo);     //获取图片页面
        image.scaleToFit(signRect.getWidth(), signRect.getHeight()); //图片大小自适应
        image.setAbsolutePosition(x, y); //添加图片
        under.addImage(image);
    }
}
