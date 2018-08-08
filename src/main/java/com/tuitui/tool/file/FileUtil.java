package com.tuitui.tool.file;


import com.tuitui.tool.entity.FileInfoVO;
import com.tuitui.tool.enums.ApiResponseCode;
import com.tuitui.tool.exception.BizException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * @author liujianxue
 */
@Component
public class FileUtil extends FileUtils {

    private final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public FileInfoVO saveFile(String storePath,MultipartFile resource, String targetFileDir) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            if (resource == null) {
                return null;
            }
            String originalFilename = resource.getOriginalFilename();
            String newPath = getNewFilePath(originalFilename);
            File file = new File(storePath + File.separator + targetFileDir + newPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            in = new BufferedInputStream(resource.getInputStream());

            out = new BufferedOutputStream(new FileOutputStream(file));
            int fileByteSize = in.available();
            byte[] bytes = new byte[fileByteSize];
            in.read(bytes, 0, bytes.length);
            out.write(bytes, 0, bytes.length);
            out.flush();

            String fileType = null;
            if (file.getName().lastIndexOf(".") > 0) {
                fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            }

            return new FileInfoVO(originalFilename, file.getName(), newPath, fileByteSize, fileType, file);
        } catch (Exception e) {
            logger.error(e.getMessage(), "", "Save file error");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), "", "Close file input stream error");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), "", "Close file output stream error");
                }
            }
        }

        return null;
    }

    public FileInfoVO saveAndUpdateFile(String storePath,MultipartFile resource, String targetFileDir) {
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            if (resource == null) {
                return null;
            }
            String originalFilename = resource.getOriginalFilename();
            File file = new File(storePath + File.separator + targetFileDir + originalFilename);

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            in = new BufferedInputStream(resource.getInputStream());
            out = new BufferedOutputStream(new FileOutputStream(file));
            int fileByteSize = in.available();
            byte[] bytes = new byte[fileByteSize];
            in.read(bytes, 0, bytes.length);
            out.write(bytes, 0, bytes.length);
            out.flush();

            String fileType = null;
            if (file.getName().lastIndexOf(".") > 0) {
                fileType = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            }
            return new FileInfoVO(originalFilename, file.getName(), originalFilename, fileByteSize, fileType, file);
        } catch (Exception e) {
            logger.error(e.getMessage(), "", "Save and update file error");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), "", "Close input stream error");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), "", "Close output stream error");
                }
            }
        }
        return null;
    }

    public byte[] fileDownload(String storePath,String path) {
        return convertBytesByFilePath(storePath + path);
    }

    public static void deleteFile() {
    }

    public byte[] convertBytesByFilePath(String path) {
        File file = new File(path);
        BufferedInputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            int fileByteSize = inputStream.available();
            byte[] bytes = new byte[fileByteSize];
            inputStream.read(bytes, 0, bytes.length);
            return bytes;
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), "", "Convert bytes by file path error");
            BizException.fail(ApiResponseCode.DATA_NOT_EXIST, "pdf文件不存在！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String getNewFilePath(String originalFilename) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DAY_OF_MONTH);

        return year + File.separator + month + File.separator + date + File.separator
                + randomValue() + "_" + originalFilename;
    }

    private static String randomValue() {
        UUID uuid = UUID.randomUUID();
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();

        return ShortUrlUtils.encoding(Math.abs(least)) + ShortUrlUtils.encoding(Math.abs(most));
    }

    public static class ShortUrlUtils {

        private static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        public static String encoding(long num) {
            if (num < 1) {
                throw new RuntimeException("num must be greater than 0.");
            }
            StringBuilder sb = new StringBuilder();
            for (; num > 0; num /= 62) {
                sb.append(ALPHABET.charAt((int) (num % 62)));
            }
            return sb.toString();
        }

        public static long decoding(String str) {
            str = str.trim();
            if (str.length() < 1) {
                throw new RuntimeException("str must not be empty.");
            }
            long result = 0;
            for (int i = 0; i < str.length(); i++) {
                result += (long) (ALPHABET.indexOf(str.charAt(i)) * Math.pow(62, i));
            }
            return result;
        }
    }
}
