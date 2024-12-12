package GUI;

import Core.Instruction.Instruction;
import Core.InstructionFileParser;
import Core.Main;
import Core.Register.RegisterEntry;
import Core.Register.RegisterFile;
import Core.Status;
import Core.Storage.*;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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

    public int cycles=0;

    //tables
    private TableView<Instruction> instructionQueueTable;
    private TableView<Map.Entry<String, RegisterEntry>> registerFileTable;
    private TableView<Map.Entry<Integer, byte[]>> cacheTable;
    private TableView<ArithmeticRSEntry> addRSTable;
    private TableView<ArithmeticRSEntry> mulRSTable;
    private TableView<LoadRSEntry> loadRSTable;
    private TableView<StoreRSEntry> storeRSTable;
    private TableView<BranchRSEntry> branchRSTable;

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
    private Button nextCycleButton;

    private Label cyclesLabel;

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        instructionQueueTable = createInstructionQueueTable();
        registerFileTable = createRegisterFileTable();
        cacheTable = createCacheTable();
        addRSTable = createAddSubTable();
        mulRSTable = createMulDivTable();
        loadRSTable = createLoadRSTable();
        storeRSTable = createStoreRSTable();
        branchRSTable = createBranchRSTable();

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

        nextCycleButton = new Button("Next Cycle");
        nextCycleButton.setOnAction(event -> getNextCycle());

        cyclesLabel = new Label("Current Cycle: " + cycles);

        root.getChildren().addAll(
                new Label("Latencies (cycles):"), latencyConfigBox,
                new Label("Reservation Station Sizes:"), rsConfigBox,
                new Label("Cache configurations:"), cacheConfigBox,
                new HBox(10, applyInputsButton, nextCycleButton, cyclesLabel),
                new Label("Instruction Queue"), instructionQueueTable,
                new HBox(10,
                        new VBox(10, new Label("Register File"), registerFileTable),
                        new VBox(10, new Label("Cache"), cacheTable)
                ),
                new Label("Add/Sub RS"), addRSTable,
                new Label("Mul/Div RS"), mulRSTable,
                new Label("Load Buffer"), loadRSTable,
                new Label("Store Buffer"), storeRSTable,
                new Label("Branch RS"), branchRSTable
        );
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
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

    private TableView<BranchRSEntry> createBranchRSTable() {
        TableView<BranchRSEntry> table = new TableView<>();

        // Tag Column
        TableColumn<BranchRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        // Busy Column
        TableColumn<BranchRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));

        // Vj Column
        TableColumn<BranchRSEntry, String> vjCol = new TableColumn<>("Vj");
        vjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVj() != null ? cellData.getValue().getVj().toString() : "N/A"));

        // Vk Column
        TableColumn<BranchRSEntry, String> vkCol = new TableColumn<>("Vk");
        vkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVk() != null ? cellData.getValue().getVk().toString() : "N/A"));

        // Address Column
        TableColumn<BranchRSEntry, String> qkCol = new TableColumn<>("Address");
        qkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstruction().getK()));

        table.getColumns().addAll(tagCol, busyCol, vjCol, vkCol, qkCol);

        // Set equal-width resizing policy
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private TableView<StoreRSEntry> createStoreRSTable() {
        TableView<StoreRSEntry> table = new TableView<>();

        TableColumn<StoreRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        TableColumn<StoreRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));

        TableColumn<StoreRSEntry, String> addCol = new TableColumn<>("Address");
        addCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAddress())));

        TableColumn<StoreRSEntry, String> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getValue())));

        TableColumn<StoreRSEntry, String> qCol = new TableColumn<>("Q");
        qCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQ())));

        table.getColumns().addAll(tagCol, busyCol, addCol, valCol, qCol);
        return table;
    }

    private TableView<LoadRSEntry> createLoadRSTable() {
        // Create a TableView for LoadRSEntry
        TableView<LoadRSEntry> table = new TableView<>();

        // Tag Column
        TableColumn<LoadRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        // Busy Column
        TableColumn<LoadRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));

        // Address Column
        TableColumn<LoadRSEntry, Integer> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getAddress()).asObject());

        table.getColumns().addAll(tagCol, busyCol, addressCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private TableView<ArithmeticRSEntry> createMulDivTable() {
        // Similar to Add/Sub TableView
        TableView<ArithmeticRSEntry> table = new TableView<>();

        // Tag Column
        TableColumn<ArithmeticRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        // Busy Column
        TableColumn<ArithmeticRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));

        // Remaining Cycles Column
        TableColumn<ArithmeticRSEntry, Integer> cyclesCol = new TableColumn<>("Remaining Cycles");
        cyclesCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRemainingCycles()).asObject());

        // Operation Column
        TableColumn<ArithmeticRSEntry, String> opCol = new TableColumn<>("Operation");
        opCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOp().toString()));

        // Vj Column
        TableColumn<ArithmeticRSEntry, String> vjCol = new TableColumn<>("Vj");
        vjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVj() != null ? cellData.getValue().getVj().toString() : "N/A"));

        // Vk Column
        TableColumn<ArithmeticRSEntry, String> vkCol = new TableColumn<>("Vk");
        vkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVk() != null ? cellData.getValue().getVk().toString() : "N/A"));

        // Qj Column
        TableColumn<ArithmeticRSEntry, String> qjCol = new TableColumn<>("Qj");
        qjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQj()));

        // Qk Column
        TableColumn<ArithmeticRSEntry, String> qkCol = new TableColumn<>("Qk");
        qkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQk()));

        // Add all columns to the table
        table.getColumns().addAll(tagCol, busyCol, cyclesCol, opCol, vjCol, vkCol, qjCol, qkCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private TableView<ArithmeticRSEntry> createAddSubTable() {
        TableView<ArithmeticRSEntry> table = new TableView<>();

        // Tag Column
        TableColumn<ArithmeticRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        // Busy Column
        TableColumn<ArithmeticRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));

        // Remaining Cycles Column
        TableColumn<ArithmeticRSEntry, Integer> cyclesCol = new TableColumn<>("Remaining Cycles");
        cyclesCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getRemainingCycles()).asObject());

        // Operation Column
        TableColumn<ArithmeticRSEntry, String> opCol = new TableColumn<>("Operation");
        opCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOp().toString()));

        // Vj Column
        TableColumn<ArithmeticRSEntry, String> vjCol = new TableColumn<>("Vj");
        vjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVj() != null ? cellData.getValue().getVj().toString() : "N/A"));

        // Vk Column
        TableColumn<ArithmeticRSEntry, String> vkCol = new TableColumn<>("Vk");
        vkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVk() != null ? cellData.getValue().getVk().toString() : "N/A"));

        // Qj Column
        TableColumn<ArithmeticRSEntry, String> qjCol = new TableColumn<>("Qj");
        qjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQj()));

        // Qk Column
        TableColumn<ArithmeticRSEntry, String> qkCol = new TableColumn<>("Qk");
        qkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQk()));

        table.getColumns().addAll(tagCol, busyCol, cyclesCol ,opCol, vjCol, vkCol, qjCol, qkCol);

        // Set equal-width resizing policy
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
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

        tableView.setMinHeight(150);

        return tableView;
    }

    private TableView<Map.Entry<String, RegisterEntry>> createRegisterFileTable() {
        TableView<Map.Entry<String, RegisterEntry>> registerFileTable = new TableView<>();

        // Register Name Column
        TableColumn<Map.Entry<String, RegisterEntry>, String> registerNameCol = new TableColumn<>("Register");
        registerNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getKey())
        );

        // Value Column
        TableColumn<Map.Entry<String, RegisterEntry>, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(cellData -> new SimpleStringProperty("" + cellData.getValue().getValue().getValue()));

        // Q Column
        TableColumn<Map.Entry<String, RegisterEntry>, String> qCol = new TableColumn<>("Q");
        qCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getValue().getQ())
        );

        registerFileTable.getColumns().addAll(registerNameCol, valueCol, qCol);

        registerFileTable.setMinHeight(100);
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

        ObservableList<Map.Entry<String, RegisterEntry>> registerEntries = FXCollections.observableArrayList(Main.registerFile.entrySet());
        registerFileTable.setItems(registerEntries);
        instructionQueueTable.refresh();
        registerFileTable.refresh();
    }

    private void getNextCycle(){
        cycles++;
        cyclesLabel.setText("Current Cycle: " + cycles);
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