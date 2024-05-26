package com.company.project.fx;

import com.company.project.controllers.PersonController;
import com.company.project.functionality.PasswordHasher;
import com.company.project.models.Person;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class Controller implements Initializable {

    @Autowired
    PersonController personController;

    @FXML
    public Label example2;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    public void changeTextExample() {
        example2.setText("BlaBlaBla");
    }

    @Bean
    String title() {
        return "Test title";
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        changeTextExample();
    }

    @FXML
    private void handleLoginButtonAction() {
        String Username = usernameInput.getText();
        String password = passwordInput.getText();
        String hashedPassword = PasswordHasher.hashPassword(password); // Utilizăm PasswordHasher
        Person person = personController.getPersonByUsername(Username);

        if (person.getPassword().equals(hashedPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid credentials");
            alert.showAndWait();
        }

        // Proceed to the main application
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Login successful!");
        alert.showAndWait();
    }
}
