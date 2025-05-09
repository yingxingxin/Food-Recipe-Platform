package org.example.foodrecipeplatform.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.example.foodrecipeplatform.FoodRecipePlatform;

import java.util.*;
import java.util.concurrent.ExecutionException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class FriendsListScreenController
{
    @FXML
    public Button addButton;
    @FXML
    public TextField friendTextField;
    @FXML
    public ListView<String[]> pendingListView = new ListView<>();
    @FXML
    public ListView<String[]> friendsListView = new ListView<>();;

    // this userId: "d1ae3ac7-14a1-4e34-a4a5-53d763685231" -- test1
    // other userId: "ce5b2344-5835-4c3f-b692-ccf13c43800b" -- test99
    // 3rd user: "77e68b36-2234-4168-9f05-6357ce0ae2d8" -- aqw
    String userId = "d1ae3ac7-14a1-4e34-a4a5-53d763685231"; // SessionManager.getUserId();

    String username;

    @FXML
    void initialize() throws ExecutionException, InterruptedException
    {
        //DocumentReference docRef = FoodRecipePlatform.fstore.collection("Users").document(user); if you don't add get, you can write to it
        ApiFuture<DocumentSnapshot> future = FoodRecipePlatform.fstore.collection("Users").document(userId).get();
        DocumentSnapshot doc = future.get();

        //System.out.println("This user docId: " + doc.getId());
        //System.out.println("This user user: " + doc.getString("UserName"));
        username = doc.getString("UserName");

        // load pending list of current user
        //List<String[]> data = returnPendingList();

//        List<String[]> data = List.of(
//                new String[]{"Joshua Horna", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg", "incoming"},
//                new String[]{"Nalor Yonoas", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg", "outgoing"},
//                new String[]{"Rex Billy Ceria Aguino", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg", "outgoing"},
//                new String[]{"Judy Ann Tahum Estares", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg", "incoming"},
//                new String[]{"Eugene Cobero", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg", "outgoing"}
//        );

        displayPending();

        displayFriends();
    }

    /*
        sends the person with the username a request in their pending list & adds a request send with their
        username on your pending list
     */
    @FXML
    void addButtonClicked(ActionEvent event) throws ExecutionException, InterruptedException
    {
        System.out.println("Add button clicked");
        String friend = friendTextField.getText();

        // adds friend if username exist, it isn't themselves, & if it's not already in their friends list
        boolean doesUserExist, isUserThemselves, isUserAlreadyFriends;

        doesUserExist = checkIfUsernameExists(friend);
        isUserThemselves = friend.equals(username);
        isUserAlreadyFriends = checkIfUserInFriends(friend);
        System.out.println(doesUserExist + " " + isUserThemselves + " " + isUserAlreadyFriends);

        //if (checkIfUsernameExists(friend) && !friend.equals(username) && !checkIfUserInFriends(friend))
        if (doesUserExist && !isUserThemselves && !isUserAlreadyFriends)
        {
            sendingFriendRequest(friend);
            System.out.println("Username exists, adding: " + friend);
            displayPending();
        }
        else
        { // print out messages with matching statements
            if (!doesUserExist)
                System.out.println("Username does not exist");
            else if (isUserThemselves)
                System.out.println("You can't add yourself");
            else if (isUserAlreadyFriends)
                System.out.println("User already in friends list");
        }

        //sendingFriendRequest("test99@gmail.com");
    }

    // add refresh button that updates the list, maybe call the display pending and friends list method
    void refreshButtonClicked(ActionEvent event)
    {

    }

    // checks if username is in firestore
    private boolean checkIfUsernameExists(String username) throws ExecutionException, InterruptedException
    {
        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", username).get();
        QuerySnapshot querySnapshot = future.get();
        List<QueryDocumentSnapshot> results = querySnapshot.getDocuments();

        System.out.println("Size of querySnapshot2, 0 means this user doesn't exist: " + results.size());
        return !results.isEmpty();
    }

    // check if the user you're adding is already in the friends list
    private boolean checkIfUserInFriends(String username) throws ExecutionException, InterruptedException
    {
        // need to go into current user's friends list and check
        DocumentReference docRef = FoodRecipePlatform.fstore.collection("Users").document(userId);
        ApiFuture<DocumentSnapshot> futureCheck = docRef.get();
        DocumentSnapshot document = futureCheck.get();

        List<String> friendsList = (List<String>)document.get("Friends");

        if (friendsList != null)
        {
//            System.out.println("Friends List size: " + friendsList.size());
//            System.out.println("Friend in list: " + friendsList.contains(username));
            return friendsList.contains(username); // returns true if user is already in friends list
        }

        return false; // returns false if user isn't in friends list
    }

    // sends to pending list of this and other user's. if sender, you see request sent, if receiver, you get accept/delete button
    private void sendingFriendRequest(String otherPerson) throws ExecutionException, InterruptedException
    {
        ApiFuture<DocumentSnapshot> myFuture = FoodRecipePlatform.fstore.collection("Users").document(userId).get();
        DocumentSnapshot myDoc = myFuture.get(); //used to get current username, maybe put this in SessionManager class too

        // adding to my pending list
        DocumentReference myDocRef = FoodRecipePlatform.fstore.collection("Users").document(userId);

        Map<String, Object> myPendingList = new HashMap<>();
        myPendingList.put("UserName", otherPerson);  // sender gets other user's name
        myPendingList.put("IsSender", true);

        myDocRef.update("Pending", FieldValue.arrayUnion(myPendingList));


        // adding to the other person's pending list
        ApiFuture<QuerySnapshot> otherFuture = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", otherPerson).get();
        QuerySnapshot querySnapshot = otherFuture.get();
        List<QueryDocumentSnapshot> otherResults = querySnapshot.getDocuments();

        // use this to delete & update to the other user
        DocumentReference otherDocRef = otherResults.get(0).getReference();

        Map<String, Object> otherPendingList = new HashMap<>();

        otherPendingList.put("UserName", myDoc.get("UserName")); // receiver gets sender's name
        otherPendingList.put("IsSender", false);

        otherDocRef.update("Pending", FieldValue.arrayUnion(otherPendingList));
    }

    private List<String[]> returnPendingList() throws ExecutionException, InterruptedException {
        // get user name, profile picture, & get if they're sender or not
        DocumentReference docRef = FoodRecipePlatform.fstore.collection("Users").document(userId);
        ApiFuture<DocumentSnapshot> futureCheck = docRef.get();
        DocumentSnapshot document = futureCheck.get();

        List<Map<String, Object>> pending = (List<Map<String, Object>>) document.get("Pending");
        List<String[]> results = new ArrayList<>();

        if (pending != null)
        {
            for (Map<String, Object> pend : pending)
            {
                String username = (String) pend.get("UserName");

                String[] userInfo = getUserInfo(username);
                String pfpUrl;

                if (userInfo == null)
                    pfpUrl = "https://img.freepik.com/premium-vector/social-media-logo_1305298-29989.jpg?semt=ais_hybrid&w=740";
                else
                    pfpUrl = userInfo[1]; // index: 0 = username, 1 = pfp

                Boolean isSender = (Boolean) pend.get("IsSender");

                String isSenderString;
                if (isSender)
                    isSenderString = "outgoing"; // is sender
                else
                    isSenderString = "incoming"; // is receiver

                //System.out.println("UserName: " + username + ", IsSender: " + isSender);

                results.add(new String[]{username, pfpUrl, isSenderString});
            }
        }
        else
        {
            System.out.println("Messages field is empty or doesn't exist.");
        }

        return results;
    }

    // returns username & profile picture >> probably make it only return pfp
    private String[] getUserInfo(String username) throws ExecutionException, InterruptedException
    {
        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", username).get();
        QuerySnapshot querySnapshot = future.get();
        List<QueryDocumentSnapshot> results = querySnapshot.getDocuments();

        // use this to read
        DocumentReference docRef; // = results.get(0).getReference();
        ApiFuture<DocumentSnapshot> futureCheck; // = docRef.get();
        DocumentSnapshot document; // = futureCheck.get();

        if (!results.isEmpty())
        {
            docRef = results.get(0).getReference();
            futureCheck = docRef.get();
            document = futureCheck.get();
            return new String[]{(String) document.get("UserName"), (String) document.get("ProfilePicture")};
        }

        return null;
    }

    // used to remove from the pending list from the sender & receiver
    // If I'm able to click accept/decline, that means I'm the receiver, which mean I'm always false in sender
    private void removeFromPendingList(String otherUsername) throws ExecutionException, InterruptedException
    {
        System.out.println("\nRemoving from pending list: " + otherUsername);

        // use this to delete from this user's pending list
        DocumentReference myDocRef = FoodRecipePlatform.fstore.collection("Users").document(userId);

        Map<String, Object> toRemove = new HashMap<>();
        toRemove.put("UserName", otherUsername);
        toRemove.put("IsSender", false);  // Must match exactly

        myDocRef.update("Pending", FieldValue.arrayRemove(toRemove));
        System.out.println("Removing user from this pending list... UserName: " + otherUsername);

        // removing from other pending list
        ApiFuture<QuerySnapshot> otherFuture = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", otherUsername).get();
        QuerySnapshot querySnapshot = otherFuture.get();
        List<QueryDocumentSnapshot> otherResults = querySnapshot.getDocuments();

        // use this to delete
        DocumentReference otherDocRef = otherResults.get(0).getReference();

        toRemove = new HashMap<>(); // re-using hashmap
        toRemove.put("UserName", this.username);
        toRemove.put("IsSender", true);  // Must match exactly

        otherDocRef.update("Pending", FieldValue.arrayRemove(toRemove));
        System.out.println("Removing user from other pending list... UserName: " + this.username);
    }


    // adding sender and receiver to each other's friends list
    private void addToFriends(String otherUsername) throws ExecutionException, InterruptedException
    {
        // adding this user to the other user's friends list
        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", otherUsername).get();
        QuerySnapshot querySnapshot = future.get();
        List<QueryDocumentSnapshot> results = querySnapshot.getDocuments();

        // use this to update
        DocumentReference docRef = results.get(0).getReference();
        docRef.update("Friends", FieldValue.arrayUnion(this.username));

        // adding the other user to this user's friends list
        DocumentReference myDocRef = FoodRecipePlatform.fstore.collection("Users").document(userId);

        myDocRef.update("Friends", FieldValue.arrayUnion(otherUsername));
    }

    // gets the user's friends list
    private List<String[]> getFirendsList() throws ExecutionException, InterruptedException
    {
        DocumentReference docRef = FoodRecipePlatform.fstore.collection("Users").document(userId);
        ApiFuture<DocumentSnapshot> snapShot =  docRef.get();
        DocumentSnapshot documentSnapshot = snapShot.get(); // used to get the Friends field from the user

        List<String[]> results = new ArrayList<>();

        List<String> friends = (List<String>) documentSnapshot.get("Friends");

        if (friends != null)
        {
            for (String friend : friends)
            {
                System.out.println("Get friend: " + friend);

                String[] friendInfo = new String[2];
                friendInfo[0] = friend;

                String[] friendPfp = getUserInfo(friend);
                if (friendPfp != null)
                {
                    System.out.println(friendPfp[1]);
                    friendInfo[1] = friendPfp[1];
                }
                else
                {
                    System.out.println(friend + " has no pfp");
                    friendInfo[1] = "https://img.freepik.com/premium-vector/social-media-logo_1305298-29989.jpg?semt=ais_hybrid&w=740";
                }

                results.add(friendInfo);
            }
            return results;
        }
        return new ArrayList<>();
    }

    // removes the friend from user's friends list & the other user's friends list
    private void removeFromFriends(String otherUsername) throws ExecutionException, InterruptedException
    {
        // removing from this user's friends list
        DocumentReference myDocRef = FoodRecipePlatform.fstore.collection("Users").document(userId);
        myDocRef.update("Friends", FieldValue.arrayRemove(otherUsername));
        System.out.println("Removing user from this friends list... UserName: " + otherUsername);
        
        // removing from other user's friends list
        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", otherUsername).get();
        QuerySnapshot querySnapshot = future.get();
        List<QueryDocumentSnapshot> results = querySnapshot.getDocuments();

        // use this to delete
        if (!results.isEmpty())
        {
            DocumentReference otherDocRef = results.get(0).getReference();
            otherDocRef.update("Friends", FieldValue.arrayRemove(username));
            System.out.println("Removing user from other friends list... UserName: " + username);
        }
        else
            System.out.println("Removing failed, " + otherUsername + " doesn't exist");

    }

    // used to test
    @FXML
    void addButtonClickedTest(ActionEvent event) throws ExecutionException, InterruptedException {
        System.out.println("Add button clicked");

        // getting user with doc id
//        ApiFuture<DocumentSnapshot> future = FoodRecipePlatform.fstore.collection("Users").document(user).get();
//        DocumentSnapshot doc = future.get();
//
//        System.out.println(doc.getId());
//        System.out.println(doc.getString("DisplayName"));
//        System.out.println(doc.getString("UserName"));
//        System.out.println(doc.getString("Password"));

        // getting user with doc id
//        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", "test1@gmail.com").get();
//        QuerySnapshot querySnapshot = future.get();
//        List<QueryDocumentSnapshot> results = querySnapshot.getDocuments();
//        System.out.println(results.size());

        // ADDING GET TO THE END OF A FoodRecipePlatform.fstore.collection MEANS READING & NOT INCLUDING GET MEANS WRITING

        // writing to a specific user using their username & removing, (use this to read, delete, update)
        ApiFuture<QuerySnapshot> future = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", "test1@gmail.com").get();
        QuerySnapshot querySnapshot = future.get();
        List<QueryDocumentSnapshot> results = querySnapshot.getDocuments();

        // use this to read, delete, update
        DocumentReference docRef = results.get(0).getReference();


        // READING______________


        // check if the user you're adding exists in the database, adding test10@gmail.com, then check users that don't exist
//        ApiFuture<QuerySnapshot> checkIfUserExist = FoodRecipePlatform.fstore.collection("Users").whereEqualTo("UserName", "test1-@gmail.com").get();
//        QuerySnapshot querySnapshot2 = checkIfUserExist.get();
//        List<QueryDocumentSnapshot> results2 = querySnapshot2.getDocuments();
//
//        System.out.println("Size of querySnapshot2: " + querySnapshot2.size()); // just use QuerySnapshot, this is enough
//        System.out.println("Size of results2: " + results2.size());


        // checks if the friend is already in the list, do this before removing, wait don't need bc we remove from Friends list
//        ApiFuture<DocumentSnapshot> futureCheck = docRef.get();
//        DocumentSnapshot document = futureCheck.get();
//
//        List<String> friends = (List<String>) document.get("Friends");
//
//        if (friends != null && friends.contains("test100@gmail.com")) {
//            System.out.println("John Doe is already in the friends list.");
//        } else {
//            System.out.println("John Doe is NOT in the friends list.");
//        }


        //  reading from pending list
//        ApiFuture<DocumentSnapshot> futureCheck = docRef.get();
//        DocumentSnapshot document = futureCheck.get();

//        List<Map<String, Object>> pending = (List<Map<String, Object>>) document.get("Pending");
//
//        if (pending != null)
//        {
//            for (Map<String, Object> pend : pending)
//            {
//                String username = (String) pend.get("UserName");
//                Boolean isSender = (Boolean) pend.get("IsSender");
//
//                System.out.println("UserName: " + username + ", IsSender: " + isSender);
//            }
//        }
//        else
//        {
//            System.out.println("Messages field is empty or doesn't exist.");
//        }



        // ADDING______________

        // adds new username to Friends & creates a list if it doesn't exist
        //docRef.update("Friends", FieldValue.arrayUnion("test200@gmail.com"));


        // adding to pending list
//        Map<String, Object> pendingList = new HashMap<>();
//        pendingList.put("UserName", "lospolloshermanus@example.com");
//        pendingList.put("IsSender", false);
//
//        docRef.update("Pending", FieldValue.arrayUnion(pendingList));



        // REMOVING__________________

        // remove from Friends array field
        //ApiFuture<WriteResult> arrayUnion = docRef.update("Friends", FieldValue.arrayRemove("test200@gmail.com"));

//        try {
//            WriteResult result = arrayUnion.get();  // Blocks until done
//            System.out.println("Friend removed at time: " + result.getUpdateTime());
//        } catch (Exception e) {
//            System.err.println("Failed to remove friend: " + e.getMessage());
//        }


        // removing specific user from pending list
//        Map<String, Object> toRemove = new HashMap<>();
//        toRemove.put("UserName", "friend@example.com");
//        toRemove.put("IsSender", true);  // Must match exactly
//
//        docRef.update("Pending", FieldValue.arrayRemove(toRemove));

    }

    // displays the pending list of this user
    void displayPending() throws ExecutionException, InterruptedException
    {
        // load pending list of current user
        List<String[]> data = returnPendingList();

        ObservableList<String[]> requestData = FXCollections.observableArrayList(data);
        pendingListView.setItems(requestData);

        pendingListView.setCellFactory(list -> new ListCell<>()
        {
            @Override
            protected void updateItem(String[] person, boolean empty)
            {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setGraphic(null);
                    return;
                }

                String name = person[0];
                String imagePath = person[1];
                String type = person.length >= 3 ? person[2] : "incoming";

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

                HBox row;
                if (type.equals("incoming")) {
                    Button confirm = new Button("Confirm");
                    Button delete = new Button("Delete");

                    confirm.setStyle("-fx-background-color: #1877F2; -fx-text-fill: white;");
                    delete.setStyle("-fx-background-color: #E4E6EB;");

                    confirm.setOnAction(e ->
                    {
                        System.out.println("Confirmed: " + name);
                        getListView().getItems().remove(person);

                        // add user to each other's friend's list & remove from both user's pending list
                        try {
                            addToFriends(name);
                            removeFromPendingList(name);
                        } catch (ExecutionException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                        // updates friends list if you click accpet
                        try {
                            displayFriends();
                        } catch (ExecutionException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

                    delete.setOnAction(e ->
                    {
                        // remove from both user's pending list
                        try {
                            removeFromPendingList(name);
                        } catch (ExecutionException | InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }

                        System.out.println("Deleted: " + name);
                        getListView().getItems().remove(person);
                    });

                    HBox buttons = new HBox(5, confirm, delete);
                    VBox textButtons = new VBox(5, nameLabel, buttons);
                    row = new HBox(10, imageView, textButtons);
                } else {
                    Label sentLabel = new Label("Request Sent");
                    sentLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                    VBox textInfo = new VBox(5, nameLabel, sentLabel);
                    row = new HBox(10, imageView, textInfo);
                }

                row.setAlignment(Pos.TOP_LEFT);
                row.setStyle("-fx-padding: 10;");
                setGraphic(row);
            }
        });
    }

    void displayFriends() throws ExecutionException, InterruptedException
    {
        // loads friends list
        List<String[]> friendsData = getFirendsList();

//        List<String[]> friendsData = List.of(
//                new String[]{"Carla Abad", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg"},
//                new String[]{"Jonathan Dela Cruz", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg"},
//                new String[]{"Maria Santos", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg"},
//                new String[]{"Jake Domingo", "https://www.borninspace.com/wp-content/uploads/2023/05/shaun-the-sheep-seaslug-cut.jpg"}
//        );

        ObservableList<String[]> friends = FXCollections.observableArrayList(friendsData);
        friendsListView.setItems(friends);

        friendsListView.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String[] person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setGraphic(null);
                    return;
                }

                String name = person[0];
                String imagePath = person[1];

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
                    
                    try {
                        removeFromFriends(name);
                    } catch (ExecutionException | InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    
                    System.out.println("Removed friend: " + name);
                    getListView().getItems().remove(person);
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

}


