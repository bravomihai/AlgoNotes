package ui;

import dao.SiteDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Site;

import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

public class AddEditSiteController {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField urlField;
    @FXML private Button deleteButton;
    private Runnable onSiteDeleted;
    private Consumer<Site> onAdded;

    private Site site;
    private SiteDAO dao;
    private Runnable onSuccess;
    private Runnable onClose;

    public void init(Site site, SiteDAO dao, Runnable onSiteDeleted, Runnable onSuccess, Consumer<Site> onAdded, Runnable onClose) {
        this.site = site;
        this.dao = dao;
        this.onSuccess = onSuccess;
        this.onClose = onClose;
        this.onAdded = onAdded;
        this.onSiteDeleted = onSiteDeleted;

        if (site == null) {
            titleLabel.setText("Add Site");
            deleteButton.setVisible(false);
        } else {
            titleLabel.setText("Edit Site");
            nameField.setText(site.getName());
            urlField.setText(site.getUrl());
        }
    }

    @FXML
    private void onSave() {
        if (nameField.getText().isBlank()) {
            showError("Site name is required");
            return;
        }
        Site saved = site;
        try {
            if (site == null) {
                Site newSite = new Site(0, nameField.getText(), urlField.getText());
                saved = dao.insert(newSite);

            } else {
                site.setName(nameField.getText());
                site.setUrl(urlField.getText());
                dao.update(site);
            }

            onSuccess.run();
            onAdded.accept(saved);
            onClose.run();

        } catch (SQLException e) {

            if (e.getMessage().contains("UNIQUE")) {
                showError("A site with this name already exists.");
            } else {
                showError("Database error.");
            }
        }
    }


    @FXML
    private void onDelete() {
        if (site == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove site");
        alert.setHeaderText("Remove site: " + site.getName());
        alert.setContentText(
                "All problems and notes for this site will be deleted.\n" +
                        "This action cannot be undone."
        );

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return; // user cancelled
        }

        try {
            dao.deleteById(site.getId());
            onSuccess.run();   // refreshSites()
            onSiteDeleted.run();
            onClose.run();     // closeModal
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to remove site.");
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
