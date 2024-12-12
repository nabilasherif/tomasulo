package GUI;

import Core.Instruction.Instruction;
import Core.InstructionFileParser;
import Core.Main;
import Core.Register.RegisterEntry;
import Core.Register.RegisterFile;
import Core.Status;
import Core.Storage.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;
import Core.Instruction.InstructionType;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import java.util.List;

public class TomasuloSimulator extends Application {
    //core components
//    List<Instruction> instructions = new ArrayList<>();
//    public Map<InstructionType, Integer> latencies = new HashMap<>();

    public int clkCycles=0;

    private TextField clockCycleField;

    RegisterFile registerFile = new RegisterFile();
    ObservableList<RegisterEntry> registerEntries = FXCollections.observableArrayList();
    ObservableList<String> registerNames = FXCollections.observableArrayList();

    //tables
    private TableView<Instruction> instructionQueueTable;
    private TableView<RegisterEntry> registerFileTable;
    private TableView<Map.Entry<Integer, byte[]>> cacheTable;
    private TableView<ArithmeticRSEntry> addRSTable;
    private TableView<ArithmeticRSEntry> mulRSTable;
    private TableView<LoadRSEntry> loadRSTable;
    private TableView<StoreRSEntry> storeRSTable;

    //latencies
    private VBox addLatencyField;
    private VBox addFPLatencyField;
    private VBox subLatencyField;
    private VBox subFPLatencyField;
    private VBox loadLatencyField;
    private VBox loadPenaltyField;
    private VBox storeLatencyField;
    private VBox mulLatencyField;
    private VBox mulFPLatencyField;
    private VBox divLatencyField;
    private VBox divFPLatencyField;
    private VBox branchLatencyField;
    private HBox latencyConfigBox;

    //rs
    private VBox addSubRSField;
    private VBox loadRSField;
    private VBox storeRSField;
    private VBox mulDivRSField;
    private HBox rsConfigBox;

    //cache
    private TextField cacheSizeField;
    private TextField blockSizeField;

    private Button applyInputsButton;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        instructionQueueTable = createInstructionQueueTable();
        registerFileTable = createRegisterFileTable();
        cacheTable = createCacheTable();

//        addRSTable = createAddRSTable("Add Reservation Stations", new ArrayList<>());
//        mulRSTable = createMulRSTable("Multiply Reservation Stations", new ArrayList<>());
//        loadRSTable = createLRSTable("Load Reservation Stations", new ArrayList<>());
//        storeRSTable = createSRSTable("Store Reservation Stations", new ArrayList<>());

        //latencies config
        VBox latencyConfigBox = new VBox(10);
        latencyConfigBox.setSpacing(5);

        addLatencyField = createLatencyField("Add Latency:");
        subLatencyField = createLatencyField("Sub Latency:");
        mulLatencyField = createLatencyField("Mul Latency:");
        divLatencyField = createLatencyField("Div Latency:");
        loadLatencyField = createLatencyField("Load Latency:");
        loadPenaltyField = createLatencyField("Load Penalty:");
        storeLatencyField = createLatencyField("Store Latency:");
        branchLatencyField = createLatencyField("Branch Latency:");
        addFPLatencyField = createLatencyField("Add FP Latency:");
        subFPLatencyField = createLatencyField("Sub FP Latency:");
        mulFPLatencyField = createLatencyField("Mul FP Latency:");
        divFPLatencyField = createLatencyField("Div FP Latency:");

        HBox row1 = new HBox(10);
        row1.getChildren().addAll(addLatencyField, subLatencyField, mulLatencyField, divLatencyField);

        HBox row2 = new HBox(10);
        row2.getChildren().addAll(loadLatencyField, loadPenaltyField, storeLatencyField, branchLatencyField);

        HBox row3 = new HBox(10);
        row3.getChildren().addAll(addFPLatencyField, subFPLatencyField, mulFPLatencyField, divFPLatencyField);

        latencyConfigBox.getChildren().addAll(row1, row2, row3);

        //rs config
        rsConfigBox = new HBox(10);
        rsConfigBox.setSpacing(15);
        addSubRSField = createRSField("Add/Sub RS Size:");
        mulDivRSField = createRSField("Mul/Div RS Size:");
        loadRSField = createRSField("Load RS Size:");
        storeRSField = createRSField("Store RS Size:");
        rsConfigBox.getChildren().addAll(addSubRSField, mulDivRSField, loadRSField, storeRSField);

        //cache config
        HBox cacheConfigBox = new HBox(10);
        Label cacheSizeLabel = new Label("Cache Size (bytes):");
        cacheSizeField = new TextField("1024");
        Label blockSizeLabel = new Label("Block Size (bytes):");
        blockSizeField = new TextField("64");
        cacheConfigBox.getChildren().addAll(cacheSizeLabel, cacheSizeField, blockSizeLabel, blockSizeField);

        applyInputsButton = new Button("Apply Inputs");
        applyInputsButton.setOnAction(event -> applyAllInputs());

//        String filePath = "src/main/java/Core/program.txt";
//        List<Instruction> instructionQueue = InstructionFileParser.fillInstructionsQueue(filePath);
//        ObservableList<Instruction> instructions = FXCollections.observableArrayList(instructionQueue);
//        instructionQueueTable.setItems(instructions);
//
//        initializeRegisterFile();
//        registerFileTable.setItems(registerEntries);

        root.getChildren().addAll(
                new Label("Latencies (cycles):"), latencyConfigBox,
                new Label("Reservation Station Sizes:"), rsConfigBox,
                new Label("Cache configurations:"), cacheConfigBox,
                applyInputsButton,
                new Label("Instruction Queue"), instructionQueueTable,
                new HBox(10,
                        new VBox(10, new Label("Register File"), registerFileTable),
                        new VBox(10, new Label("Cache"), cacheTable)
                )
//                new Label("Add Reservation Station"), addRSTable,
//                new Label("Multiply Reservation Station"), mulRSTable,
//                new Label("Load Reservation Station"), loadRSTable,
//                new Label("Store Reservation Station"), storeRSTable
        );

        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setTitle("Tomasulo Algorithm Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLatencyField(String label) {
        VBox vbox = new VBox(10);
        Label latencyLabel = new Label(label);
        TextField textField = new TextField();
        vbox.getChildren().addAll(latencyLabel, textField);
        return vbox;
    }

    private VBox createRSField(String label) {
        VBox vbox = new VBox(10);
        Label rsLabel = new Label(label);
        TextField textField = new TextField();
        vbox.getChildren().addAll(rsLabel, textField);
        return vbox;
    }

    private TableView<Instruction> createInstructionQueueTable(){
        TableView<Instruction> tableView = new TableView<>();

        TableColumn<Instruction, String> operationCol = new TableColumn<>("Operation");
        operationCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOp().toString()));

        TableColumn<Instruction, String> destCol = new TableColumn<>("Destination");
        destCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDest()));

        TableColumn<Instruction, String> jCol = new TableColumn<>("J");
        jCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getJ()));

        TableColumn<Instruction, String> kCol = new TableColumn<>("K");
        kCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getK()));

        TableColumn<Instruction, String> issuedCol = new TableColumn<>("Issued");

        issuedCol.setCellValueFactory(cellData -> new SimpleStringProperty("" + cellData.getValue().getIssue()));

        TableColumn<Instruction, String> executedCol = new TableColumn<>("Executed");
        executedCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExecution().toString()));

        TableColumn<Instruction, String> writebackCol = new TableColumn<>("Writeback");
        writebackCol.setCellValueFactory(cellData -> new SimpleStringProperty(""+cellData.getValue().getWrite()));
        // Adding the updated columns to the TableView
        tableView.getColumns().addAll(operationCol, destCol, jCol, kCol, issuedCol, executedCol, writebackCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        return tableView;
    }


    private TableView<RegisterEntry> createRegisterFileTable() {
        TableView<RegisterEntry> registerFileTable = new TableView<>();

        TableColumn<RegisterEntry, String> registerNameCol = new TableColumn<>("Register");
        registerNameCol.setCellValueFactory(cellData -> {
            String name = registerNames.get(registerEntries.indexOf(cellData.getValue()));
            return new SimpleStringProperty(name);
        });

        TableColumn<RegisterEntry, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getValue() != 0) {
                return new SimpleStringProperty(String.valueOf(cellData.getValue().getValue()));
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        TableColumn<RegisterEntry, String> qCol = new TableColumn<>("Q");
        qCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQ()));

        registerFileTable.getColumns().addAll(registerNameCol, valueCol, qCol);
        return registerFileTable;
    }

    private TableView<Map.Entry<Integer, byte[]>> createCacheTable() {
        TableView<Map.Entry<Integer, byte[]>> tableView = new TableView<>();

        TableColumn<Map.Entry<Integer, byte[]>, String> blockAddressCol = new TableColumn<>("Block Address");
        blockAddressCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getKey())));

        TableColumn<Map.Entry<Integer, byte[]>, String> dataCol = new TableColumn<>("Block Data");
        dataCol.setCellValueFactory(cellData -> {
            byte[] blockData = cellData.getValue().getValue();
            StringBuilder dataString = new StringBuilder();
            for (byte b : blockData) {
                dataString.append(b).append(" ");
            }
            return new SimpleStringProperty(dataString.toString());
        });

        tableView.getColumns().addAll(blockAddressCol, dataCol);
        return tableView;
    }

//    private void initializeRegisterFile() {
//        Map<String, RegisterEntry> registers = registerFile.getRegisters();
//
//        for (Map.Entry<String, RegisterEntry> entry : registers.entrySet()) {
//            RegisterEntry regEntry = entry.getValue();
//            registerEntries.add(regEntry);
//            registerNames.add(entry.getKey());
//        }
//    }

    private void applyLatencies() {
        try {
            Main.addLatency = Integer.parseInt(((TextField) addLatencyField.getChildren().get(1)).getText());
            Main.subLatency = Integer.parseInt(((TextField) subLatencyField.getChildren().get(1)).getText());
            Main.mulLatency = Integer.parseInt(((TextField) mulLatencyField.getChildren().get(1)).getText());
            Main.divLatency = Integer.parseInt(((TextField) divLatencyField.getChildren().get(1)).getText());
            Main.loadLatency = Integer.parseInt(((TextField) loadLatencyField.getChildren().get(1)).getText());
            Main.loadPenalty = Integer.parseInt(((TextField) loadPenaltyField.getChildren().get(1)).getText());
            Main.storeLatency = Integer.parseInt(((TextField) storeLatencyField.getChildren().get(1)).getText());
            Main.branchLatency = Integer.parseInt(((TextField) branchLatencyField.getChildren().get(1)).getText());
            Main.addFPLatency = Integer.parseInt(((TextField) addFPLatencyField.getChildren().get(1)).getText());
            Main.subFPLatency = Integer.parseInt(((TextField) subFPLatencyField.getChildren().get(1)).getText());
            Main.mulFPLatency = Integer.parseInt(((TextField) mulFPLatencyField.getChildren().get(1)).getText());
            Main.divFPLatency = Integer.parseInt(((TextField) divFPLatencyField.getChildren().get(1)).getText());
        } catch (NumberFormatException e) {
            showErrorDialog("Input Error", "Please enter valid numeric values for latencies.");
        }
    }

    private void applyReservationStationSizes() {
        try {
            Main.addReservationStationSize = Integer.parseInt(((TextField) addSubRSField.getChildren().get(1)).getText());
            Main.mulReservationStationSize = Integer.parseInt(((TextField) mulDivRSField.getChildren().get(1)).getText());
            Main.loadReservationStationSize = Integer.parseInt(((TextField) loadRSField.getChildren().get(1)).getText());
            Main.storeReservationStationSize = Integer.parseInt(((TextField) storeRSField.getChildren().get(1)).getText());
        } catch (NumberFormatException e) {
            showErrorDialog("Input Error", "Please enter valid numeric values for reservation station sizes.");
        }
    }

    private void applyCacheConfig() {
        try {
            Main.cacheSize = Integer.parseInt(cacheSizeField.getText());
            Main.blockSize = Integer.parseInt(blockSizeField.getText());
        } catch (NumberFormatException e) {
            showErrorDialog("Input Error", "Please enter valid numeric values for cache size and block size.");
        }
    }

    private void applyAllInputs() {
        applyLatencies();
        applyReservationStationSizes();
        applyCacheConfig();
        Main.init();
        ObservableList<Instruction> instructions = FXCollections.observableArrayList(Main.instructionQueue);
        instructionQueueTable.setItems(instructions);
        ObservableList<RegisterEntry> observableList = FXCollections.observableArrayList(Main.registerFile.values());
        registerFileTable.setItems(observableList);
        instructionQueueTable.refresh();
        registerFileTable.refresh();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}