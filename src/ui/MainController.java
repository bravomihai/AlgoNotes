package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import dao.SiteDAO;
import dao.ProblemDAO;
import dao.NoteDAO;
import model.Site;
import model.Problem;
import model.Note;

import java.sql.SQLException;
import java.util.List;

public class MainController {

    // ===== BROWSE =====
    @FXML private HBox browseView;
    @FXML private ListView<Site> sitesList;
    @FXML private ListView<Problem> problemsList;
    @FXML private TextArea noteArea;
    @FXML private Hyperlink selectedSiteLink;
    @FXML private Hyperlink problemLink;
    @FXML private Label difficultyLabel;
    @FXML private Label triesLabel;
    @FXML private Button addProblemButton;
    @FXML private Button editProblemButton;
    @FXML private Button editSiteButton;

    // ===== ADD SITE =====
    @FXML private VBox addSiteView;
    @FXML private TextField addSiteNameField;
    @FXML private TextField addSiteUrlField;

    // ===== EDIT SITE =====
    @FXML private VBox editSiteView;
    @FXML private TextField editSiteNameField;
    @FXML private TextField editSiteUrlField;

    // ===== ADD PROBLEM =====
    @FXML private VBox addProblemView;
    @FXML private TextField addProblemCodeField;
    @FXML private TextField addProblemTitleField;
    @FXML private TextField addProblemDifficultyField;
    @FXML private TextField addProblemLinkField;
    @FXML private Spinner<Integer> addProblemTriesSpinner;


    // ===== EDIT PROBLEM =====
    @FXML private VBox editProblemView;
    @FXML private TextField editProblemCodeField;
    @FXML private TextField editProblemTitleField;
    @FXML private TextField editProblemDifficultyField;
    @FXML private TextField editProblemLinkField;
    @FXML private Spinner<Integer> editProblemTriesSpinner;

    // ===== STATE =====
    private Site currentSite;
    private Problem currentProblem;

    // ===== DAO =====
    private final SiteDAO siteDAO = new SiteDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();
    private final NoteDAO noteDAO = new NoteDAO();

    // ===== INIT =====
    @FXML
    public void initialize() {
        addProblemButton.disableProperty().bind(
                sitesList.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
        );

        editProblemButton.disableProperty().bind(
                problemsList.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
        );

        editSiteButton.disableProperty().bind(
                sitesList.getSelectionModel()
                        .selectedItemProperty()
                        .isNull()
        );

        initNumericSpinner(addProblemTriesSpinner);
        initNumericSpinner(editProblemTriesSpinner);

        initListeners();
        refreshSites();
        show(browseView);
    }

    private void commitEditorText(Spinner<Integer> spinner) {
        String text = spinner.getEditor().getText();
        if (text == null || text.isBlank()) {
            spinner.getValueFactory().setValue(0);
        } else {
            spinner.getValueFactory().setValue(Integer.parseInt(text));
        }
    }

    private void initNumericSpinner(Spinner<Integer> spinner) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1_000_000, 0);

        spinner.setValueFactory(factory);
        spinner.setEditable(true);

        TextField editor = spinner.getEditor();

        editor.textProperty().addListener((obs, old, val) -> {
            if (!val.matches("\\d*")) {
                editor.setText(val.replaceAll("[^\\d]", ""));
            }
        });

        editor.focusedProperty().addListener((obs, old, focused) -> {
            if (!focused) {
                commitEditorText(spinner);
            }
        });
    }

    // ===== VIEW SWITCH =====
    private void hideAll() {
        hide(browseView);
        hide(addSiteView);
        hide(editSiteView);
        hide(addProblemView);
        hide(editProblemView);
    }

    private void show(Region r) {
        hideAll();
        r.setVisible(true);
        r.setManaged(true);
    }

    private void hide(Region r) {
        r.setVisible(false);
        r.setManaged(false);
    }

    // ===== SITE =====
    @FXML
    private void onAddSite() {
        addSiteNameField.clear();
        addSiteUrlField.clear();
        show(addSiteView);
    }

    @FXML
    private void onEditSite() {
        if (currentSite == null) return;
        editSiteNameField.setText(currentSite.getName());
        editSiteUrlField.setText(currentSite.getUrl());
        show(editSiteView);
    }

    @FXML
    private void onSaveAddSite() throws SQLException {
        siteDAO.insert(new Site(0,
                addSiteNameField.getText(),
                addSiteUrlField.getText()));
        refreshSites();
        show(browseView);
    }

    @FXML
    private void onSaveEditSite() throws SQLException {
        currentSite.setName(editSiteNameField.getText());
        currentSite.setUrl(editSiteUrlField.getText());
        siteDAO.update(currentSite);
        refreshSites();
        show(browseView);
    }

    @FXML
    private void onRemoveSite() {
        if (currentSite == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Site");
        alert.setHeaderText("Remove site: " + currentSite.getName());
        alert.setContentText(
                "All problems and notes for this site will be deleted.\n" +
                        "This action cannot be undone."
        );

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    siteDAO.deleteById(currentSite.getId());
                    currentSite = null;
                    problemsList.getItems().clear();
                    refreshSites();
                    show(browseView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ===== PROBLEM =====
    @FXML
    private void onAddProblem() {
        if (currentSite == null) return;
        addProblemCodeField.clear();
        addProblemTitleField.clear();
        addProblemDifficultyField.clear();
        addProblemLinkField.clear();
        addProblemTriesSpinner.getValueFactory().setValue(0);
        show(addProblemView);
    }

    @FXML
    private void onEditProblem() {
        if (currentProblem == null) return;
        editProblemCodeField.setText(currentProblem.getCode());
        editProblemTitleField.setText(currentProblem.getTitle());
        editProblemDifficultyField.setText(currentProblem.getDifficulty());
        editProblemLinkField.setText(currentProblem.getLink());
        editProblemTriesSpinner.getValueFactory().setValue(currentProblem.getTries());
        show(editProblemView);
    }

    @FXML
    private void onSaveAddProblem() throws SQLException {
        problemDAO.insert(new Problem(
                0,
                currentSite.getId(),
                addProblemCodeField.getText(),
                addProblemTitleField.getText(),
                addProblemDifficultyField.getText(),
                addProblemLinkField.getText(),
                addProblemTriesSpinner.getValue()
        ));
        refreshProblems();
        show(browseView);
    }

    @FXML
    private void onSaveEditProblem() throws SQLException {
        currentProblem.setCode(editProblemCodeField.getText());
        currentProblem.setTitle(editProblemTitleField.getText());
        currentProblem.setDifficulty(editProblemDifficultyField.getText());
        currentProblem.setLink(editProblemLinkField.getText());
        currentProblem.setTries(editProblemTriesSpinner.getValue());
        problemDAO.update(currentProblem);
        refreshProblems();
        show(browseView);
    }

    @FXML
    private void onRemoveProblem() {
        if (currentProblem == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Problem");
        alert.setHeaderText("Remove problem: " + currentProblem.getCode());
        alert.setContentText(
                "All notes for this problem will be deleted.\n" +
                        "This action cannot be undone."
        );

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    problemDAO.deleteById(currentProblem.getId());
                    currentProblem = null;
                    refreshProblems();
                    noteArea.clear();
                    show(browseView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    // ===== NOTE =====
    @FXML
    private void onSaveNote() throws SQLException {
        if (currentProblem == null) return;

        String content = noteArea.getText();

        List<Note> notes = noteDAO.findByProblemId(currentProblem.getId());

        if (notes.isEmpty()) {
            noteDAO.insert(new Note(
                    currentProblem.getId(),
                    content
            ));
        } else {
            Note note = notes.get(0);
            note.setContent(content);
            noteDAO.update(note);
        }
    }


    // ===== NAV =====
    @FXML
    private void onCancel() {
        show(browseView);
    }

    // ===== LISTENERS =====
    private void initListeners() {

        sitesList.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, a, site) -> {
                    currentSite = site;
                    if (site == null) return;
                    selectedSiteLink.setText(site.getUrl());
                    refreshProblems();
                });

        problemsList.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, a, p) -> {
                    currentProblem = p;
                    if (p == null) return;
                    difficultyLabel.setText(p.getDifficulty());
                    triesLabel.setText(String.valueOf(p.getTries()));
                    problemLink.setText(p.getLink());
                    loadNote();
                });
    }

    // ===== HELPERS =====
    private void refreshSites() {
        try {
            sitesList.getItems().setAll(siteDAO.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshProblems() {
        if (currentSite == null) return;
        try {
            problemsList.getItems().setAll(
                    problemDAO.findBySiteId(currentSite.getId()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadNote() {
        try {
            List<Note> notes = noteDAO.findByProblemId(currentProblem.getId());
            noteArea.setText(notes.isEmpty() ? "" : notes.get(0).getContent());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== CLIPBOARD =====
    @FXML
    private void onCopySiteLink() {
        copy(selectedSiteLink.getText());
    }

    @FXML
    private void onCopyProblemLink() {
        copy(problemLink.getText());
    }

    private void copy(String s) {
        ClipboardContent c = new ClipboardContent();
        c.putString(s);
        Clipboard.getSystemClipboard().setContent(c);
    }
}
