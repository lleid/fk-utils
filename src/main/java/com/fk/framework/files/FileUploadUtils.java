package com.fk.framework.files;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

/**
 * 文件上传
 */
@SuppressWarnings("all")
public class FileUploadUtils {

    /**
     * 文件上传
     * @param request
     * @param uploadPath 上传路径
     */
    public static List<String> upload(HttpServletRequest request, String uploadPath) {
        List<String> list = Lists.newArrayList();
        if (request instanceof MultipartHttpServletRequest) {
            List<String> relativePaths = upload(request, uploadPath, null);
            list.addAll(relativePaths);
        }
        return list;
    }

    /**
     * 文件上传
     *
     * @param request
     * @param uploadPath 上传路径
     * @param prefix     前缀
     */
    public static List<String> upload(HttpServletRequest request, String uploadPath, String prefix) {
        List<String> list = Lists.newArrayList();
        if (request instanceof MultipartHttpServletRequest) {
            List<String> relativePaths = upload(request, uploadPath, prefix, "files");
            list.addAll(relativePaths);
        }
        return list;
    }

    /**
     * 文件上传
     *
     * @param request
     * @param filesName  上传文件名
     * @param uploadPath 上传路径
     * @param prefix     前缀
     */
    public static List<String> upload(HttpServletRequest request, String uploadPath, String prefix, String filesName) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles(filesName);
        List<String> relativeFilePaths = Lists.newArrayList();

        files.forEach(file -> {
            String relativeFilePath = upload(file, uploadPath, prefix);
            relativeFilePaths.add(relativeFilePath);
        });
        return relativeFilePaths;
    }

    /**
     * 文件上传
     *
     * @param request
     * @param filesName  上传文件名
     * @param uploadPath 上传路径
     * @param prefix     前缀
     */
    public static String upload(MultipartFile file, String uploadPath, String prefix) {
        try {
            String originFileName = file.getOriginalFilename();
            String[] nameArr = originFileName.split("\\.");
            String fileName = RandomStringUtils.randomAlphabetic(6) + "." + nameArr[nameArr.length - 1];

            List<String> fs = Lists.newArrayList();

            fs.add(uploadPath);
            if (StringUtils.isNotBlank(prefix)) {
                fs.add(prefix);
            }

            fs.add(DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
            fs.add(fileName);

            String absoluteFilePath = StringUtils.join(fs, File.separator);

            File f = new File(absoluteFilePath);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }

            Path path = Paths.get(absoluteFilePath);
            Files.write(path, file.getBytes());

            fs.remove(0);
            String relativeFilePath = StringUtils.join(fs, "/");
            return relativeFilePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
