/**
 * Rolando Murillo
 * COP 4330, Fall 2018
 * SQL Client Controller
 */

package SQLClient;

// imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.*;
import javafx.util.Callback;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

// sole controller
public class SQLClientController implements Initializable
{
    // property of the mySQLModel object
    private MySQLModel mySQLModel;

    // property to keep track of all the returned records from a single query
    // updates after every query execution
    private ObservableList<ObservableList> records;

    // ======= FXML UI ========
    @FXML
    private Label connectionStatusLabel;

    @FXML
    private Button executeSQLCommandButton;

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
    private TableView<ObservableList> resultTableView;


    // ======= FXML UI Action Events ========
    @FXML
    private void connectToDatabaseButtonPressed(ActionEvent event) {

        // get the url from the observablelist
        String dbURL = mySQLModel.databaseURLOptions[0];
        mySQLModel.connectToDatabase(usernameTextField.getText(), passwordTextField.getText(), dbURL);

        // connection status label
        if (mySQLModel.connectedToDatabase) {
            connectionStatusLabel.setText("Connected to " + dbURL);
            executeSQLCommandButton.setDisable(false);
        }
        else
            connectionStatusLabel.setText("Failed to connect.\nMake sure server is running and credentials are correct.");
    }

    @FXML
    private void clearSQLCommandButtonPressed(ActionEvent event) {
        sqlCommandTextArea.setText("");
    }

    @FXML
    private void executeSQLCommandButtonPressed(ActionEvent event) {

        records = FXCollections.observableArrayList();

        String command = sqlCommandTextArea.getText();
        String[] wordsInCommand = command.split(" ", 2);

        if (wordsInCommand[0].equalsIgnoreCase("select")) {

            String response = mySQLModel.executeSQLQuery(sqlCommandTextArea.getText());

            // If there was a null response, that means no error message was returned from the model.
            // So continue with parsing the results.
            if (response == null) {
                System.out.println("Column Count: " + mySQLModel.getColumnCount() + "\nRow Count: " + mySQLModel.getRowCount());

                resultTableView.getItems().clear();
                resultTableView.getColumns().clear();
                //ObservableList<TableColumn> columnNames = FXCollections.observableArrayList();
                for (int i = 0; i < mySQLModel.getColumnCount(); i++) {

                    TableColumn col = new TableColumn<>(mySQLModel.getColumnName(i));

                    // console printing
                    System.out.printf("%-20s\t", mySQLModel.getColumnName(i));

                    final int j = i;

                    // configuring the retrieval method the data for each cell
                    col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });

                    col.setStyle( "-fx-alignment: CENTER;");

                    // Dynamically set the width of each column
                    col.setPrefWidth(resultTableView.getWidth() / mySQLModel.getColumnCount());

                    // add the configured column to the tableview
                    resultTableView.getColumns().add(col);
                }

                // Console printing
                System.out.println();
                for (int i = 0; i < mySQLModel.getColumnCount(); i++) {
                    System.out.printf("%-20s\t", "---------------");
                }

                try {
                    System.out.println();

                    for (int x = 0; x < mySQLModel.getRowCount(); x++) {
                        ObservableList<String> row = FXCollections.observableArrayList();

                        for (int i = 0; i < mySQLModel.getColumnCount(); i++) {
                            row.add(mySQLModel.getValueAt(x, i).toString());
                            System.out.printf("%-20s\t", row.get(i));
                        }

                        records.add(row);
                        System.out.println();
                    }

                    // give the tableview the data for its cells (the records instance (ObservableList))
                    resultTableView.setItems(records);
                    mySQLModel.resultSet.first();
                } catch (SQLException e) {
                    System.out.println("error: " + e);
                }
            }
            else
                createAlert(response, Alert.AlertType.ERROR);
        }
        // Gets executed if the query did not begin with a SELECT.
        else {
            String response = mySQLModel.executeSQLUpdate(command);
            boolean successResponse = mySQLModel.responseType(response);
            if (successResponse)
                createAlert(response, Alert.AlertType.INFORMATION);
            else
                createAlert(response, Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clearResultWindowButtonPressed(ActionEvent event) {
        // Clear the items and columns
        resultTableView.getItems().clear();
        resultTableView.getColumns().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Create the model instance
        mySQLModel = new MySQLModel();

        // Give the driver combobox its options
        ObservableList<String> options = FXCollections.observableArrayList(mySQLModel.driverOptions);
        JDBCDriverCombobox.setItems(options);

        // Give the database url combobox its options
        options = FXCollections.observableArrayList(mySQLModel.databaseURLOptions);
        databaseURLCombobox.setItems(options);

        // Change some UI states
        executeSQLCommandButton.setDisable(true);
        resultTableView.getItems().clear();
        resultTableView.getColumns().clear();
        resultTableView.setId("myTableView");
    }

    private void createAlert(String message, Alert.AlertType type) {
        // Show error message
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
