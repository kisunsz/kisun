package com.oracle.fusion.hcm.ca.scanner;

public class InfoBean {

	private String copy_ref;
	private String filename;
	private String uid;
	private String url;
	private boolean is_dir;
	private int weiboid;
	private String bytes;
	private String mime_type;
	private String type;
	public String getCopy_ref() {
		return copy_ref;
	}
	public void setCopy_ref(String copy_ref) {
		this.copy_ref = copy_ref;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public boolean isIs_dir() {
		return is_dir;
	}
	public void setIs_dir(boolean is_dir) {
		this.is_dir = is_dir;
	}
	public int getWeiboid() {
		return weiboid;
	}
	public void setWeiboid(int weiboid) {
		this.weiboid = weiboid;
	}
	public String getBytes() {
		return bytes;
	}
	public void setBytes(String bytes) {
		this.bytes = bytes;
	}
	public String getMime_type() {
		return mime_type;
	}
	public void setMime_type(String mime_type) {
		this.mime_type = mime_type;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
