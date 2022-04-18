package hu.kristof.nagy.hikebookserver.service.route;

import com.google.cloud.firestore.*;
import hu.kristof.nagy.hikebookserver.data.DbPathConstants;
import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.model.RouteType;
import hu.kristof.nagy.hikebookserver.service.FutureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RouteLoadService {

    @Autowired
    private Firestore db;

    public List<Route> loadRoutes(String ownerName, String ownerPath) {
        var queryFuture =  db
                .collection(DbPathConstants.COLLECTION_ROUTE)
                .select(Route.getSelectPaths())
                .whereEqualTo(ownerPath, ownerName)
                .get();
        return FutureUtil.handleFutureGet(() ->
                queryFuture.get().getDocuments()
                        .stream()
                        .map(Route::from)
                        .collect(Collectors.toList())
        );
    }
}