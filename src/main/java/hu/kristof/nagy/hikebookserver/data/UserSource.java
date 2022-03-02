
// based on:
// https://firebase.google.com/docs/firestore/quickstart

package hu.kristof.nagy.hikebookserver.data;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import hu.kristof.nagy.hikebookserver.model.UserAuth;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserSource {
    public CollectionReference users;

    // TODO: make this singleton -> DI
    public void init() {
        try {
            InputStream serviceAccount = new FileInputStream("C:\\Users\\36203\\hikebook-595dc-firebase-adminsdk-9nnbq-8738991677.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

           Firestore db = FirestoreClient.getFirestore();
           users  = db.collection("users");
           // TODO: do some logging
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<UserAuth>  getUsers() {
        List<UserAuth> userAuthList = new ArrayList<>();
        for (DocumentReference docRef : users.listDocuments()) {
            try {
                DocumentSnapshot documentSnapshot = docRef.get().get();
                UserAuth user = new UserAuth(
                        (String) documentSnapshot.get("name"),
                        (String) documentSnapshot.get("pswd"));
                userAuthList.add(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return userAuthList;
    }

    public void putUser(UserAuth user) {
        DocumentReference document = users.document(user.getName());
        Map<String, Object> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("pswd", user.getPassword());
        data.put("avgSpeed", 0L);
        document.set(data);
    }
}
