package hu.kristof.nagy.hikebookserver.service.grouphike;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
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
        // TODO: make it run as a transaction
        // because random server errors can happen, and if
        // the error happens after deleting a groupHike,
        // then the corresponding groupHikeRoute will never get deleted
        // and this will cause all sort of problems
        List<QueryDocumentSnapshot> groupHikesToDelete = getGroupHikes();
        List<QueryDocumentSnapshot> groupHikeRoutesToDelete = getGroupHikeRoutes(groupHikesToDelete
                .stream()
                .map(queryDocumentSnapshot ->
                        queryDocumentSnapshot.getString(DbPathConstants.GROUP_HIKE_NAME)
                ).collect(Collectors.toList())
        );

        var groupHikes = db
                .collection(DbPathConstants.COLLECTION_GROUP_HIKE);
        for (var docSnapshot : groupHikesToDelete) {
            String id = docSnapshot.getId();
            groupHikes.document(id)
                    .delete();
        }

        var groupHikeRoutes = db
                .collection(DbPathConstants.COLLECTION_ROUTE);
        for (var docSnapshot : groupHikeRoutesToDelete) {
            String id = docSnapshot.getId();
            groupHikeRoutes.document(id)
                    .delete();
        }
    }

    private List<QueryDocumentSnapshot> getGroupHikeRoutes(List<String> groupHikeNames) {
        var queryFuture = db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .whereIn(DbPathConstants.ROUTE_GROUP_HIKE_NAME, groupHikeNames)
                .get();
        return FutureUtil.handleFutureGet(() -> queryFuture.get().getDocuments());
    }

    private List<QueryDocumentSnapshot> getGroupHikes() {
        var currentTime = Calendar.getInstance();
        // We can only make inequality comparisons on one field per query:
        // https://firebase.google.com/docs/firestore/query-data/queries#query_limitations
        var groupHikes = db
                .collection(DbPathConstants.COLLECTION_GROUP_HIKE);
        var select = groupHikes.select(DbPathConstants.GROUP_HIKE_NAME);
        var yearQueryFuture= select
                .whereLessThan(DbPathConstants.GROUP_HIKE_YEAR, currentTime.get(Calendar.YEAR))
                .get();
        var years = FutureUtil.handleFutureGet(() -> yearQueryFuture.get().getDocuments());
        // if there are no group hikes which happened in the past year,
        // then we filter on months, because there might be group hikes
        // which dates are in the last month
        if (years.isEmpty()) {
            var monthQueryFuture = select
                    .whereLessThan(DbPathConstants.GROUP_HIKE_MONTH, currentTime.get(Calendar.MONTH))
                    .get();
            var months = FutureUtil.handleFutureGet(() -> monthQueryFuture.get().getDocuments());
            if (months.isEmpty()) {
                var dayOfMonthQueryFuture = select
                        .whereLessThan(DbPathConstants.GROUP_HIKE_DAY_OF_MONTH, currentTime.get(Calendar.DAY_OF_MONTH))
                        .get();
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
