package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class FriendsListController {

    @FXML
    private ListView<String> friendsListView;

    @FXML
    private TextField addFriendTextField;

    @FXML
    private Button addFriendButton;

    @FXML
    private Button removeFriendButton;

    @FXML
    private Label messageLabel;

    private ObservableList<String> friendsList;

    @FXML
    public void initialize() {
        friendsList = FXCollections.observableArrayList();
        friendsListView.setItems(friendsList);
        loadFriends();
    }

    private void loadFriends() {
        friendsList.clear();

        String currentUserId = SessionManager.getUserId();
        if (currentUserId == null) {
            messageLabel.setText("Error: No user logged in.");
            return;
        }

        CollectionReference friendsRef = FoodRecipePlatform.fstore.collection("Users")
                .document(currentUserId)
                .collection("Friends");

        ApiFuture<QuerySnapshot> future = friendsRef.get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                String friendName = doc.getString("DisplayName");
                if (friendName != null) {
                    friendsList.add(friendName);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addFriendButtonClicked() {
        String friendDisplayName = addFriendTextField.getText().trim();
        if (friendDisplayName.isEmpty()) {
            messageLabel.setText("Enter a display name.");
            return;
        }

        String currentUserId = SessionManager.getUserId();
        if (currentUserId == null) {
            messageLabel.setText("Error: No user logged in.");
            return;
        }

        CollectionReference usersRef = FoodRecipePlatform.fstore.collection("Users");
        Query query = usersRef.whereEqualTo("DisplayName", friendDisplayName);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            if (!documents.isEmpty()) {
                DocumentSnapshot friendDoc = documents.get(0);
                String friendUserId = friendDoc.getId();

                Map<String, Object> friendData = new HashMap<>();
                friendData.put("DisplayName", friendDisplayName);
                friendData.put("UserID", friendUserId);

                CollectionReference friendsRef = usersRef.document(currentUserId).collection("Friends");
                friendsRef.document(UUID.randomUUID().toString()).set(friendData);

                messageLabel.setText("Friend added!");
                friendsList.add(friendDisplayName);
                addFriendTextField.clear();
            } else {
                messageLabel.setText("User not found.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            messageLabel.setText("Error adding friend.");
        }
    }

    @FXML
    private void removeFriendButtonClicked() {
        String selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            messageLabel.setText("Select a friend to remove.");
            return;
        }

        String currentUserId = SessionManager.getUserId();
        if (currentUserId == null) {
            messageLabel.setText("Error: No user logged in.");
            return;
        }

        CollectionReference friendsRef = FoodRecipePlatform.fstore.collection("Users")
                .document(currentUserId)
                .collection("Friends");

        // Search for the friend's document by DisplayName
        Query query = friendsRef.whereEqualTo("DisplayName", selectedFriend);
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        try {
            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
            if (!documents.isEmpty()) {
                for (QueryDocumentSnapshot doc : documents) {
                    doc.getReference().delete();
                }
                friendsList.remove(selectedFriend);
                messageLabel.setText("Friend removed!");
            } else {
                messageLabel.setText("Friend not found.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            messageLabel.setText("Error removing friend.");
        }
    }
}
