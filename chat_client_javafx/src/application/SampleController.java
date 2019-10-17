package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class SampleController implements Initializable {
	@FXML
	private TextField tfServerIp, tfName;
	@FXML
	private Button btnConnect;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnConnect.setOnAction(event -> handleBtnConnect(event));
	} // initialize
	
	public void handleBtnConnect(ActionEvent event) {
		String serverIp = tfServerIp.getText().trim();
		String name = tfName.getText().trim();
		
		Main.getDataMap().put("serverIp", serverIp);
		Main.getDataMap().put("name", name);
		
		try {
			AnchorPane anchorPane = (AnchorPane) FXMLLoader.load(getClass().getResource("Chat.fxml"));
			StackPane stackPane = (StackPane) btnConnect.getScene().getRoot();
			stackPane.getChildren().add(anchorPane);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // handleBtnConnect method
	
}
