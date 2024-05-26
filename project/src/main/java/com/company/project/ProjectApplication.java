package com.company.project;

import com.company.project.controllers.*;
import com.company.project.models.*;
import com.company.project.models.enums.OrderStatus;
import com.company.project.models.enums.OrderType;
import com.company.project.models.enums.ProductType;
import com.company.project.models.enums.Role;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.Arrays;
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

    private Stage mainStage;

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
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        currentPerson = Person.fetchDefaultUser();
        paginaUnu();
    }

    @Override
    public void stop() throws Exception {
        context.close();
    }


    public void initial(Stage primaryStage) {
        primaryStage.setTitle("Sistem de Management al Producției");

        // Ecranul de autentificare
        BorderPane loginPane = new BorderPane();

        Scene loginScene = new Scene(loginPane, 400, 300);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private void paginaUnu() {
        switch (currentPerson.getRole()) {
            case UNREGISTERED -> {
                displayAnonUser();
            }
            case MANAGER -> {
                displayManager();
            }
            case SENIOR -> {
                displaySenior();
            }
            case JUNIOR -> {
                displayJunior();
            }
            case CLIENT -> {
                displayClient();
            }
        }
    }

    private void displayAnonUser() {
        TabPane tabPane = new TabPane();

        //product tab
        tabPane.getTabs().add(createProductTab());
        tabPane.getTabs().add(createServicesTab());
        tabPane.getTabs().add(createLoginTab());

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(tabPane);
        Scene scene = new Scene(mainLayout, 800, 600);

        mainStage.setScene(scene);
        mainStage.show();
    }

    private void displayManager() {
        TabPane tabPane = new TabPane();

        //product tab
        tabPane.getTabs().add(createProductTab());
        tabPane.getTabs().add(createServicesTab());
        tabPane.getTabs().add(createOrdersTab());

        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(tabPane);
        Scene scene = new Scene(mainLayout, 800, 600);

        mainStage.setScene(scene);
        mainStage.show();
    }

    private void displaySenior() {
    }

    private void displayJunior() {
    }

    private void displayClient() {

    }

    private Tab createLoginTab() {
        Tab tab = new Tab("Login");

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
                paginaUnu();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid credentials");
                alert.showAndWait();
            }
        });

        loginGrid.getChildren().addAll(usernameLabel, usernameInput, passwordLabel, passwordInput, loginButton);
        loginPane.setCenter(loginGrid);

        tab.setContent(loginPane);

        return tab;
    }


    private Tab createProductTab() {
        // Tab-ul pentru Produse
        Tab productsTab = new Tab("Produse");
        VBox productsLayout = new VBox();
        productsLayout.setPadding(new Insets(10));
        productsLayout.setSpacing(10);
        productsLayout.getChildren().add(new Label("Catalog Produse:"));
        ListView<String> productList = new ListView<>();
        productList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        updateProductList(productList);
        productsLayout.getChildren().add(productList);

        if (!currentPerson.getRole().equals(Role.UNREGISTERED)) {
            VBox createOrderLayout = new VBox();
            createOrderLayout.setPadding(new Insets(10));
            createOrderLayout.setSpacing(10);

            HBox createOrderBox = new HBox(10);
            createOrderBox.setPadding(new Insets(10));

            Button createOrderButton = new Button("Creaza comanda");

            createOrderButton.setOnAction(e -> {
                var selected = productList.getSelectionModel().getSelectedItems();

                var products = selected.stream().map(it -> {
                    var idIndex = it.indexOf(":");
                    var idString = it.substring(0, idIndex);
                    long id = Long.parseLong(idString);
                    return productController.getProductById(id);
                }).toList();

                System.out.println("Hello");
                var order = new PurchaseOrder();
                order.setStatus(OrderStatus.PENDING);
                order.setPerson(currentPerson);
                order.setType(OrderType.BUY);
                order.setProducts(products);

                System.out.println("world");
                System.out.println("Order is " + order.getId() + " type " + order.getType());

                try {
                    purchaseOrderController.addOrder(order);
                } catch (Exception x) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(x.getLocalizedMessage());
                    alert.showAndWait();
                }

                selected.forEach(it -> System.out.println("Selected " + it));
            });

            createOrderBox.getChildren().addAll(createOrderButton);
            createOrderLayout.getChildren().add(createOrderBox);

            productsLayout.getChildren().add(createOrderLayout);
        }

        productsTab.setContent(productsLayout);

        return productsTab;
    }


    private Tab createOrdersTab() {
        // Tab-ul pentru Comenzi

        Tab ordersTab = new Tab("Comenzi");
        SplitPane splitPane = new SplitPane();

        VBox leftControl = new VBox(new Label("Left Control"));
        VBox rightControl = new VBox(new Label("Right Control"));

        splitPane.getItems().addAll(leftControl, rightControl);

        leftControl.setPadding(new Insets(10));
        leftControl.setSpacing(10);
        leftControl.getChildren().add(new Label("Lista Comenzi:"));
        ListView<String> ordersList = new ListView<>();
        updateOrdersList(ordersList);
        leftControl.getChildren().add(ordersList);
        ordersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        Button selectOrder = new Button("Selecteaza comanda");
        leftControl.getChildren().add(selectOrder);

        selectOrder.setOnAction(e -> {
            rightControl.getChildren().clear();

            var selectedOrder = ordersList.getSelectionModel().getSelectedItems().getFirst();
            var idIndex = selectedOrder.indexOf(":");
            var idString = selectedOrder.substring(0, idIndex);
            long orderId = Long.parseLong(idString);

            rightControl.setPadding(new Insets(10));
            rightControl.setSpacing(10);
            rightControl.getChildren().add(new Label("Produse"));
            ListView<String> productList = new ListView<>();
            updateProductListForOrder(productList, orderId);
            rightControl.getChildren().add(productList);

            VBox setStatusLayout = new VBox();
            setStatusLayout.setPadding(new Insets(10));
            setStatusLayout.setSpacing(10);

            HBox setStatusBox = new HBox(10);
            setStatusBox.setPadding(new Insets(10));

            CheckBox status_pending = new CheckBox("Pending");

            //Asta trebuia sa fie luat din PurchaseOrder
            status_pending.setSelected(true);

            CheckBox status_processing = new CheckBox("Processing");
            CheckBox status_canceled = new CheckBox("Cancel");
            CheckBox status_complete = new CheckBox("Complete");
            Button setStatusButton = new Button("Seteaza status");

            setStatusButton.setOnAction(x -> {
                boolean is_pending = status_pending.isSelected();
                boolean is_processing = status_processing.isSelected();
                boolean is_complete = status_complete.isSelected();
                boolean is_canceled = status_canceled.isSelected();

                var statusList = Arrays.asList(is_pending, is_canceled, is_complete, is_processing);

                var selected = statusList.stream().filter(status -> status.equals(true)).toList();
                if (selected.size() == 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Nici un status nu a fost selectat");
                    alert.showAndWait();
                } else if (selected.size() > 1) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Selectati un singur status");
                    alert.showAndWait();
                } else {
                    //TODO pruchaseOrder.setStatus(selected.selected.getFirst())
                }
            });

            setStatusBox.getChildren().addAll(status_pending, status_processing, status_canceled, status_complete, setStatusButton);
            setStatusLayout.getChildren().add(setStatusBox);

            rightControl.getChildren().add(setStatusLayout);

        });

        ordersTab.setContent(splitPane);
        return ordersTab;
    }


    private Tab createClientOrdersTab() {
        // Tab-ul pentru Comenzi
        Tab ordersTab = new Tab("Comenzi");
        VBox ordersLayout = new VBox();
        ordersLayout.setPadding(new Insets(10));
        ordersLayout.setSpacing(10);
        ordersLayout.getChildren().add(new Label("Lista Comenzi:"));
        ListView<String> ordersList = new ListView<>();
        updateClientOrdersList(ordersList);
        ordersLayout.getChildren().add(ordersList);
        ordersTab.setContent(ordersLayout);
        return ordersTab;
    }

    private Tab createServicesTab() {
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
        return servicesTab;
    }


    private void showMainScreen(Stage primaryStage) {
        // TabPane pentru a deține diferite secțiuni
        TabPane tabPane = new TabPane();

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
                    //updateProductList(productList);

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
            //TODO productsLayout.getChildren().add(addProductBox);
        }


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
        tabPane.getTabs().addAll(promotionsTab);

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
            productList.getItems().add(product.getId() + ":" + product.getType().toString() + ": " + product.getName() + " - " + product.getPrice() + " Lei");
        }
    }


    private void updateProductListForOrder(ListView<String> productList, Long orderId) {
        var products = purchaseOrderController.getProductsByOrderId(orderId);
        for (Product product : products) {
            productList.getItems().add(product.getId() + ":" + product.getType().toString() + ": " + product.getName() + " - " + product.getPrice() + " Lei");
        }

    }

    private void updateOrdersList(ListView<String> ordersList) {
        ordersList.getItems().clear();
        for (PurchaseOrder order : purchaseOrderController.getAllOrders()) {
            ordersList.getItems().add(order.getId() + ":" + order.getType().toString() + ": " + order.getType() + " - " + order.getStatus());
        }
    }

    private void updateClientOrdersList(ListView<String> ordersList) {
        ordersList.getItems().clear();
        for (PurchaseOrder order : purchaseOrderController.getOrdersByUserId(currentPerson.getId())) {
            ordersList.getItems().add(order.getId() + ":" + order.getType().toString() + ": " + order.getType() + " - " + order.getStatus());
        }
    }
}