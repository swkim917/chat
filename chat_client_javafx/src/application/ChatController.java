package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController implements Initializable {
	@FXML
	private TextArea textArea;
	@FXML
	private TextField textField;
	@FXML
	private Button btnSend, btnExit;
	
	private static final int SERVER_PORT = 6000;
	private Socket socket;
	
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	
	String userId, line;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
		textField.setOnAction(event -> send());
		btnSend.setOnAction(event -> send());
		btnExit.setOnAction(event -> Platform.exit());
	} // initialize
	
	public void init() {
		String ipAddr = (String) Main.getDataMap().get("serverIp");
		userId = (String) Main.getDataMap().get("name");
		
		try {
			socket = new Socket(ipAddr, SERVER_PORT);
			
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"));
			
			Main.getDataMap().put("socket", socket);
			Main.getDataMap().put("bufferedReader", bufferedReader);
			Main.getDataMap().put("bufferedWriter", bufferedWriter);
			
			bufferedWriter.write(userId + "\n"); // 서버에게 전송
			bufferedWriter.flush();
			System.out.println("userId : " + userId);
			
			// 작업스레드
			Thread thread = new Thread(() -> receive());
			thread.setDaemon(true); // 데몬스레드
			thread.start();
			
			textField.requestFocus();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // init method
	
	
	public void receive() {
		line = "";
		
		while (true) {
			try {
				line = bufferedReader.readLine();
			} catch (IOException e) {
				//e.printStackTrace();
				break;
			}
			
			Platform.runLater(() -> {
				textArea.appendText(line + "\n");
				
				int pos = textArea.getCaretPosition();
				textArea.positionCaret(pos);
			});
		} // while
		
	} // receive method
	
	
	public void send() {
		String message = textField.getText().trim();
		
		try {
			bufferedWriter.write(message + "\n");
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
			textArea.setText(e.getMessage() + "\n");
		}
		
		textField.setText("");
		textField.requestFocus();
	} // send method
	
}
