package org.example.foodrecipeplatform.Controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FriendsListController {
    @FXML
    public ListView<String[]> friendsListView;
    @FXML
    public ListView<String[]> pendingListView;
    @FXML
    public Button addButton;
    @FXML
    public TextField inputTextField;
    @FXML
    private Label welcomeLabel;

    String userId = SessionManager.getUserId();

    @FXML
    public void initialize() throws ExecutionException, InterruptedException {
        welcomeLabel.setText("Welcome, " + SessionManager.getUserDisplayName());
        addButton.setOnAction(e -> onAddFriendClicked());
        displayPending();
        displayFriends();
    }

    public void onAddFriendClicked() {
        String userNameToSearch = inputTextField.getText().trim();
        if (userNameToSearch.isEmpty()) return;

        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("Users")
                .whereEqualTo("UserName", userNameToSearch)
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            if (documents.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "User Not Found", "No user found with that username.");
                return;
            }
            DocumentSnapshot targetDoc = documents.get(0);
            String targetUserId = targetDoc.getId();

            if (targetUserId.equals(userId)) {
                showAlert(Alert.AlertType.WARNING, "Invalid Action", "You cannot send a friend request to yourself.");
                return;
            }

            ApiFuture<QuerySnapshot> existingRequest = db.collection("FriendRequests")
                    .whereEqualTo("senderId", userId)
                    .whereEqualTo("receiverId", targetUserId)
                    .get();

            if (!existingRequest.get().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Request Already Sent", "You've already sent a request to this user.");
                return;
            }

            sendFriendRequest(userId, targetUserId);
            showAlert(Alert.AlertType.INFORMATION, "Request Sent", "Friend request sent to " + userNameToSearch + ".");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong while sending the request.");
        }
    }


    public void sendFriendRequest(String senderId, String receiverId) {
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("senderId", senderId);
        requestData.put("receiverId", receiverId);
        requestData.put("timestamp", FieldValue.serverTimestamp());

        ApiFuture<DocumentReference> future = db.collection("FriendRequests").add(requestData);

        try {
            DocumentReference docRef = future.get();
            System.out.println("Request sent: " + docRef.getId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void acceptRequest(String currentUserId, String senderId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        db.collection("users").document(currentUserId)
                .collection("Friends").document(senderId)
                .set(Map.of("userId", senderId));

        db.collection("users").document(senderId)
                .collection("Friends").document(currentUserId)
                .set(Map.of("userId", currentUserId));

        QuerySnapshot snapshot = db.collection("FriendRequests")
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", currentUserId)
                .get().get();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            db.collection("FriendRequests").document(doc.getId()).delete();
        }
    }

    public void declineRequest(String currentUserId, String senderId) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        QuerySnapshot snapshot = db.collection("FriendRequests")
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("receiverId", currentUserId)
                .get().get();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            db.collection("FriendRequests").document(doc.getId()).delete();
        }
    }

    public void removeFriend(String userId1, String userId2) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();

        db.collection("users").document(userId1)
                .collection("Friends").document(userId2).delete();

        db.collection("users").document(userId2)
                .collection("Friends").document(userId1).delete();
    }

    public void displayFriends() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("users")
                .document(userId).collection("Friends")
                .get();
        List<QueryDocumentSnapshot> friendDocs = future.get().getDocuments();

        List<String[]> friendsData = new ArrayList<>();
        for (DocumentSnapshot friendDoc : friendDocs) {
            String friendId = friendDoc.getId();
            DocumentSnapshot userSnap = db.collection("Users").document(friendId).get().get();
            String name = userSnap.getString("UserName");
            String imageUrl = userSnap.contains("ProfilePicture") ? userSnap.getString("ProfilePicture") : "https://via.placeholder.com/50";
            friendsData.add(new String[]{name, imageUrl, friendId});
        }

        ObservableList<String[]> friends = FXCollections.observableArrayList(friendsData);
        friendsListView.setItems(friends);

        friendsListView.setCellFactory(list -> new ListCell<>() {
            protected void updateItem(String[] person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setGraphic(null);
                    return;
                }

                String name = person[0];
                String imagePath = person[1];
                String friendId = person[2];

                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(imagePath, 50, 50, true, true));
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + imagePath);
                }
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);

                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Button delete = new Button("Remove");
                delete.setStyle("-fx-background-color: #E4E6EB;");
                delete.setOnAction(e -> {
                    System.out.println("Removed friend: " + name);
                    getListView().getItems().remove(person);
                    try {
                        removeFriend(userId, friendId);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(10, imageView, nameLabel, spacer, delete);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 10;");
                setGraphic(row);
            }
        });
    }

    public void displayPending() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = db.collection("FriendRequests")
                .whereEqualTo("receiverId", userId)
                .get();
        List<QueryDocumentSnapshot> pendingDocs = future.get().getDocuments();

        List<String[]> pendingData = new ArrayList<>();
        for (DocumentSnapshot pendingDoc : pendingDocs) {
            String senderId = pendingDoc.getString("senderId");
            DocumentSnapshot userSnap = db.collection("Users").document(senderId).get().get();
            String name = userSnap.getString("UserName");
            String imageUrl = userSnap.contains("ProfilePicture") ? userSnap.getString("ProfilePicture") : "https://via.placeholder.com/50";
            pendingData.add(new String[]{name, imageUrl, senderId});
        }

        ObservableList<String[]> pendingList = FXCollections.observableArrayList(pendingData);
        pendingListView.setItems(pendingList);

        pendingListView.setCellFactory(list -> new ListCell<>() {
            protected void updateItem(String[] person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setGraphic(null);
                    return;
                }

                String name = person[0];
                String imagePath = person[1];
                String senderId = person[2];

                ImageView imageView = new ImageView();
                try {
                    imageView.setImage(new Image(imagePath, 50, 50, true, true));
                } catch (Exception e) {
                    System.err.println("Failed to load image: " + imagePath);
                }
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);

                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

                Button accept = new Button("Accept");
                accept.setStyle("-fx-background-color: #90EE90;");
                accept.setOnAction(e -> {
                    try {
                        acceptRequest(userId, senderId);
                        getListView().getItems().remove(person);
                        displayFriends();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                Button decline = new Button("Decline");
                decline.setStyle("-fx-background-color: #FFCCCB;");
                decline.setOnAction(e -> {
                    try {
                        declineRequest(userId, senderId);
                        getListView().getItems().remove(person);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                HBox row = new HBox(10, imageView, nameLabel, spacer, accept, decline);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-padding: 10;");
                setGraphic(row);
            }
        });
    }
    @FXML
    void goBackToProfile(ActionEvent Event) throws IOException {
        FoodRecipePlatform.setRoot("ProfilePage");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
