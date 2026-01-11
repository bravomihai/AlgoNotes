package ui;

import dao.SiteDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Site;

import java.sql.SQLException;

public class AddEditSiteController {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField urlField;
    @FXML private Button deleteButton;
    private Runnable onRemoved;

    private Site site;
    private SiteDAO dao;
    private Runnable onSuccess;
    private Runnable onClose;

    public void init(Site site, SiteDAO dao, Runnable onRemoved, Runnable onSuccess, Runnable onClose) {
        this.site = site;
        this.dao = dao;
        this.onSuccess = onSuccess;
        this.onClose = onClose;
        this.onRemoved = onRemoved;

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

        try {
            if (site == null) {
                dao.insert(new Site(0, nameField.getText(), urlField.getText()));
            } else {
                site.setName(nameField.getText());
                site.setUrl(urlField.getText());
                dao.update(site);
            }

            onSuccess.run();
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
        try {
            dao.deleteById(site.getId());
            onSuccess.run();
            onRemoved.run();
            onClose.run();
        } catch (Exception e) {
            e.printStackTrace();
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
