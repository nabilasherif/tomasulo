package GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class TomasuloSimulator extends Application {
    // Configuration parameters
    private int loadStoreBufferSize = 3;
    private int addReservationStationSize = 3;
    private int mulReservationStationSize = 2;
    private int integerReservationStationSize = 2;
    private int clkCycles=0;

    // Latencies
    private Map<InstructionType, Integer> latencies = new HashMap<>();

    // Components
    private TableView<ReservationStation> addResStationTable;
    private TableView<ReservationStation> mulResStationTable;
    private TableView<ReservationStation> intResStationTable;
    private TableView<LoadBuffer> loadBufferTable;
    private TableView<Register> registerTable;
    private TextArea instructionInput;
    private TableView<Instruction> instructionQueueTable;

    // Cache configuration
    private int cacheSize = 1024; // bytes
    private int blockSize = 64;   // bytes
    private int hitLatency = 1;   // cycles
    private int missLatency = 10; // cycles

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Create configuration section
        root.getChildren().add(createConfigurationSection());

        // Create instruction input section
//        root.getChildren().add(createInstructionInputSection());

        // Create instruction section
        root.getChildren().add(createInstruction());

        // Create tables section
        root.getChildren().add(createTablesSection());

        // Create control buttons
        root.getChildren().add(createControlButtons());

        // Wrap the VBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);  // Allow scrolling for the entire height
        scrollPane.setFitToWidth(true);   // Allow scrolling for the entire width

        Scene scene = new Scene(scrollPane, 800, 600); // Set the ScrollPane as the scene's root
        primaryStage.setTitle("Tomasulo Algorithm Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createConfigurationSection() {
        VBox config = new VBox(5);
        config.getChildren().addAll(
                new Label("Configuration"),
                createLatencyInputs(),
                createStationSizeInputs(),
                createCacheConfigInputs()
        );
        return config;
    }

    private GridPane createLatencyInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        int row = 0;
        for (InstructionType type : InstructionType.values()) {
            grid.add(new Label(type.toString() + " Latency:"), 0, row);
            TextField latencyField = new TextField("1");
            grid.add(latencyField, 1, row);
            row++;
        }

        return grid;
    }

    private GridPane createStationSizeInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        grid.add(new Label("Load/Store Buffer Size:"), 0, 0);
        TextField loadStoreField = new TextField(String.valueOf(loadStoreBufferSize));
        grid.add(loadStoreField, 1, 0);

        grid.add(new Label("Add RS Size:"), 0, 1);
        TextField addRSField = new TextField(String.valueOf(addReservationStationSize));
        grid.add(addRSField, 1, 1);

        return grid;
    }

    private GridPane createCacheConfigInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        grid.add(new Label("Cache Size (bytes):"), 0, 0);
        TextField cacheSizeField = new TextField(String.valueOf(cacheSize));
        grid.add(cacheSizeField, 1, 0);

        grid.add(new Label("Block Size (bytes):"), 0, 1);
        TextField blockSizeField = new TextField(String.valueOf(blockSize));
        grid.add(blockSizeField, 1, 1);

        grid.add(new Label("Clock Cycle:"), 0, 2);
        TextField clockCycleField = new TextField(String.valueOf(clkCycles));
        clockCycleField.setEditable(false);
        clockCycleField.setStyle("-fx-background-color: lightgray;");
        grid.add(clockCycleField, 1, 2);

        return grid;
    }


//    private VBox createInstructionInputSection() {
//        VBox section = new VBox(5);
//        instructionInput = new TextArea();
//        instructionInput.setPrefRowCount(5);
//        section.getChildren().addAll(
//                new Label("Instructions (one per line)"),
//                instructionInput
//        );
//        return section;
//    }

    private VBox createInstruction() {
        HBox instruction = new HBox(10);

        ComboBox<String> pre = new ComboBox<>();
        pre.getItems().addAll("LOOP", "None");
        pre.setValue("None");

        ComboBox<String> operation = new ComboBox<>();
        operation.getItems().addAll("ADD.D", "SUB.D", "MUL.D", "DIV.D", "ADDI", "SUBI", "LW", "LD", "L.S", "L.D", "SW", "SD", "S.S", "S.D");
        operation.setValue("ADD.D");

        TextField destination = new TextField("R1");
        TextField operand1 = new TextField("R2");
        TextField operand2 = new TextField("R3");
        Button addButton = new Button("Add");

        instruction.getChildren().addAll(pre, operation, destination, operand1, operand2, addButton);

        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(new Label("Create Instruction"), instruction);

        return vbox;
    }

    private VBox createTablesSection() {
        VBox tables = new VBox(10);

        // Create reservation station tables
        addResStationTable = createReservationStationTable("Add Reservation Stations");
        mulResStationTable = createReservationStationTable("Multiply Reservation Stations");
        intResStationTable = createReservationStationTable("Integer Reservation Stations");

        // Create load buffer table
        loadBufferTable = createLoadBufferTable();

        // Create register table
        registerTable = createRegisterTable();

        // Create instruction queue table
        instructionQueueTable = createInstructionQueueTable();

        tables.getChildren().addAll(
                addResStationTable,
                mulResStationTable,
                intResStationTable,
                loadBufferTable,
                registerTable,
                instructionQueueTable
        );

        return tables;
    }

    private HBox createControlButtons() {
        HBox buttons = new HBox(10);
        Button loadButton = new Button("Load Instructions");
        Button stepButton = new Button("Step");
        Button runButton = new Button("Run");
        Button resetButton = new Button("Reset");

        buttons.getChildren().addAll(loadButton, stepButton, runButton, resetButton);

        return buttons;
    }

    // Helper classes
    enum InstructionType {
        LOAD, STORE, ADD, SUB, MUL, DIV, ADDI, SUBI, BRANCH
    }

    static class ReservationStation {
        String name;
        InstructionType type;
        String busy;
        String op;
        String vj;
        String vk;
        String qj;
        String qk;
        String dest;
    }

    static class LoadBuffer {
        String name;
        String busy;
        String address;
        String dest;
    }

    static class Register {
        String name;
        String value;
        String qi;
    }

    static class Instruction {
        InstructionType type;
        String dest;
        String src1;
        String src2;
        int address;
        int issueTime;
        int executeTime;
        int writeTime;
    }

    private TableView<ReservationStation> createReservationStationTable(String title) {
        TableView<ReservationStation> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Busy", "busy"),
                createColumn("Op", "op"),
                createColumn("Vj", "vj"),
                createColumn("Vk", "vk"),
                createColumn("Qj", "qj"),
                createColumn("Qk", "qk"),
                createColumn("Dest", "dest")
        );
        return table;
    }

    private TableView<LoadBuffer> createLoadBufferTable() {
        TableView<LoadBuffer> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Busy", "busy"),
                createColumn("Address", "address"),
                createColumn("Dest", "dest")
        );
        return table;
    }

    private TableView<Register> createRegisterTable() {
        TableView<Register> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Value", "value"),
                createColumn("Qi", "qi")
        );
        return table;
    }

    private TableView<Instruction> createInstructionQueueTable() {
        TableView<Instruction> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Type", "type"),
                createColumn("Dest", "dest"),
                createColumn("Src1", "src1"),
                createColumn("Src2", "src2"),
                createColumn("Issue", "issueTime"),
                createColumn("Execute", "executeTime"),
                createColumn("Write", "writeTime")
        );
        return table;
    }

    private <S,T> TableColumn<S,T> createColumn(String title, String property) {
        TableColumn<S,T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    public static void main(String[] args) {
        launch(args);
    }
}