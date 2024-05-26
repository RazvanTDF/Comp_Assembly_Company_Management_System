package com.company.project;

import com.company.project.controllers.*;
import com.company.project.models.*;
import com.company.project.models.enums.Role;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.stream.Collectors;

import static com.company.project.models.enums.ProductType.DESKTOP_PC;

@EnableJpaRepositories
@SpringBootApplication
public class ProjectApplication extends Application {

    public static void main(String[] args) {
        launch();
    }

    private ProductController productController;
    private PersonController personController;
    private PurchaseOrderController purchaseOrderController;
    private PromotionController promotionController;

    private Person currentPerson;

    @Override
    public void start(Stage stage) throws Exception {
        var context = org.springframework.boot.SpringApplication.run(ProjectApplication.class);
        var fxml = new FXMLLoader(getClass().getResource("/Main.fxml"));

        var scene = new Scene(fxml.load());

        //asta aici este exemplu de cum iei din spring si pui aici
        String title = context.getBean("title", String.class);

        productController = context.getBean(ProductController.class);
        personController = context.getBean(PersonController.class);
        purchaseOrderController = context.getBean(PurchaseOrderController.class);
        promotionController = context.getBean(PromotionController.class);

        initial(stage);
    }


    public void initial(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Sistem de Management al Producției");

        // Ecranul de autentificare
        BorderPane loginPane = new BorderPane();
        GridPane loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(10, 10, 10, 10));
        loginGrid.setVgap(8);
        loginGrid.setHgap(10);

        // Câmpurile de autentificare
        Label UsernameLabel = new Label("Username:");
        GridPane.setConstraints(UsernameLabel, 0, 0);
        TextField UsernameInput = new TextField();
        GridPane.setConstraints(UsernameInput, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            String Username = UsernameInput.getText();
            String password = passwordInput.getText();
            currentPerson = personController.getPersonByUsername(Username);
            if (currentPerson.getPassword().equals(password)) {
                showMainScreen(primaryStage);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid credentials");
                alert.showAndWait();
            }
        });

        loginGrid.getChildren().addAll(UsernameLabel, UsernameInput, passwordLabel, passwordInput, loginButton);
        loginPane.setCenter(loginGrid);
        Scene loginScene = new Scene(loginPane, 400, 300);
        primaryStage.setScene(loginScene);
        primaryStage.show();

    }

    private void showMainScreen(Stage primaryStage) {
        // TabPane pentru a deține diferite secțiuni
        TabPane tabPane = new TabPane();

        // Tab-ul pentru Produse
        Tab productsTab = new Tab("Produse");
        VBox productsLayout = new VBox();
        productsLayout.getChildren().add(new Label("Catalog Produse:"));
        ListView<String> productList = new ListView<>();
        productsLayout.getChildren().add(productList);

        // Funcționalități pentru manager pentru produse
        if (currentPerson.getRole() == Role.SENIOR) {
            HBox addProductBox = new HBox();
            TextField productNameInput = new TextField();
            productNameInput.setPromptText("Nume produs");
            TextField productPriceInput = new TextField();
            productPriceInput.setPromptText("Preț");
            TextField productDescriptionInput = new TextField();
            productDescriptionInput.setPromptText("Descriere");
            TextField productRatingInput = new TextField();
            productRatingInput.setPromptText("Rating");
            ComboBox<String> productCategoryInput = new ComboBox<>();
            productCategoryInput.getItems().addAll("Desktop PC", "Laptop PC", "Imprimante", "Periferice", "Componente");
            productCategoryInput.setPromptText("Categorie");
            Button addProductButton = new Button("Adaugă Produs");

            addProductButton.setOnAction(e -> {
                String name = productNameInput.getText();
                double price = Double.parseDouble(productPriceInput.getText());
                String description = productDescriptionInput.getText();
                int rating = Integer.parseInt(productRatingInput.getText());
                String category = productCategoryInput.getValue();

                Product product = new Product();
                //name, price, description, rating, category
                product.setName(name);
                product.setPrice(price);
                product.setDescription(description);
                product.setStars(rating);
                product.setType(DESKTOP_PC);


                productController.addProduct(product);
                updateProductList(productList);
            });

            addProductBox.getChildren().addAll(productNameInput, productPriceInput, productDescriptionInput, productRatingInput, productCategoryInput, addProductButton);
            productsLayout.getChildren().add(addProductBox);
        }

        productsTab.setContent(productsLayout);

        // Tab-ul pentru Comenzi
        Tab ordersTab = new Tab("Comenzi");
        VBox ordersLayout = new VBox();
        ordersLayout.getChildren().add(new Label("Lista Comenzi:"));
        ListView<String> ordersList = new ListView<>();
        ordersLayout.getChildren().add(ordersList);
        // Funcționalități pentru manager și angajați
        ordersTab.setContent(ordersLayout);

        // Tab-ul pentru Servicii
        Tab servicesTab = new Tab("Servicii");
        VBox servicesLayout = new VBox();
        servicesLayout.getChildren().add(new Label("Lista Servicii:"));
        ListView<String> serviceList = new ListView<>();
        servicesLayout.getChildren().add(serviceList);
        // Funcționalități pentru manager și angajați
        servicesTab.setContent(servicesLayout);

        // Tab-ul pentru Promoții
        Tab promotionsTab = new Tab("Promoții");
        VBox promotionsLayout = new VBox();
        promotionsLayout.getChildren().add(new Label("Lista Promoții:"));
        ListView<String> promotionsList = new ListView<>();
        promotionsLayout.getChildren().add(promotionsList);
        // Funcționalități pentru manager
        if (currentPerson.getRole().equals(Role.SENIOR)) {
            HBox addPromotionBox = new HBox();
            TextField promoNameInput = new TextField();
            promoNameInput.setPromptText("Nume Promoție");
            ComboBox<Product> promoProductInput = new ComboBox<>();
            promoProductInput.getItems().addAll(productController.getAllProducts());
            promoProductInput.setPromptText("Produs");
            TextField promoDiscountInput = new TextField();
            promoDiscountInput.setPromptText("Discount (%)");
            Button addPromotionButton = new Button("Adaugă Promoție");
            addPromotionButton.setOnAction(e -> {
                String name = promoNameInput.getText();
                List<Product> products = List.of(promoProductInput.getValue());
                double discount = Double.parseDouble(promoDiscountInput.getText());
                Promotion promo = new Promotion();
                //name, products, discount
                promo.setName(name);
                promo.setProductIds(products.stream().map(Product::getId).collect(Collectors.toList()));
                promotionController.addPromotion(promo);

            });
            addPromotionBox.getChildren().addAll(promoNameInput, promoProductInput, promoDiscountInput, addPromotionButton);
            promotionsLayout.getChildren().add(addPromotionBox);
        }

        promotionsTab.setContent(promotionsLayout);

        // Tab-ul pentru Angajați
        if (currentPerson.getRole() == Role.SENIOR) {
            Tab employeesTab = new Tab("Angajați");
            VBox employeesLayout = new VBox();
            employeesLayout.getChildren().add(new Label("Lista Angajați:"));
            ListView<String> employeeList = new ListView<>();
            employeesLayout.getChildren().add(employeeList);
            // Funcționalități pentru manager
            HBox addEmployeeBox = new HBox();
            TextField employeeUsernameInput = new TextField();
            employeeUsernameInput.setPromptText("Username");
            PasswordField employeePasswordInput = new PasswordField();
            employeePasswordInput.setPromptText("Password");
            CheckBox isJuniorInput = new CheckBox("Junior");
            CheckBox isSeniorInput = new CheckBox("Senior");
            Button addEmployeeButton = new Button("Adaugă Angajat");
            addEmployeeButton.setOnAction(e -> {

                String username = employeeUsernameInput.getText();
                String password = employeePasswordInput.getText();
                boolean isJunior = isJuniorInput.isSelected();
                boolean isSenior = isSeniorInput.isSelected();

                Person employee = new Person();
                //Username, password, isManager, isSenior
                employee.setName(username);
                employee.setUsername(username);
                employee.setPassword(password);
                if (isJunior) {
                    employee.setRole(Role.JUNIOR);
                } else if (isSenior) {
                    employee.setRole(Role.SENIOR);
                }
                personController.addPerson(employee);
            });
            addEmployeeBox.getChildren().addAll(employeeUsernameInput, employeePasswordInput, isJuniorInput, isSeniorInput, addEmployeeButton);
            employeesLayout.getChildren().add(addEmployeeBox);
            employeesTab.setContent(employeesLayout);
            tabPane.getTabs().add(employeesTab);
        }

        // Adaugă tab-urile în tabPane
        tabPane.getTabs().addAll(productsTab, ordersTab, servicesTab, promotionsTab);

        // Scena principală
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(tabPane);
        Scene scene = new Scene(mainLayout, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void updateProductList(ListView<String> productList) {
        productList.getItems().clear();
        for (Product product : productController.getAllProducts()) {
            productList.getItems().add(product.getType().toString() + ": " + product.getName() + " - " + product.getPrice() + " Lei");
        }
    }

}
