package com.tuitui.tool.file;

import com.itextpdf.text.*;
import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.tuitui.tool.enums.ApiResponseCode;
import com.tuitui.tool.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;

/**
 * @author liujianxue
 */
@Component
public class PdfUtil {


    private final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private void doMerge(Document document, TreeMap<String, PdfReader> filesToMerge, OutputStream outputStream) throws IOException, DocumentException {
        Map<Integer, String> toc = new TreeMap<>();
        PdfCopy copy = new PdfCopy(document, outputStream);
        PdfCopy.PageStamp stamp;
        document.open();
        int n;
        int pageNo = 0;
        PdfImportedPage page;
        Chunk chunk;

        // 合并文件并添加目录
        for (Map.Entry<String, PdfReader> entry : filesToMerge.entrySet()) {
            n = entry.getValue().getNumberOfPages();
            toc.put(pageNo + 1, entry.getKey());
            for (int i = 0; i < n; ) {
                pageNo++;
                page = copy.getImportedPage(entry.getValue(), ++i);
                stamp = copy.createPageStamp(page);
                chunk = new Chunk(String.format("Page %d", pageNo));
                if (i == 1) {
                    chunk.setLocalDestination("p" + pageNo);
                }

                // A4纸大小
                ColumnText.showTextAligned(stamp.getUnderContent(),
                        Element.ALIGN_RIGHT, new Phrase(chunk),
                        559, 810, 0);
                stamp.alterContents();
                copy.addPage(page);
            }
        }

        document.close();
        for (PdfReader pdfReader : filesToMerge.values()) {
            pdfReader.close();
        }
    }

    /**
     * 合并多个pdf文件
     *
     * @param filePathList
     * @return
     */
    public File mergeFile(String tmpStorePath, List<String> filePathList) {
        Document document = new Document();
        TreeMap<String, PdfReader> filesToMerge = new TreeMap<>();
        FileOutputStream outputStream = null;

        try {
            for (String path : filePathList) {
                filesToMerge.put(path, new PdfReader(path));
            }

            File mergedFile = new File(tmpStorePath + UUID.randomUUID().toString() + ".pdf");
            if (!mergedFile.getParentFile().exists()) {
                mergedFile.getParentFile().mkdirs();
            }

            outputStream = new FileOutputStream(mergedFile);

            if (!mergedFile.exists()) {
                mergedFile.createNewFile();
            }

            doMerge(document, filesToMerge, outputStream);
            return mergedFile;
        } catch (Exception e) {
            logger.error(e.getMessage(), "", "Merge pdf file error");
        } finally {
            if (document.isOpen()) {
                document.close();
            }

            if (filesToMerge.size() > 0) {
                try {
                    for (PdfReader pdfReader : filesToMerge.values()) {
                        pdfReader.close();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), "", "Close pdfReader error");
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), "", "Close file output stream error");
                }
            }
        }
        return null;
    }

    /**
     * 合并接口文档
     *
     * @param hashKeyList
     * @return
     */
    public File mergeApiDocument(String tmpStorePath,String storePath, List<String> hashKeyList) {
        List<String> filePathList = new ArrayList<>();

        for (String filePath : hashKeyList) {
            filePathList.add(storePath + filePath);
        }

        return mergeFile(tmpStorePath,filePathList);
    }

    /**
     * 判断是否是加密的pdf文档
     *
     * @param pdfFile
     * @return
     */
    public Boolean isProtectedPdf(File pdfFile) {
        PdfReader pdfReader = null;
        try {
            pdfReader = new PdfReader(pdfFile.getPath());
            pdfReader.close();
            return false;
        } catch (BadPasswordException e) {
            return true;
        } catch (Exception e) {
            BizException.fail(ApiResponseCode.PDF_NOT, "不是pdf文件！");
            return null;
        } finally {
            if (pdfReader != null) {
                pdfReader.close();
            }
        }
    }
}
