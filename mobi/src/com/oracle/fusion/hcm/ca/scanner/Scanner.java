package com.oracle.fusion.hcm.ca.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;

public class Scanner {

	private static Queue<InfoBean> list = new LinkedBlockingDeque<InfoBean>();
	private static String url = "http://vdisk.weibo.com/search/?type=&sortby=default&keyword=mobi&filetype=&page=1";

	public static void main(String[] args) {
		parseHTML();
		download();
	}

	private static void download() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS,
				new LinkedBlockingDeque<Runnable>());
		for (InfoBean info : list) {
			executor.execute(new Download(info));
		}
	}

	private static void parseHTML() {
		try {
			URL u = new URL(url);
			Document dom = Jsoup.parse(u, 10000);
			Elements tables = dom.select("#search_table");
			if (tables.isEmpty())
				return;
			Element table = tables.get(0);
			Elements trs = table.select("tr");
			for (Iterator<Element> it = trs.iterator(); it.hasNext();) {
				Element tr = it.next();
				Elements as = tr.select(".sort_name_detail a");
				if (as.isEmpty())
					continue;
				Element a = as.get(0);
				String title = a.attr("title");
				if (title == null || title.isEmpty() || !title.endsWith(".mobi"))
					continue;
				Elements downloads = tr.select(".search_table_action a");
				if (downloads.isEmpty())
					continue;
				Element download = downloads.get(0);
				String info = download.attr("data-info");
				InfoBean bean = JSON.parseObject(info, InfoBean.class);
				list.add(bean);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
