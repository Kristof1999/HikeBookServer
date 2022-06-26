package hu.kristof.nagy.hikebookserver.api;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.GroupHikeListHelper;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeCreateService;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeGeneralConnectService;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeListService;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeParticipantsListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groupHike/")
public class GroupHikeController {

    @Autowired
    private Firestore db;

    @GetMapping("{userName}/{isConnectedPage}")
    public ResponseResult<List<GroupHikeListHelper>> listGroupHikes(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return GroupHikeListService.listGroupHikes(db, userName, isConnectedPage);
    }

    @PutMapping("{userName}/{groupHikeName}")
    public ResponseResult<Boolean> createGroupHike(
            @PathVariable String userName,
            @PathVariable String groupHikeName,
            @RequestBody GroupHikeCreateHelper helper
    ) {
        return GroupHikeCreateService.createGroupHike(db, userName, groupHikeName, helper);
    }

    @GetMapping("{groupHikeName}")
    public ResponseResult<List<String>> listParticipants(
            @PathVariable String groupHikeName
    ) {
        return GroupHikeParticipantsListService.listParticipants(db, groupHikeName);
    }

    @PutMapping("{groupHikeName}/{userName}/{isConnectedPage}")
    public ResponseResult<Boolean> generalGroupHikeConnect(
            @PathVariable String groupHikeName,
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage,
            @RequestBody DateTime dateTime
    ) {
        return GroupHikeGeneralConnectService.generalConnect(db, groupHikeName, userName, isConnectedPage, dateTime);
    }
}
