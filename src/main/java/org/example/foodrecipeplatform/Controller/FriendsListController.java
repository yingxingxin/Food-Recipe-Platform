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

    @FXML
    private Button acceptButton; // For accepting friend requests
    @FXML
    private Button declineButton; // For declining friend requests
    @FXML
    private ListView<String> pendingRequestsListView; // To show pending friend requests

    private ObservableList<String> friendsList;
    private ObservableList<String> pendingRequestsList;

    @FXML
    public void initialize() {
        friendsList = FXCollections.observableArrayList();
        friendsListView.setItems(friendsList);
        loadFriends();

        pendingRequestsList = FXCollections.observableArrayList();
        pendingRequestsListView.setItems(pendingRequestsList);
        loadPendingRequests();
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

    private void loadPendingRequests() {
        pendingRequestsList.clear();

        String currentUserId = SessionManager.getUserId();
        if (currentUserId == null) {
            messageLabel.setText("Error: No user logged in.");
            return;
        }

        CollectionReference requestsRef = FoodRecipePlatform.fstore.collection("FriendRequests");

        ApiFuture<QuerySnapshot> future = requestsRef
                .whereEqualTo("receiverId", currentUserId)
                .whereEqualTo("status", "pending")
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                String senderId = doc.getString("senderId");
                if (senderId != null) {
                    // Fetch the sender's display name and display it as a pending request
                    DocumentReference senderRef = FoodRecipePlatform.fstore.collection("Users").document(senderId);
                    ApiFuture<DocumentSnapshot> senderDoc = senderRef.get();

                    String senderName = senderDoc.get().getString("DisplayName");
                    if (senderName != null) {
                        pendingRequestsList.add(senderName);
                    }
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

                // Create a friend request document
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("senderId", currentUserId);
                requestData.put("receiverId", friendUserId);
                requestData.put("status", "pending");

                // Store the friend request in the FriendRequests collection
                String requestId = UUID.randomUUID().toString();
                FoodRecipePlatform.fstore.collection("FriendRequests")
                        .document(requestId)
                        .set(requestData);

                messageLabel.setText("Friend request sent!");
                addFriendTextField.clear();
            } else {
                messageLabel.setText("User not found.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            messageLabel.setText("Error sending friend request.");
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

    @FXML
    private void acceptFriendRequest() {
        String selectedRequest = pendingRequestsListView.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            messageLabel.setText("Select a friend request to accept.");
            return;
        }

        String currentUserId = SessionManager.getUserId();
        if (currentUserId == null) {
            messageLabel.setText("Error: No user logged in.");
            return;
        }

        // Find the friend request from the current user’s perspective
        CollectionReference requestsRef = FoodRecipePlatform.fstore.collection("FriendRequests");
        ApiFuture<QuerySnapshot> future = requestsRef
                .whereEqualTo("receiverId", currentUserId)
                .whereEqualTo("status", "pending")
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                String senderId = doc.getString("senderId");
                if (senderId != null && selectedRequest.equals(doc.getString("senderId"))) {
                    // Update the request to "accepted"
                    doc.getReference().update("status", "accepted");

                    // Add to both users' Friends collection
                    Map<String, Object> friendData = new HashMap<>();
                    friendData.put("DisplayName", selectedRequest); // Add their name as friend
                    friendData.put("UserID", senderId); // Store their userID as a friend

                    // Add friend to current user's Friends collection
                    FoodRecipePlatform.fstore.collection("Users")
                            .document(currentUserId)
                            .collection("Friends")
                            .document(UUID.randomUUID().toString())
                            .set(friendData);

                    // Add current user to friend's Friends collection
                    friendData.put("DisplayName", SessionManager.getUserDisplayName());
                    friendData.put("UserID", currentUserId);
                    FoodRecipePlatform.fstore.collection("Users")
                            .document(senderId)
                            .collection("Friends")
                            .document(UUID.randomUUID().toString())
                            .set(friendData);

                    messageLabel.setText("Friend request accepted!");
                    loadFriends(); // Refresh friends list
                    loadPendingRequests(); // Refresh pending requests list
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            messageLabel.setText("Error accepting friend request.");
        }
    }

    @FXML
    private void declineFriendRequest() {
        String selectedRequest = pendingRequestsListView.getSelectionModel().getSelectedItem();
        if (selectedRequest == null) {
            messageLabel.setText("Select a friend request to decline.");
            return;
        }

        String currentUserId = SessionManager.getUserId();
        if (currentUserId == null) {
            messageLabel.setText("Error: No user logged in.");
            return;
        }

        CollectionReference requestsRef = FoodRecipePlatform.fstore.collection("FriendRequests");
        ApiFuture<QuerySnapshot> future = requestsRef
                .whereEqualTo("receiverId", currentUserId)
                .whereEqualTo("status", "pending")
                .get();

        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : documents) {
                String senderId = doc.getString("senderId");
                if (senderId != null && selectedRequest.equals(doc.getString("senderId"))) {
                    // Remove the request document from Firestore
                    doc.getReference().delete();

                    messageLabel.setText("Friend request declined!");
                    loadPendingRequests(); // Refresh the pending requests list
                    break;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            messageLabel.setText("Error declining friend request.");
        }
    }
}
