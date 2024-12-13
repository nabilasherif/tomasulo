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

public class
TomasuloSimulator extends Application {
    //core components
//    List<Instruction> instructions = new ArrayList<>();
//    public Map<InstructionType, Integer> latencies = new HashMap<>();

    public static boolean isProgramDone = false;

    //tables
    private TableView<Instruction> instructionQueueTable;
    private TableView<Map.Entry<String, RegisterEntry>> registerFileTable;
    private TableView<Map.Entry<Integer, byte[]>> cacheTable;
    private TableView<ArithmeticRSEntry> addRSTable;
    private TableView<ArithmeticRSEntry> mulRSTable;
    private TableView<LoadRSEntry> loadRSTable;
    private TableView<StoreRSEntry> storeRSTable;
    private TableView<ArithmeticRSEntry> branchRSTable;

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
    private HBox latencyConfigBox;

    //rs
    private VBox addSubRSField;
    private VBox loadRSField;
    private VBox storeRSField;
    private VBox mulDivRSField;
    private VBox branchRSField;
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
        addFPLatencyField = createLatencyField("Add FP Latency:");
        subFPLatencyField = createLatencyField("Sub FP Latency:");
        mulFPLatencyField = createLatencyField("Mul FP Latency:");
        divFPLatencyField = createLatencyField("Div FP Latency:");

        HBox row1 = new HBox(10);
        row1.getChildren().addAll(addLatencyField, subLatencyField, mulLatencyField, divLatencyField);

        HBox row2 = new HBox(10);
        row2.getChildren().addAll(loadLatencyField, loadPenaltyField, storeLatencyField);

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
        branchRSField = createRSField("Branch RS Size:");
        rsConfigBox.getChildren().addAll(addSubRSField, mulDivRSField, loadRSField, storeRSField, branchRSField);

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

        cyclesLabel = new Label("Current Cycle: " + 0);

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

    private TableView<ArithmeticRSEntry> createBranchRSTable() {
        TableView<ArithmeticRSEntry> table = new TableView<>();

        TableColumn<ArithmeticRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        TableColumn<ArithmeticRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));
        busyCol.setCellFactory(column -> new TableCell<ArithmeticRSEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean busy, boolean empty) {
                super.updateItem(busy, empty);
                if (empty || busy == null) {
                    setText(null);
                } else {
                    setText(busy ? "1" : "0");
                }
            }
        });

        TableColumn<ArithmeticRSEntry, String> qjCol = new TableColumn<>("Qj");
        qjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQj() != null ? cellData.getValue().getQj().toString() : ""));

        TableColumn<ArithmeticRSEntry, String> qkCol = new TableColumn<>("Qk");
        qkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQk() != null ? cellData.getValue().getQk().toString() : ""));

        TableColumn<ArithmeticRSEntry, String> addCol = new TableColumn<>("Address");
        addCol.setCellValueFactory(cellData -> {
            String kValue = cellData.getValue().getInstruction() != null ? cellData.getValue().getInstruction().getK() : null;
            return new SimpleStringProperty(kValue == null ? "" : kValue);
        });

        table.getColumns().addAll(tagCol, busyCol, qjCol, qkCol, addCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(100);
        return table;
    }

    private TableView<StoreRSEntry> createStoreRSTable() {
        TableView<StoreRSEntry> table = new TableView<>();

        TableColumn<StoreRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        TableColumn<StoreRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));
        busyCol.setCellFactory(column -> new TableCell<StoreRSEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean busy, boolean empty) {
                super.updateItem(busy, empty);
                if (empty || busy == null) {
                    setText(null);
                } else {
                    setText(busy ? "1" : "0");
                }
            }
        });

        TableColumn<StoreRSEntry, String> addCol = new TableColumn<>("Address");
        addCol.setCellValueFactory(cellData -> {
            Integer address = cellData.getValue().getAddress();
            return new SimpleStringProperty(address == null ? "" : String.valueOf(address));
        });

        TableColumn<StoreRSEntry, String> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue() == null ? "" : String.valueOf(cellData.getValue().getValue())));

        TableColumn<StoreRSEntry, String> qCol = new TableColumn<>("Q");
        qCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQ()) != null ? String.valueOf(cellData.getValue().getQ()) : ""));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getColumns().addAll(tagCol, busyCol, addCol, valCol, qCol);
        table.setMinHeight(100);
        return table;
    }

    private TableView<LoadRSEntry> createLoadRSTable() {
        TableView<LoadRSEntry> table = new TableView<>();

        TableColumn<LoadRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        TableColumn<LoadRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));
        busyCol.setCellFactory(column -> new TableCell<LoadRSEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean busy, boolean empty) {
                super.updateItem(busy, empty);
                if (empty || busy == null) {
                    setText(null);
                } else {
                    setText(busy ? "1" : "0");
                }
            }
        });

        TableColumn<LoadRSEntry, String> addCol = new TableColumn<>("Address");
        addCol.setCellValueFactory(cellData -> {
            Integer address = cellData.getValue().getAddress();
            return new SimpleStringProperty(address == null ? "" : String.valueOf(address));
        });

        table.getColumns().addAll(tagCol, busyCol, addCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(100);
        return table;
    }

    private TableView<ArithmeticRSEntry> createMulDivTable() {
        TableView<ArithmeticRSEntry> table = new TableView<>();

        TableColumn<ArithmeticRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        TableColumn<ArithmeticRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));
        busyCol.setCellFactory(column -> new TableCell<ArithmeticRSEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean busy, boolean empty) {
                super.updateItem(busy, empty);
                if (empty || busy == null) {
                    setText(null);
                } else {
                    setText(busy ? "1" : "0");
                }
            }
        });

        TableColumn<ArithmeticRSEntry, String> opCol = new TableColumn<>("Operation");
        opCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstruction() == null ? "" : cellData.getValue().getInstruction().getOp().toString()));

        TableColumn<ArithmeticRSEntry, String> vjCol = new TableColumn<>("Vj");
        vjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVj() != null ?  String.valueOf(cellData.getValue().getVj()) : ""));

        TableColumn<ArithmeticRSEntry, String> vkCol = new TableColumn<>("Vk");
        vkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVk() != null ? cellData.getValue().getVk().toString() : ""));

        TableColumn<ArithmeticRSEntry, String> qjCol = new TableColumn<>("Qj");
        qjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQj() != null ? cellData.getValue().getQj() : ""));

        TableColumn<ArithmeticRSEntry, String> qkCol = new TableColumn<>("Qk");
        qkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQk() != null ? cellData.getValue().getQk() : ""));

        table.getColumns().addAll(tagCol, busyCol, opCol, vjCol, vkCol, qjCol, qkCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(100);
        return table;
    }

    private TableView<ArithmeticRSEntry> createAddSubTable() {
        TableView<ArithmeticRSEntry> table = new TableView<>();

        TableColumn<ArithmeticRSEntry, String> tagCol = new TableColumn<>("Tag");
        tagCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTag()));

        TableColumn<ArithmeticRSEntry, Boolean> busyCol = new TableColumn<>("Busy");
        busyCol.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().isBusy()));
        busyCol.setCellFactory(column -> new TableCell<ArithmeticRSEntry, Boolean>() {
            @Override
            protected void updateItem(Boolean busy, boolean empty) {
                super.updateItem(busy, empty);
                if (empty || busy == null) {
                    setText(null);
                } else {
                    setText(busy ? "1" : "0");
                }
            }
        });

        TableColumn<ArithmeticRSEntry, String> opCol = new TableColumn<>("Operation");
        opCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInstruction() == null ? "" : cellData.getValue().getInstruction().getOp().toString()));

        TableColumn<ArithmeticRSEntry, String> vjCol = new TableColumn<>("Vj");
        vjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVj() != null ?  String.valueOf(cellData.getValue().getVj()) : ""));

        TableColumn<ArithmeticRSEntry, String> vkCol = new TableColumn<>("Vk");
        vkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVk() != null ? cellData.getValue().getVk().toString() : ""));

        TableColumn<ArithmeticRSEntry, String> qjCol = new TableColumn<>("Qj");
        qjCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQj() != null ? cellData.getValue().getQj() : ""));

        TableColumn<ArithmeticRSEntry, String> qkCol = new TableColumn<>("Qk");
        qkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getQk() != null ? cellData.getValue().getQk() : ""));

        table.getColumns().addAll(tagCol, busyCol ,opCol, vjCol, vkCol, qjCol, qkCol);

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinHeight(100);
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
        issuedCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIssue() == 0 ? "" : String.valueOf(cellData.getValue().getIssue()))
        );
        TableColumn<Instruction, String> executedCol = new TableColumn<>("Executed");
        executedCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getExecution().toString()));

        TableColumn<Instruction, String> writebackCol = new TableColumn<>("Writeback");
        writebackCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWrite() == 0 ? "" : String.valueOf(cellData.getValue().getWrite())));

        tableView.getColumns().addAll(operationCol, destCol, jCol, kCol, issuedCol, executedCol, writebackCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setMinHeight(150);
        return tableView;
    }

    private TableView<Map.Entry<String, RegisterEntry>> createRegisterFileTable() {
        TableView<Map.Entry<String, RegisterEntry>> registerFileTable = new TableView<>();

        TableColumn<Map.Entry<String, RegisterEntry>, String> registerNameCol = new TableColumn<>("Register");
        registerNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getKey()));

        TableColumn<Map.Entry<String, RegisterEntry>, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(cellData -> new SimpleStringProperty("" + cellData.getValue().getValue().getValue()));

        TableColumn<Map.Entry<String, RegisterEntry>, String> qCol = new TableColumn<>("Q");
        qCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getQ()));

        registerFileTable.getColumns().addAll(registerNameCol, valueCol, qCol);
        registerFileTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        registerFileTable.setMinHeight(100);
        return registerFileTable;
    }

    private TableView<Map.Entry<Integer, byte[]>> createCacheTable() {
        TableView<Map.Entry<Integer, byte[]>> tableView = new TableView<>();

        TableColumn<Map.Entry<Integer, byte[]>, String> blockAddressCol = new TableColumn<>("Block Address");
        blockAddressCol.setCellValueFactory(cellData -> {
            int index = cellData.getValue().getKey();
            int blockStartAddress = index - (index % Main.blockSize);
            return new SimpleStringProperty(String.valueOf(blockStartAddress));
        });

        TableColumn<Map.Entry<Integer, byte[]>, String> dataCol = new TableColumn<>("Block Data");
        dataCol.setCellValueFactory(cellData -> {
            byte[] blockData = cellData.getValue().getValue();
            StringBuilder dataString = new StringBuilder();
            for (byte b : blockData) {
                dataString.append(b).append(" ");
            }
            return new SimpleStringProperty(dataString.toString());
        });

        ObservableList<Map.Entry<Integer, byte[]>> cacheDataList = FXCollections.observableArrayList();

        for (int i = 0; i < Main.cache.cache.length; i += Main.blockSize) {
            int blockStartAddress = i;
            byte[] blockData = new byte[Main.blockSize];
            System.arraycopy(Main.cache.cache, i, blockData, 0, Main.blockSize);
            cacheDataList.add(new AbstractMap.SimpleEntry<>(blockStartAddress, blockData));
        }

        tableView.setItems(cacheDataList);

        tableView.getColumns().addAll(blockAddressCol, dataCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tableView;
    }

    public void refreshCacheTable() {
        ObservableList<Map.Entry<Integer, byte[]>> cacheDataList = FXCollections.observableArrayList();

        for (int i = 0; i < Main.cache.cache.length; i += Main.blockSize) {
            int blockStartAddress = i;
            byte[] blockData = new byte[Main.blockSize];
            System.arraycopy(Main.cache.cache, i, blockData, 0, Main.blockSize);
            cacheDataList.add(new AbstractMap.SimpleEntry<>(blockStartAddress, blockData));
        }

        cacheTable.setItems(cacheDataList);
        cacheTable.refresh();
    }

    private void applyLatencies() {
        try {
            Main.addLatency = Integer.parseInt(((TextField) addLatencyField.getChildren().get(1)).getText());
            Main.subLatency = Integer.parseInt(((TextField) subLatencyField.getChildren().get(1)).getText());
            Main.mulFPLatency = Integer.parseInt(((TextField) mulLatencyField.getChildren().get(1)).getText());
            Main.divFPLatency = Integer.parseInt(((TextField) divLatencyField.getChildren().get(1)).getText());
            Main.loadLatency = Integer.parseInt(((TextField) loadLatencyField.getChildren().get(1)).getText());
            Main.loadPenalty = Integer.parseInt(((TextField) loadPenaltyField.getChildren().get(1)).getText());
            Main.storeLatency = Integer.parseInt(((TextField) storeLatencyField.getChildren().get(1)).getText());
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
            Main.branchReservationStationSize = Integer.parseInt(((TextField) branchRSField.getChildren().get(1)).getText());
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
        instructionQueueTable.refresh();

        ObservableList<Map.Entry<String, RegisterEntry>> registerEntries = FXCollections.observableArrayList(Main.registerFile.entrySet());
        registerFileTable.setItems(registerEntries);
        registerFileTable.refresh();

        ObservableList<ArithmeticRSEntry> addSubRSList = FXCollections.observableArrayList(Main.addSubRS);
        addRSTable.setItems(addSubRSList);
        addRSTable.refresh();

        ObservableList<ArithmeticRSEntry> mulDivRSList = FXCollections.observableArrayList(Main.mulDivRS);
        mulRSTable.setItems(mulDivRSList);
        mulRSTable.refresh();

        ObservableList<LoadRSEntry> loadRSList = FXCollections.observableArrayList(Main.loadRS);
        loadRSTable.setItems(loadRSList);
        loadRSTable.refresh();

        ObservableList<StoreRSEntry> storeRSList = FXCollections.observableArrayList(Main.storeRS);
        storeRSTable.setItems(storeRSList);
        storeRSTable.refresh();

        ObservableList<ArithmeticRSEntry> branchRSList = FXCollections.observableArrayList(Main.branchRS);
        branchRSTable.setItems(branchRSList);
        branchRSTable.refresh();

        refreshCacheTable();
    }

    private void getNextCycle(){
        isProgramDone = Main.pc >= Main.instructionQueue.size() && Main.allStationsEmpty();
        if(isProgramDone){
            return;
        }

        Main.incrementCycle();
        cyclesLabel.setText("Current Cycle: " + Main.cycle);

        ObservableList<Instruction> instructions = FXCollections.observableArrayList(Main.instructionQueue);
        instructionQueueTable.setItems(instructions);
        instructionQueueTable.refresh();

        ObservableList<Map.Entry<String, RegisterEntry>> registerEntries = FXCollections.observableArrayList(Main.registerFile.entrySet());
        registerFileTable.setItems(registerEntries);
        registerFileTable.refresh();

        ObservableList<ArithmeticRSEntry> addSubRSList = FXCollections.observableArrayList(Main.addSubRS);
        addRSTable.setItems(addSubRSList);
        addRSTable.refresh();

        ObservableList<ArithmeticRSEntry> mulDivRSList = FXCollections.observableArrayList(Main.mulDivRS);
        mulRSTable.setItems(mulDivRSList);
        mulRSTable.refresh();

        ObservableList<LoadRSEntry> loadRSList = FXCollections.observableArrayList(Main.loadRS);
        loadRSTable.setItems(loadRSList);
        loadRSTable.refresh();

        ObservableList<StoreRSEntry> storeRSList = FXCollections.observableArrayList(Main.storeRS);
        storeRSTable.setItems(storeRSList);
        storeRSTable.refresh();

        ObservableList<ArithmeticRSEntry> branchRSList = FXCollections.observableArrayList(Main.branchRS);
        branchRSTable.setItems(branchRSList);
        branchRSTable.refresh();

        refreshCacheTable();
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