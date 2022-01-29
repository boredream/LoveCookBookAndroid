package com.boredream.baseapplication.entity.dto;


/**
 * 文件上传凭证
 */
public class FileUploadPolicyDTO {

	// 口令
    protected String token;

	// 上传地址
    protected String uploadHost;

	// 下载地址
    protected String downloadHost;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUploadHost() {
        return uploadHost;
    }

    public void setUploadHost(String uploadHost) {
        this.uploadHost = uploadHost;
    }

    public String getDownloadHost() {
        return downloadHost;
    }

    public void setDownloadHost(String downloadHost) {
        this.downloadHost = downloadHost;
    }
}
