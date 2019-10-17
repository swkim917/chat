package com.exam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;

public class ChatServerTask implements Runnable {
	
	private Socket socket;
	private Map<String, BufferedWriter> map;
	
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;
	
	private String userId;

	public ChatServerTask(Socket socket, Map<String, BufferedWriter> map) {
		this.socket = socket;
		this.map = map;
		
		System.out.println(socket.getInetAddress() + "로부터 연결요청 받음.");
		
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
			
			userId = bufferedReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 스레드 공유 객체
		map.put(userId, bufferedWriter);
		
		System.out.println("접속한 클라이언트의 아이디는 " + userId + " 입니다.");
		broadcast(userId + " 님이 접속하셨습니다.");
	} // constructor



	@Override
	public void run() {
		String line = "";
		
		while (true) {
			try {
				line = bufferedReader.readLine();
			} catch (IOException e) {
				//e.printStackTrace();
				break;
			}
			
			if (line == null || line.equals("/quit")) {
				break;
			} else if (line.contains("/to")) {
				sendMessage(line);
			} else {
				broadcast(userId + " : " + line);
			}
		} // while
		
		// "/quit" 종료 명령어 수행시
		map.remove(userId);
		System.out.println(userId + "님이 나가셨습니다.");
		broadcast(userId + "님이 나가셨습니다.");
		
		// 입출력 객체 닫기
		close();
	} // run
	
	
	public void broadcast(String message) {
		for (BufferedWriter writer : map.values()) {
			try {
				writer.write(message + "\n");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // for
	} // broadcast method
	
	
	// 귓속말 형식: /to 아이디 대화내용
	public void sendMessage(String message) {
		// 아이디 문자열 가져오기
		int beginIndex = message.indexOf(" ") + 1;
		int endIndex = message.indexOf(" ", beginIndex);
		
		if (endIndex > -1) {
			String toId = message.substring(beginIndex, endIndex);
			String content = message.substring(endIndex + 1);
			
			BufferedWriter writer = map.get(toId);
			
			if (writer != null) {
				try {
					writer.write(userId + "님이 " + toId + "님께 귓속말을 보냈습니다: " + content + "\n");
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} // if
		} // if
	} // sendMessage method
	
	
	public void close() {
		if (bufferedReader != null) {
			try {
				bufferedReader.close();
				System.out.println("bufferedReader closed.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (bufferedWriter != null) {
			try {
				bufferedWriter.close();
				System.out.println("bufferedWriter closed.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (socket != null && !socket.isClosed()) {
			try {
				socket.close();
				System.out.println("socket closed.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} // close method

}
