package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.print.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends Application {

    private TextField nameField, rollNumberField;
    private ComboBox<String> semesterComboBox;
    private VBox subjectsContainer;
    private TextArea resultArea;
    private TableView<Student> studentTable;
    private Label statusLabel;

    private ObservableList<Student> students = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Result Generator");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // Top section
        VBox topSection = createTopSection();
        mainLayout.setTop(topSection);

        // Center section
        SplitPane centerSection = new SplitPane();
        
        VBox leftPane = createLeftPane();
        VBox rightPane = createRightPane();

        centerSection.getItems().addAll(leftPane, rightPane);
        centerSection.setDividerPositions(0.6);
        mainLayout.setCenter(centerSection);

        // Bottom section
        HBox bottomSection = createBottomSection();
        mainLayout.setBottom(bottomSection);

        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createTopSection() {
        VBox topSection = new VBox(10);
        topSection.setPadding(new Insets(0, 0, 20, 0));

        Label titleLabel = new Label("Student Result Generator");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        topSection.getChildren().add(titleLabel);
        return topSection;
    }

    private VBox createLeftPane() {
        VBox leftPane = new VBox(20);
        leftPane.setPadding(new Insets(10));

        TitledPane inputSection = createInputSection();
        studentTable = createStudentTable();
        VBox.setVgrow(studentTable, Priority.ALWAYS);

        leftPane.getChildren().addAll(inputSection, studentTable);
        return leftPane;
    }

    private TitledPane createInputSection() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        GridPane inputGrid = createInputGrid();
        content.getChildren().add(inputGrid);

        subjectsContainer = new VBox(5);
        ScrollPane subjectsScroll = new ScrollPane(subjectsContainer);
        subjectsScroll.setFitToWidth(true);
        subjectsScroll.setPrefViewportHeight(150);
        content.getChildren().add(subjectsScroll);

        HBox subjectButtonBox = createSubjectButtonBox();
        content.getChildren().add(subjectButtonBox);

        HBox actionButtonBox = createActionButtonBox();
        content.getChildren().add(actionButtonBox);

        TitledPane titledPane = new TitledPane("Student Information", content);
        titledPane.setCollapsible(false);
        return titledPane;
    }

    private GridPane createInputGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Name:"), 0, 0);
        nameField = new TextField();
        grid.add(nameField, 1, 0);

        grid.add(new Label("Roll Number:"), 0, 1);
        rollNumberField = new TextField();
        grid.add(rollNumberField, 1, 1);

        grid.add(new Label("Semester:"), 0, 2);
        semesterComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th"
        ));
        grid.add(semesterComboBox, 1, 2);

        return grid;
    }

    private HBox createSubjectButtonBox() {
        HBox buttonBox = new HBox(10);

        Button addSubjectButton = new Button("Add Subject");
        addSubjectButton.setOnAction(e -> addSubjectField());

        buttonBox.getChildren().addAll(addSubjectButton);
        return buttonBox;
    }

    private void addSubjectField() {
        HBox subjectBox = new HBox(10);
        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject Name");
        TextField scoreField = new TextField();
        scoreField.setPromptText("Score (Max 70)");
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(e -> subjectsContainer.getChildren().remove(subjectBox));

        subjectBox.getChildren().addAll(subjectField, scoreField, removeButton);
        subjectsContainer.getChildren().add(subjectBox);
    }

    private HBox createActionButtonBox() {
        HBox buttonBox = new HBox(10);

        Button addButton = new Button("Add Student");
        addButton.setOnAction(e -> addStudent());

        Button clearInputButton = new Button("Clear Input");
        clearInputButton.setOnAction(e -> clearInputFields());

        buttonBox.getChildren().addAll(addButton, clearInputButton);
        return buttonBox;
    }

    private TableView<Student> createStudentTable() {
        TableView<Student> table = new TableView<>();
        table.setItems(students);

        TableColumn<Student, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Student, String> rollNumberCol = new TableColumn<>("Roll Number");
        rollNumberCol.setCellValueFactory(cellData -> cellData.getValue().rollNumberProperty());

        TableColumn<Student, String> semesterCol = new TableColumn<>("Semester");
        semesterCol.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());

        table.getColumns().addAll(nameCol, rollNumberCol, semesterCol);
        return table;
    }

    private VBox createRightPane() {
        VBox rightPane = new VBox(20);
        rightPane.setPadding(new Insets(10));

        Label resultLabel = new Label("Generated Results:");
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        rightPane.getChildren().add(resultLabel);

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        VBox.setVgrow(resultArea, Priority.ALWAYS);
        rightPane.getChildren().add(resultArea);

        HBox buttonBox = new HBox(10);
        Button generateButton = new Button("Generate Results");
        generateButton.setOnAction(e -> generateResults());
        generateButton.setMaxWidth(Double.MAX_VALUE);
        
        Button printButton = new Button("Print Results");
        printButton.setOnAction(e -> printResults());
        
        Button saveButton = new Button("Save Results");
        saveButton.setOnAction(e -> saveResults());
        
        buttonBox.getChildren().addAll(generateButton, printButton, saveButton);
        rightPane.getChildren().add(buttonBox);

        return rightPane;
    }

    private HBox createBottomSection() {
        HBox bottomSection = new HBox(10);
        bottomSection.setPadding(new Insets(20, 0, 0, 0));

        statusLabel = new Label("Ready");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        Button clearTableButton = new Button("Clear All Data");
        clearTableButton.setOnAction(e -> clearAllData());

        bottomSection.getChildren().addAll(statusLabel, clearTableButton);
        return bottomSection;
    }

    private void addStudent() {
        String name = nameField.getText();
        String rollNumber = rollNumberField.getText();
        String semester = semesterComboBox.getValue();

        if (name.isEmpty() || rollNumber.isEmpty() || semester == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        Student student = new Student(name, rollNumber, semester);
        for (javafx.scene.Node node : subjectsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox subjectBox = (HBox) node;
                TextField subjectField = (TextField) subjectBox.getChildren().get(0);
                TextField scoreField = (TextField) subjectBox.getChildren().get(1);
                
                String subject = subjectField.getText();
                if (subject.isEmpty()) {
                    showAlert("Error", "Please enter a subject name for all fields.");
                    return;
                }
                
                try {
                    int score = Integer.parseInt(scoreField.getText());
                    if (score < 0 || score > 70) {
                        showAlert("Error", "Score must be between 0 and 70.");
                        return;
                    }
                    student.addSubjectScore(subject, score);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid scores for all subjects.");
                    return;
                }
            }
        }

        students.add(student);
        clearInputFields();
        updateStatus("Student added successfully.");
    }

    private void generateResults() {
        StringBuilder result = new StringBuilder();
        for (Student student : students) {
            result.append(student.generateResult()).append("\n\n");
        }
        resultArea.setText(result.toString());
        updateStatus("Results generated successfully.");
    }

    private void printResults() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(resultArea);
            if (success) {
                job.endJob();
                updateStatus("Results printed successfully.");
            } else {
                updateStatus("Printing failed.");
            }
        } else {
            updateStatus("Unable to create printer job.");
        }
    }

    private void saveResults() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Results");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println(resultArea.getText());
                updateStatus("Results saved successfully.");
            } catch (IOException e) {
                showAlert("Error", "Failed to save file: " + e.getMessage());
                updateStatus("Failed to save results.");
            }
        }
    }

    private void clearInputFields() {
        nameField.clear();
        rollNumberField.clear();
        semesterComboBox.getSelectionModel().clearSelection();
        subjectsContainer.getChildren().clear();
        updateStatus("Input fields cleared.");
    }

    private void clearAllData() {
        students.clear();
        resultArea.clear();
        clearInputFields();
        updateStatus("All data cleared.");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showAlert(String title, String message) {
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

class Student {
    private final javafx.beans.property.StringProperty name;
    private final javafx.beans.property.StringProperty rollNumber;
    private final javafx.beans.property.StringProperty semester;
    private final javafx.collections.ObservableMap<String, Integer> subjectScores;

    public Student(String name, String rollNumber, String semester) {
        this.name = new javafx.beans.property.SimpleStringProperty(name);
        this.rollNumber = new javafx.beans.property.SimpleStringProperty(rollNumber);
        this.semester = new javafx.beans.property.SimpleStringProperty(semester);
        this.subjectScores = FXCollections.observableHashMap();
    }

    public void addSubjectScore(String subject, int score) {
        subjectScores.put(subject, score);
    }

    public String generateResult() {
        StringBuilder result = new StringBuilder();
        result.append("Student Details:\n");
        result.append("----------------\n");
        result.append("Name: ").append(name.get()).append("\n");
        result.append("Roll Number: ").append(rollNumber.get()).append("\n");
        result.append("Semester: ").append(semester.get()).append("\n\n");
        
        result.append("Subject Scores:\n");
        result.append("---------------\n");
        int totalScore = 0;
        int totalMaxScore = 0;
        for (String subject : subjectScores.keySet()) {
            int score = subjectScores.get(subject);
            result.append(String.format("%-15s: %3d / 70\n", subject, score));
            totalScore += score;
            totalMaxScore += 70;
        }

        double percentageScore = (double) totalScore / totalMaxScore * 100;
        result.append("\nResult Summary:\n");
        result.append("---------------\n");
        result.append(String.format("Total Score:    %3d / %d\n", totalScore, totalMaxScore));
        result.append(String.format("Percentage:     %5.2f%%\n", percentageScore));

        String overallGrade = calculateOverallGrade(percentageScore);
        result.append(String.format("Overall Grade:  %s\n", overallGrade));

        return result.toString();
    }

    private String calculateOverallGrade(double percentageScore) {
        if (percentageScore >= 90) return "A+ (Outstanding)";
        if (percentageScore >= 80) return "A  (Excellent)";
        if (percentageScore >= 70) return "B  (Very Good)";
        if (percentageScore >= 60) return "C  (Good)";
        if (percentageScore >= 50) return "D  (Satisfactory)";
        return "F  (Fail)";
    }

    public javafx.beans.property.StringProperty nameProperty() {
        return name;
    }

    public javafx.beans.property.StringProperty rollNumberProperty() {
        return rollNumber;
    }

    public javafx.beans.property.StringProperty semesterProperty() {
        return semester;
    }
}