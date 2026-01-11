package ui;

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

    private Site currentSite;
    private Problem currentProblem;

    private final SiteDAO siteDAO = new SiteDAO();
    private final ProblemDAO problemDAO = new ProblemDAO();
    private final NoteDAO noteDAO = new NoteDAO();

    @FXML private Button addProblemButton;
    @FXML private Button editProblemButton;
    @FXML private Button editSiteButton;

    @FXML
    public void initialize() {

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

        initListeners();
        refreshSites();
        overlay.getChildren().setAll(browseView, dimPane);

    }


    /* ================= OPEN MODALS ================= */

    @FXML
    private void onAddSite() {
        openSiteModal(null);
    }

    @FXML
    private void onEditSite() {
        if (currentSite != null)
            openSiteModal(currentSite);
    }

    private void onSiteRemoved() {
        currentSite = null;
        currentProblem = null;

        sitesList.getSelectionModel().clearSelection();
        problemsList.getItems().clear();

        difficultyLabel.setText("");
        triesLabel.setText("");
        problemLink.setText("");
        noteArea.clear();

        refreshSites();
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
            c.init(site, siteDAO, this::onSiteRemoved, this::refreshSites, this::closeModal);

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
                    loader.getController(); // FĂRĂ cast manual

            controller.init(
                    p,
                    currentSite,
                    problemDAO,
                    this::refreshProblems,
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

    /* ================= LISTENERS ================= */

    private void initListeners() {
        sitesList.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            currentSite = s;
            if (s == null) return;
            selectedSiteLink.setText(s.getUrl());
            refreshProblems();
        });

        problemsList.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> {
            currentProblem = p;
            if (p == null) return;
            difficultyLabel.setText(p.getDifficulty());
            triesLabel.setText(String.valueOf(p.getTries()));
            problemLink.setText(p.getLink());
            loadNote();
        });
    }

    /* ================= DATA ================= */

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
            problemsList.getItems().setAll(problemDAO.findBySiteId(currentSite.getId()));
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
    }

    /* ================= CLIPBOARD ================= */

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
