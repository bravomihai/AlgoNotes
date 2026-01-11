package ui;

import dao.ProblemDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Problem;
import model.Site;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

public class AddEditProblemController {

    @FXML private Label titleLabel;
    @FXML private TextField codeField;
    @FXML private TextField titleField;
    @FXML private TextField difficultyField;
    @FXML private TextField linkField;
    @FXML private Spinner<Integer> triesSpinner;
    @FXML private Button deleteButton;

    private Problem problem;
    private Site site;
    private ProblemDAO dao;
    private Runnable onSuccess;
    private Runnable onClose;
    private Runnable onProblemDeleted;
    private Consumer<Problem> onAdded;

    public void init(Problem p, Site site, ProblemDAO dao, Runnable onProblemDeleted,
                     Runnable onSuccess, Consumer<Problem> onAdded, Runnable onClose) {
        this.problem = p;
        this.site = site;
        this.dao = dao;
        this.onSuccess = onSuccess;
        this.onClose = onClose;
        this.onAdded = onAdded;
        this.onProblemDeleted = onProblemDeleted;

        triesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1_000_000, 0)
        );

        if (p == null) {
            titleLabel.setText("Add Problem");
            deleteButton.setVisible(false);
        } else {
            titleLabel.setText("Edit Problem");
            codeField.setText(p.getCode());
            titleField.setText(p.getTitle());
            difficultyField.setText(p.getDifficulty());
            linkField.setText(p.getLink());
            triesSpinner.getValueFactory().setValue(p.getTries());
        }
    }

    @FXML
    private void onSave() {

        String code = codeField.getText();
        String title = titleField.getText();

        if (code == null || code.isBlank() ||
                title == null || title.isBlank()) {

            showError("Code and title are required.");
            return;
        }
        Problem saved = problem;
        try {
            if (problem == null) {
                // ADD
                Problem newProblem = new Problem(
                        0,
                        site.getId(),
                        code.trim(),
                        title.trim(),
                        difficultyField.getText(),
                        linkField.getText(),
                        triesSpinner.getValue()
                );

                saved = dao.insert(newProblem);
                onAdded.accept(saved);

            } else {
                // EDIT
                problem.setCode(code.trim());
                problem.setTitle(title.trim());
                problem.setDifficulty(difficultyField.getText());
                problem.setLink(linkField.getText());
                problem.setTries(triesSpinner.getValue());

                dao.update(problem);
            }

            onSuccess.run(); // refreshProblems()
            onAdded.accept(saved);
            onClose.run();   // closeModal()

        } catch (SQLException e) {

            // SQLite UNIQUE(site_id, code)
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
                showError(
                        "A problem with this code already exists on this site."
                );

                codeField.requestFocus();
                codeField.selectAll();

            } else {
                showError("Database error while saving problem.");
            }
        }
    }

    @FXML
    private void onDelete() {
        if (problem == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove problem");
        alert.setHeaderText("Remove problem: " + problem.getCode());
        alert.setContentText(
                "This will delete the problem and all its notes.\n" +
                        "This action cannot be undone."
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // user cancelled
        }

        try {
            dao.deleteById(problem.getId());
            onSuccess.run();
            onProblemDeleted.run();
            onClose.run();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to remove problem");
        }
    }

    @FXML
    private void onCancel() {
        onClose.run();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
