package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.Transaction;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class GroupHikeCleanService {
    @Autowired
    private Firestore db;

    // Run once a day at 23:45:00
    @Scheduled(cron = "0 45 23 * * *")
    public void cleanGroupHikes() {
        db.runTransaction(transaction -> {
            List<QueryDocumentSnapshot> groupHikesToDelete = getGroupHikes(transaction);
            List<QueryDocumentSnapshot> groupHikeRoutesToDelete = getGroupHikeRoutes(
                    transaction,
                    groupHikesToDelete.stream()
                    .map(queryDocumentSnapshot ->
                            queryDocumentSnapshot.getString(DbPathConstants.GROUP_HIKE_NAME)
                    ).collect(Collectors.toList())
            );

            var groupHikes = db
                    .collection(DbPathConstants.COLLECTION_GROUP_HIKE);
            for (var docSnapshot : groupHikesToDelete) {
                String id = docSnapshot.getId();
                var docRef = groupHikes.document(id);
                transaction.delete(docRef);
            }

            var groupHikeRoutes = db
                    .collection(DbPathConstants.COLLECTION_ROUTE);
            for (var docSnapshot : groupHikeRoutesToDelete) {
                String id = docSnapshot.getId();
                var docRef = groupHikeRoutes.document(id);
                transaction.delete(docRef);
            }
            return null;
        });
    }

    private List<QueryDocumentSnapshot> getGroupHikeRoutes(
            Transaction transaction,
            List<String> groupHikeNames
    ) {
        var query = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .whereIn(DbPathConstants.ROUTE_GROUP_HIKE_NAME, groupHikeNames);
        var queryFuture = transaction.get(query);
        return FutureUtil.handleFutureGet(() -> queryFuture.get().getDocuments());
    }

    private List<QueryDocumentSnapshot> getGroupHikes(Transaction transaction) {
        var currentTime = Calendar.getInstance();
        // We can only make inequality comparisons on one field per query:
        // https://firebase.google.com/docs/firestore/query-data/queries#query_limitations
        var groupHikes = db
                .collection(DbPathConstants.COLLECTION_GROUP_HIKE);
        var select = groupHikes.select(DbPathConstants.GROUP_HIKE_NAME);
        var yearQuery = select
                .whereLessThan(DbPathConstants.GROUP_HIKE_YEAR, currentTime.get(Calendar.YEAR));
        var yearQueryFuture = transaction.get(yearQuery);
        var years = FutureUtil.handleFutureGet(() -> yearQueryFuture.get().getDocuments());
        // if there are no group hikes which happened in the past year,
        // then we filter on months, because there might be group hikes
        // which dates are in the last month
        if (years.isEmpty()) {
            var monthQuery = select
                    .whereLessThan(DbPathConstants.GROUP_HIKE_MONTH, currentTime.get(Calendar.MONTH));
            var monthQueryFuture = transaction.get(monthQuery);
            var months = FutureUtil.handleFutureGet(() -> monthQueryFuture.get().getDocuments());
            if (months.isEmpty()) {
                var dayOfMonthQuery = select
                        .whereLessThan(DbPathConstants.GROUP_HIKE_DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH));
                var dayOfMonthQueryFuture = transaction.get(dayOfMonthQuery);
                return FutureUtil.handleFutureGet(() -> dayOfMonthQueryFuture.get().getDocuments());
            } else {
                return months;
            }
        } else {
            // if there are group hikes which happened in the past year,
            // then we give back these for deletion,
            // and we don't look further, because cleaning of group hikes is done daily,
            // and because if year has changed, then month and day has changed too,
            // and thus there would be no point in querying these fields
            return years;
        }
    }
}
