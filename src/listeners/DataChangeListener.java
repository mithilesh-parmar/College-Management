package listeners;

import com.google.cloud.firestore.QuerySnapshot;
import javafx.collections.ObservableList;

public interface DataChangeListener {



    void onDataLoaded(ObservableList data);

    void onDataChange(QuerySnapshot data);

    void onError(Exception e);

}
