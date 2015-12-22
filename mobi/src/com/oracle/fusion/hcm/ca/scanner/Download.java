package com.oracle.fusion.hcm.ca.scanner;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class Download implements Runnable, Serializable {
	private InfoBean infoBean;

	public Download(InfoBean infoBean) {
		this.infoBean = infoBean;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void run() {
		if (infoBean == null)
			return;
		File file = new File("e:/mobi/" + infoBean.getFilename());
		try {
			FileUtils.copyURLToFile(new URL(infoBean.getUrl()), file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
