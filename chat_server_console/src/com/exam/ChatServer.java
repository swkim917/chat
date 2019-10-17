package com.exam;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

	public static void main(String[] args) {
		// 스레드간 공유하는 동기화 Map 객체. key는 아이디(채팅명), value는 출력문자스트림.
		Map<String, BufferedWriter> map = new ConcurrentHashMap<String, BufferedWriter>();

		int port = 6000; // 포트번호
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(port);

			System.out.println("*** 채팅 서버 ***");
			System.out.println("서버는 클라이언트 소켓의 접속 요청을 기다리고 있음");

			while (true) {
				socket = serverSocket.accept();

				ChatServerTask task = new ChatServerTask(socket, map);
				Thread thread = new Thread(task);
				thread.setDaemon(true);
				thread.start();
			} // while
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null && !serverSocket.isClosed()) {
				try {
					serverSocket.close();
					System.out.println("serverSocket closed.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	} // main method

}
