package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import dao.SiteDAO;
import dao.ProblemDAO;
import dao.NoteDAO;
import model.Site;
import model.Problem;
import model.Note;

import java.sql.SQLException;
import java.util.List;

public class MainController {

    @FXML private StackPane overlay;
    @FXML private HBox browseView;
    @FXML private Pane dimPane;

    @FXML private ListView<Site> sitesList;
    @FXML private ListView<Problem> problemsList;

    @FXML private TextArea noteArea;
    @FXML private Hyperlink selectedSiteLink;
    @FXML private Hyperlink problemLink;
    @FXML private Label difficultyLabel;
    @FXML private Label triesLabel;
    @FXML private Label dateAddedLabel;
    @FXML private Label lastUpdatedLabel;

    @FXML private TextField siteSearchField;
    @FXML private TextField problemSearchField;
    private FilteredList<Site> filteredSites;
    private FilteredList<Problem> filteredProblems;

    private final ObservableList<Site> allSites =
            FXCollections.observableArrayList();

    private final ObservableList<Problem> allProblems =
            FXCollections.observableArrayList();

    private Site currentSite;
    private Problem currentProblem;

    private final SiteDAO siteDAO = new SiteDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();
    private final NoteDAO noteDAO = new NoteDAO();

    @FXML private Button addProblemButton;
    @FXML private Button editProblemButton;
    @FXML private Button editSiteButton;
    @FXML private Button saveNoteButton;

    @FXML
    public void initialize() {
        problemSearchField.disableProperty().bind(
                sitesList.getSelectionModel().selectedItemProperty().isNull()
        );

        saveNoteButton.disableProperty().bind(
                problemsList.getSelectionModel().selectedItemProperty().isNull()
        );
        editSiteButton.disableProperty().bind(
                sitesList.getSelectionModel().selectedItemProperty().isNull()
        );
        addProblemButton.disableProperty().bind(
                sitesList.getSelectionModel().selectedItemProperty().isNull()
        );
        editProblemButton.disableProperty().bind(
                problemsList.getSelectionModel().selectedItemProperty().isNull()
        );
        noteArea.disableProperty().bind(
                problemsList.getSelectionModel().selectedItemProperty().isNull()
        );

        filteredSites = new FilteredList<>(allSites, s -> true);
        sitesList.setItems(filteredSites);

        filteredProblems = new FilteredList<>(allProblems, p -> true);
        problemsList.setItems(filteredProblems);

        siteSearchField.textProperty().addListener((obs, old, text) -> {
            String q = text.toLowerCase().trim();
            filteredSites.setPredicate(site ->
                    q.isEmpty() || site.getName().toLowerCase().contains(q)
            );
        });

        problemSearchField.textProperty().addListener((obs, old, text) -> {
            String q = text.toLowerCase().trim();
            filteredProblems.setPredicate(p ->
                    q.isEmpty() || p.getCode().toLowerCase().contains(q)
            );
        });

        initListeners();

        refreshSites();

        overlay.getChildren().setAll(browseView, dimPane);
    }


    @FXML
    private void onAddSite() {
        openSiteModal(null);
    }

    @FXML
    private void onEditSite() {
        if (currentSite != null)
            openSiteModal(currentSite);
    }

    @FXML
    private void onAddProblem() {
        if (currentSite != null)
            openProblemModal(null);
    }

    @FXML
    private void onEditProblem() {
        if (currentProblem != null)
            openProblemModal(currentProblem);
    }

    private void openSiteModal(Site site) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add_edit_site.fxml"));
            Parent view = loader.load();

            AddEditSiteController c = loader.getController();
            c.init(site, siteDAO, this::onSiteDeleted, this::refreshSites, this::onSiteAdded, this::closeModal);

            showModal(view);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openProblemModal(Problem p) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("add_edit_problem.fxml"));

            Parent view = loader.load();

            AddEditProblemController controller =
                    loader.getController();

            controller.init(
                    p,
                    currentSite,
                    problemDAO,
                    this::onProblemDeleted,
                    this::refreshProblems,
                    this::onProblemAdded,
                    this::closeModal
            );

            showModal(view);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showModal(Parent modal) {
        browseView.setDisable(true);

        dimPane.setVisible(true);
        dimPane.setManaged(true);

        overlay.getChildren().add(modal);
    }

    private void closeModal() {
        // scoate ultimul copil (modalul)
        if (overlay.getChildren().size() > 2) {
            overlay.getChildren().remove(overlay.getChildren().size() - 1);
        }

        browseView.setDisable(false);

        dimPane.setVisible(false);
        dimPane.setManaged(false);
    }

    private void initListeners() {
        sitesList.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            currentSite = s;
            if (s == null) return;
            selectedSiteLink.setText(s.getUrl());
            refreshProblems();
            problemSearchField.clear();
        });

        problemsList.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> {
            currentProblem = p;
            if (p == null) return;
            difficultyLabel.setText(p.getDifficulty());
            triesLabel.setText(String.valueOf(p.getTries()));
            problemLink.setText(p.getLink());
            loadNote();
        });

        problemSearchField.textProperty().addListener((obs, old, text) -> {
            String q = text.toLowerCase().trim();

            filteredProblems.setPredicate(p ->
                    q.isEmpty() ||
                            p.getCode().toLowerCase().contains(q)
            );
        });

    }

    private void refreshSites() {
        try {
            allSites.setAll(siteDAO.findAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshProblems() {
        if (currentSite == null) {
            allProblems.clear();
            return;
        }

        try {
            allProblems.setAll(
                    problemDAO.findBySiteId(currentSite.getId())
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void onSiteDeleted() {
        currentSite = null;
        currentProblem = null;

        sitesList.getSelectionModel().clearSelection();
        problemsList.getSelectionModel().clearSelection();

        allProblems.clear();
        problemSearchField.clear();

        noteArea.clear();
        difficultyLabel.setText("-");
        triesLabel.setText("-");
        dateAddedLabel.setText("-");
        lastUpdatedLabel.setText("-");
    }

    private void onProblemAdded(Problem problem) {
        refreshProblems();

        problemsList.getItems().stream()
                .filter(p -> p.getId() == problem.getId())
                .findFirst()
                .ifPresent(p -> {
                    problemsList.getSelectionModel().select(p);
                    problemsList.scrollTo(p);
                });
    }



    private void onProblemDeleted(){
        currentProblem = null;
        problemsList.getSelectionModel().clearSelection();

        noteArea.clear();
        noteArea.setDisable(true);

        difficultyLabel.setText("-");
        triesLabel.setText("-");
        dateAddedLabel.setText("-");
        lastUpdatedLabel.setText("-");
    }

    private void loadNote() {
        if (currentProblem == null) {
            noteArea.clear();
            dateAddedLabel.setText("-");
            lastUpdatedLabel.setText("-");
            return;
        }
        try {
            List<Note> notes = noteDAO.findByProblemId(currentProblem.getId());
            noteArea.setText(notes.isEmpty() ? "" : notes.get(0).getContent());
            if (notes.isEmpty()) {
                noteArea.setText("");
                dateAddedLabel.setText("-");
                lastUpdatedLabel.setText("-");
            } else {
                Note n = notes.get(0);
                noteArea.setText(n.getContent());
                dateAddedLabel.setText(n.getDateAdded());
                lastUpdatedLabel.setText(
                        n.getLastUpdated() == null ? "-" : n.getLastUpdated()
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSaveNote() throws SQLException {
        if (currentProblem == null) return;
        String content = noteArea.getText();
        List<Note> notes = noteDAO.findByProblemId(currentProblem.getId());
        if (notes.isEmpty()) {
            noteDAO.insert(new Note(currentProblem.getId(), content));
        } else {
            Note n = notes.get(0);
            n.setContent(content);
            noteDAO.update(n);
        }
        loadNote();

    }

    private void onSiteAdded(Site site) {
        refreshSites();

        sitesList.getItems().stream()
                .filter(s -> s.getId() == site.getId())
                .findFirst()
                .ifPresent(s -> {
                    sitesList.getSelectionModel().select(s);
                    sitesList.scrollTo(s);
                });
    }


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
