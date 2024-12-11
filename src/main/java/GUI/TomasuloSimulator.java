package GUI;

import Core.Instruction.Instruction;
import Core.InstructionFileParser;
import Core.Register.RegisterEntry;
import Core.Storage.ArithmeticRSEntry;
import Core.Storage.LoadRSEntry;
import Core.Storage.StoreRSEntry;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;
import Core.Instruction.InstructionType;

public class TomasuloSimulator extends Application {
    //core components
    List<Instruction> instructions = new ArrayList<>();

    // Configuration parameters
    public int loadBufferSize = 3;
    public int storeBufferSize =3;
    public int addReservationStationSize = 3;
    public int mulReservationStationSize = 2;
    public int integerReservationStationSize = 2;
    public int clkCycles=0;
    // Latencies
    public Map<InstructionType, Integer> latencies = new HashMap<>();

    //#region Components
    public TableView<Instruction> instructionQueueTable;
    public TableView<ArithmeticRSEntry> addResStationTable;
    public TableView<ArithmeticRSEntry> mulResStationTable;
    public TableView<ArithmeticRSEntry> intResStationTable;
    public TableView<LoadRSEntry> loadBufferTable;
    public TableView<StoreRSEntry> storeBufferTable;
    public TableView<RegisterEntry> registerTable;
    //#endregion

    private TextField clockCycleField;

    // Cache configuration
    public int cacheSize = 1024; // bytes
    public int blockSize = 64;   // bytes
    public int hitLatency = 1;   // cycles
    public int missPenalty = 10; // cycles

    private void initializeCore() {
        instructions = InstructionFileParser.fillInstructionsQueue("src/main/java/Core/program.txt");
        instructionQueueTable = createInstructionQueueTable();
    }

    @Override
    public void start(Stage primaryStage) {
        initializeCore();
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        root.getChildren().add(createConfigurationSection());
        root.getChildren().add(createTablesSection());
        root.getChildren().add(createControlButtons());

        // Wrap the VBox in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, 800, 600);
        primaryStage.setTitle("Tomasulo Algorithm Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createConfigurationSection() {
        VBox config = new VBox(5);
        config.getChildren().addAll(
                new Label("Latency Configurations:"),
                createLatencyInputs(),
                new Label("Station Sizes:"),
                createStationSizeInputs(),
                new Label("Cache Confgurations:"),
                createCacheConfigInputs()
        );
        return config;
    }

    private GridPane createLatencyInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        int row = 0;
        int col = 0;

        for (InstructionType type : InstructionType.values()) {
            grid.add(new Label(type.toString() + " Latency:"), col, row);

            TextField latencyField = new TextField("1");
            latencyField.setPrefWidth(50);
            grid.add(latencyField, col + 1, row);
            col += 2;
            if (col >= 10) {
                col = 0;
                row++;
            }
        }
        return grid;
    }

    private GridPane createStationSizeInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        int col = 0;
        grid.add(new Label("Load RS Size:"), col, 0);
        TextField loadField = new TextField(String.valueOf(loadBufferSize));
        loadField.setPrefWidth(50);
        grid.add(loadField, col + 1, 0);
        col += 2;

        grid.add(new Label("Store RS Size:"), col, 0);
        TextField storeField = new TextField(String.valueOf(storeBufferSize));
        storeField.setPrefWidth(50);
        grid.add(storeField, col + 1, 0);
        col += 2;

        grid.add(new Label("Add/Sub RS Size:"), col, 0);
        TextField addSubField = new TextField(String.valueOf(addReservationStationSize));
        addSubField.setPrefWidth(50);
        grid.add(addSubField, col + 1, 0);
        col += 2;

        grid.add(new Label("Mul/Div RS Size:"), col, 0);
        TextField mulDivField = new TextField(String.valueOf(mulReservationStationSize));
        mulDivField.setPrefWidth(50);
        grid.add(mulDivField, col + 1, 0);

        return grid;
    }

    private GridPane createCacheConfigInputs() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        int col = 0;

        grid.add(new Label("Cache Size (bytes):"), col, 0);
        TextField cacheSizeField = new TextField(String.valueOf(cacheSize));
        cacheSizeField.setPrefWidth(50);
        grid.add(cacheSizeField, col + 1, 0);
        col += 2;

        grid.add(new Label("Block Size (bytes):"), col, 0);
        TextField blockSizeField = new TextField(String.valueOf(blockSize));
        blockSizeField.setPrefWidth(50);
        grid.add(blockSizeField, col + 1, 0);
        col += 2;

        grid.add(new Label("Clock Cycle:"), col, 0);
        clockCycleField = new TextField(String.valueOf(clkCycles));
        clockCycleField.setEditable(false);
        clockCycleField.setStyle("-fx-background-color: lightgray;");
        grid.add(clockCycleField, col + 1, 0);

        return grid;
    }

    private VBox createTablesSection() {
        VBox tables = new VBox(10);

        Label instLabel = new Label("Instruction queue table");
        instLabel.setStyle("-fx-font-weight: bold;");
        instructionQueueTable.setItems(FXCollections.observableList(instructions));
        Label registerLabel = new Label("Register table");
        registerLabel.setStyle("-fx-font-weight: bold;");
        registerTable = createRegisterTable();
        Label addSubLabel = new Label("ADD/SUB reservation station");
        addSubLabel.setStyle("-fx-font-weight: bold;");
        addResStationTable = createReservationStationTable();
        Label mulDivLabel = new Label("MUL/DIV reservation station");
        mulDivLabel.setStyle("-fx-font-weight: bold;");
        mulResStationTable = createReservationStationTable();
        Label intLabel = new Label("Integer reservation station");
        intLabel.setStyle("-fx-font-weight: bold;");
        intResStationTable = createReservationStationTable();
        Label loadLabel = new Label("Load reservation station");
        loadLabel.setStyle("-fx-font-weight: bold;");
        loadBufferTable = createLoadBufferTable();
        Label storeLabel = new Label("Store reservation station");
        storeLabel.setStyle("-fx-font-weight: bold;");
        storeBufferTable = createStoreBufferTable();


        tables.getChildren().addAll(     instLabel,
                instructionQueueTable,
                registerLabel,
                registerTable,
                addSubLabel,
                addResStationTable,
                mulDivLabel,
                mulResStationTable,
                intLabel,
                intResStationTable,
                loadLabel,
                loadBufferTable,
                storeLabel,
                storeBufferTable
        );

        return tables;
    }

    private HBox createControlButtons() {
        HBox button = new HBox(10);
        Button save = new Button("Save inputs");
        Button stepButton = new Button("Next Cycle");

        stepButton.setOnAction(e -> {
            clkCycles++;
            clockCycleField.setText(String.valueOf(clkCycles));
        });

        button.getChildren().addAll(save, stepButton);

        return button;
    }

    private TableView<ArithmeticRSEntry> createReservationStationTable() {
        TableView<ArithmeticRSEntry> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Busy", "busy"),
                createColumn("Op", "op"),
                createColumn("Vj", "vj"),
                createColumn("Vk", "vk"),
                createColumn("Qj", "qj"),
                createColumn("Qk", "qk")
        );
        return table;
    }

    private TableView<LoadRSEntry> createLoadBufferTable() {
        TableView<LoadRSEntry> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Tag", "tag"),
                createColumn("Busy", "busy"),
                createColumn("Address", "address")
        );
        return table;
    }

    private TableView<StoreRSEntry> createStoreBufferTable() {
        TableView<StoreRSEntry> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Tag", "tag"),
                createColumn("Busy", "busy"),
                createColumn("Address", "address"),
                createColumn("V", "v"),
                createColumn("Q","q")
        );
        return table;
    }

    private TableView<RegisterEntry> createRegisterTable() {
        TableView<RegisterEntry> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Name", "name"),
                createColumn("Value", "value"),
                createColumn("Q", "q")
        );
        return table;
    }

    private TableView<Instruction> createInstructionQueueTable() {
        TableView<Instruction> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Type", "instruction.op"),  // Assuming `op` is part of `Instruction`
                createColumn("Dest", "instruction.dest"),
                createColumn("Src1", "instruction.j"),
                createColumn("Src2", "instruction.k"),
                createColumn("Issue", "issue"),
                createColumn("Execute", "execution"),  // This would require a custom cell value factory for lists
                createColumn("Write", "write")
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