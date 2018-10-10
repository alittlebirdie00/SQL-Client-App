package SQLClient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SQLClientController implements Initializable
{
    private MySQLModel mySQLModel;

    // ======= FXML UI ========
    @FXML
    private Label connectionStatusLabel;

    @FXML
    private Button connectToDatabaseButton;

    @FXML
    private Button clearSQLCommandButton;

    @FXML
    private Button executeSQLCommandButton;

    @FXML
    private Button clearResultWindowButton;

    @FXML
    private ComboBox<String> JDBCDriverCombobox;

    @FXML
    private ComboBox<String> databaseURLCombobox;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextArea sqlCommandTextArea;

    @FXML
    private TableView resultTableView;

    @FXML
    private ImageView imageViewMysql;


    // ======= FXML UI Action Events ========
    @FXML
    private void connectToDatabaseButtonPressed(ActionEvent event) {

        String dbURL = mySQLModel.databaseURLOptions[0];
        mySQLModel.connectToDatabase(usernameTextField.getText(), passwordTextField.getText(), dbURL);
        if (mySQLModel.connectedToDatabase) {
            connectionStatusLabel.setText("Connected to " + dbURL);
            executeSQLCommandButton.setDisable(false);
        }
        else {
            connectionStatusLabel.setText("Failed to connect.\nMake sure server is running and credentials are correct.");
        }

    }

    @FXML
    private void clearSQLCommandButtonPressed(ActionEvent event) {
        sqlCommandTextArea.setText("");
    }

    @FXML
    private void executeSQLCommandButtonPressed(ActionEvent event) {
        mySQLModel.executeSQLQuery(sqlCommandTextArea.getText());

    }

    @FXML
    private void clearResultWindowButtonPressed(ActionEvent event) {

    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the MySQL model object property
        mySQLModel = new MySQLModel();

        ObservableList<String> options = FXCollections.observableArrayList(mySQLModel.driverOptions);
        JDBCDriverCombobox.setItems(options);

        options = FXCollections.observableArrayList(mySQLModel.databaseURLOptions);
        databaseURLCombobox.setItems(options);

        resultTableView = new TableView<MySQLModel>();
        ObservableList<MySQLModel> model= FXCollections.observableArrayList(mySQLModel);

        resultTableView.setItems(model);

        executeSQLCommandButton.setDisable(true);
    }
}
