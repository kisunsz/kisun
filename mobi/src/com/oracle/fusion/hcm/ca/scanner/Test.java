package com.oracle.fusion.hcm.ca.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Test {

	public static void main(String[] args) throws UnknownHostException,
			IOException {
		Socket socket = new Socket("10.189.162.95", 11);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		while (true) {
			System.out.println("start send");
			String msg = reader.readLine();
			msg = msg.replace("\r\n", "").replace("\n", "").replace("\r", "");
			out.println(msg);
			out.flush();
			if (msg.equals("bye")) {
				break;
			}
			System.out.println("start receive");
			System.out.println(in.readLine());
		}
		socket.close();
	}
}