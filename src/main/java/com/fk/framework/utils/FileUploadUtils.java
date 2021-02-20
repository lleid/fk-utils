package com.fk.framework.utils;

import com.fk.framework.beans.UploadReq;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileUploadUtils {

    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * @param req
     * @return 相对路径
     */
    public static List<String> upload(UploadReq req) {
        List<String> photoUrls = Lists.newArrayList();

        HttpServletRequest request = req.getRequest();

        if (request instanceof MultipartHttpServletRequest) {
            List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles(req.getFileRequestNames());
            files.forEach(file -> {
                try {
                    byte[] bytes = file.getBytes();
                    List<String> fs = Lists.newArrayList();

                    fs.add(req.getUploadPath());
                    if (StringUtils.isNotBlank(req.getPrefix())) {
                        fs.add(req.getPrefix());
                    }
                    fs.add(sdf2.format(new Date()));
                    fs.add(file.getOriginalFilename());

                    String absoluteFilePath = StringUtils.join(fs, "/");

                    File f = new File(absoluteFilePath);
                    if (!f.getParentFile().exists()) {
                        f.mkdirs();
                    }

                    Path path = Paths.get(absoluteFilePath);
                    Files.write(path, bytes);

                    List<String> rfs = fs.subList(1, fs.size() - 1);
                    String relativeFilePath = StringUtils.join(rfs, "/");
                    photoUrls.add(relativeFilePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        return photoUrls;
    }
}
