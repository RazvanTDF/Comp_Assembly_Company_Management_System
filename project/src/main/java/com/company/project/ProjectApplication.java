package com.company.project;

import com.company.project.controllers.*;
import com.company.project.models.*;
import com.company.project.models.enums.ProductType;
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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.stream.Collectors;

@EnableJpaRepositories
@SpringBootApplication
public class ProjectApplication extends Application {

    private ConfigurableApplicationContext context;
    private ProductController productController;
    private PersonController personController;
    private PurchaseOrderController purchaseOrderController;
    private PromotionController promotionController;

    private Person currentPerson;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        context = org.springframework.boot.SpringApplication.run(ProjectApplication.class);
        productController = context.getBean(ProductController.class);
        personController = context.getBean(PersonController.class);
        purchaseOrderController = context.getBean(PurchaseOrderController.class);
        promotionController = context.getBean(PromotionController.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initial(primaryStage);
    }

    @Override
    public void stop() throws Exception {
        context.close();
    }

    public void initial(Stage primaryStage) {
        primaryStage.setTitle("Sistem de Management al Producției");

        // Ecranul de autentificare
        BorderPane loginPane = new BorderPane();
        GridPane loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(10, 10, 10, 10));
        loginGrid.setVgap(8);
        loginGrid.setHgap(10);

        // Câmpurile de autentificare
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);
        TextField usernameInput = new TextField();
        GridPane.setConstraints(usernameInput, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);
        PasswordField passwordInput = new PasswordField();
        GridPane.setConstraints(passwordInput, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);
        loginButton.setOnAction(e -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            currentPerson = personController.getPersonByUsername(username);
            if (currentPerson != null && currentPerson.getPassword().equals(password)) {
                showMainScreen(primaryStage);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid credentials");
                alert.showAndWait();
            }
        });

        loginGrid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton);
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
        productsLayout.setPadding(new Insets(10));
        productsLayout.setSpacing(10);
        productsLayout.getChildren().add(new Label("Catalog Produse:"));
        ListView<String> productList = new ListView<>();
        updateProductList(productList);
        productsLayout.getChildren().add(productList);

        // Funcționalități pentru manager pentru produse
        if (currentPerson.getRole() == Role.SENIOR) {
            HBox addProductBox = new HBox(10);
            addProductBox.setPadding(new Insets(10));

            TextField productNameInput = new TextField();
            productNameInput.setPromptText("Nume produs");
            TextField productPriceInput = new TextField();
            productPriceInput.setPromptText("Preț");
            TextField productDescriptionInput = new TextField();
            productDescriptionInput.setPromptText("Descriere");
            TextField productRatingInput = new TextField();
            productRatingInput.setPromptText("Rating");
            ComboBox<ProductType> productCategoryInput = new ComboBox<>();
            productCategoryInput.getItems().addAll(ProductType.values());
            productCategoryInput.setPromptText("Categorie");
            Button addProductButton = new Button("Adaugă Produs");

            addProductButton.setOnAction(e -> {
                String name = productNameInput.getText();
                String priceText = productPriceInput.getText();
                String description = productDescriptionInput.getText();
                String ratingText = productRatingInput.getText();
                ProductType category = productCategoryInput.getValue();

                if (name.isEmpty() || priceText.isEmpty() || description.isEmpty() || ratingText.isEmpty() || category == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Toate câmpurile trebuie completate.");
                    alert.showAndWait();
                    return;
                }

                try {
                    double price = Double.parseDouble(priceText);
                    int rating = Integer.parseInt(ratingText);

                    Product product = new Product();
                    product.setName(name);
                    product.setPrice(price);
                    product.setDescription(description);
                    product.setStars(rating);
                    product.setType(category);

                    productController.addProduct(product);
                    updateProductList(productList);

                    productNameInput.clear();
                    productPriceInput.clear();
                    productDescriptionInput.clear();
                    productRatingInput.clear();
                    productCategoryInput.getSelectionModel().clearSelection();

                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Prețul și ratingul trebuie să fie numere valide.");
                    alert.showAndWait();
                }
            });

            addProductBox.getChildren().addAll(productNameInput, productPriceInput, productDescriptionInput, productRatingInput, productCategoryInput, addProductButton);
            productsLayout.getChildren().add(addProductBox);
        }

        productsTab.setContent(productsLayout);

        // Tab-ul pentru Comenzi
        Tab ordersTab = new Tab("Comenzi");
        VBox ordersLayout = new VBox();
        ordersLayout.setPadding(new Insets(10));
        ordersLayout.setSpacing(10);
        ordersLayout.getChildren().add(new Label("Lista Comenzi:"));
        ListView<String> ordersList = new ListView<>();
        // Implement orders list update method
        ordersLayout.getChildren().add(ordersList);
        ordersTab.setContent(ordersLayout);

        // Tab-ul pentru Servicii
        Tab servicesTab = new Tab("Servicii");
        VBox servicesLayout = new VBox();
        servicesLayout.setPadding(new Insets(10));
        servicesLayout.setSpacing(10);
        servicesLayout.getChildren().add(new Label("Lista Servicii:"));
        ListView<String> serviceList = new ListView<>();
        // Implement services list update method
        servicesLayout.getChildren().add(serviceList);
        servicesTab.setContent(servicesLayout);

        // Tab-ul pentru Promoții
        Tab promotionsTab = new Tab("Promoții");
        VBox promotionsLayout = new VBox();
        promotionsLayout.setPadding(new Insets(10));
        promotionsLayout.setSpacing(10);
        promotionsLayout.getChildren().add(new Label("Lista Promoții:"));
        ListView<String> promotionsList = new ListView<>();
        // Implement promotions list update method
        promotionsLayout.getChildren().add(promotionsList);

        promotionsTab.setContent(promotionsLayout);

        // Funcționalități pentru manager
        if (currentPerson.getRole() == Role.MANAGER) {
            HBox addPromotionBox = new HBox(10);
            addPromotionBox.setPadding(new Insets(10));

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
                Product product = promoProductInput.getValue();
                String discountText = promoDiscountInput.getText();

                if (name.isEmpty() || product == null || discountText.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Toate câmpurile trebuie completate.");
                    alert.showAndWait();
                    return;
                }

                try {
                    double discount = Double.parseDouble(discountText);

                    Promotion promo = new Promotion();
                    promo.setName(name);
                    promo.setProductIds(List.of(product.getId()));


                    promotionController.addPromotion(promo);

                    promoNameInput.clear();
                    promoProductInput.getSelectionModel().clearSelection();
                    promoDiscountInput.clear();

                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Discountul trebuie să fie un număr valid.");
                    alert.showAndWait();
                }
            });

            addPromotionBox.getChildren().addAll(promoNameInput, promoProductInput, promoDiscountInput, addPromotionButton);
            promotionsLayout.getChildren().add(addPromotionBox);
        }

        // Tab-ul pentru Angajați
        if (currentPerson.getRole() == Role.MANAGER) {
            Tab employeesTab = new Tab("Angajați");
            VBox employeesLayout = new VBox();
            employeesLayout.setPadding(new Insets(10));
            employeesLayout.setSpacing(10);
            employeesLayout.getChildren().add(new Label("Lista Angajați:"));
            ListView<String> employeeList = new ListView<>();
            // Implement employee list update method
            employeesLayout.getChildren().add(employeeList);

            HBox addEmployeeBox = new HBox(10);
            addEmployeeBox.setPadding(new Insets(10));

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

                if (username.isEmpty() || password.isEmpty() || (!isJunior && !isSenior)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Toate câmpurile trebuie completate și trebuie selectat un rol.");
                    alert.showAndWait();
                    return;
                }

                Person employee = new Person();
                employee.setUsername(username);
                employee.setPassword(password);
                employee.setRole(isJunior ? Role.JUNIOR : Role.SENIOR);

                personController.addPerson(employee);
                // Implement employee list update method

                employeeUsernameInput.clear();
                employeePasswordInput.clear();
                isJuniorInput.setSelected(false);
                isSeniorInput.setSelected(false);
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