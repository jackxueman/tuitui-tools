package com.tuitui.tool.entity;

import java.io.File;

/**
 * @author liujianxue
 */
public class FileInfoVO {
    private String originalFilename;
    private String newName;
    private String newPath;
    private Long fileSize;
    private String fileType;
    private File file;

    public FileInfoVO(String originalFilename, String newName, String newPath, long fileSize, String fileType, File file) {
        this.originalFilename = originalFilename;
        this.newName = newName;
        this.newPath = newPath;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.file = file;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getNewName() {
        return newName;
    }

    public String getNewPath() {
        return newPath;
    }

    public long getFileSize() {
        return fileSize != null ? this.fileSize : 0;
    }

    public String getFileType() {
        return fileType;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
