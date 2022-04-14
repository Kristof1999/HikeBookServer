package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.DateTime;
import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.GroupHikeListHelper;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeCreateService;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeGeneralConnectService;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeListService;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeParticipantsListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("groupHike/")
public class GroupHikeController {

    @Autowired
    private GroupHikeListService groupHikeListService;

    @Autowired
    private GroupHikeCreateService groupHikeCreateService;

    @Autowired
    private GroupHikeParticipantsListService groupHikeParticipantsListService;

    @Autowired
    private GroupHikeGeneralConnectService groupHikeGeneralConnectService;

    @GetMapping("{userName}/{isConnectedPage}")
    public List<GroupHikeListHelper> listGroupHikes(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return groupHikeListService.listGroupHikes(userName, isConnectedPage);
    }

    @PutMapping("{userName}/{groupHikeName}")
    public boolean createGroupHike(
            @PathVariable String userName,
            @PathVariable String groupHikeName,
            @RequestBody GroupHikeCreateHelper helper
    ) {
        return groupHikeCreateService.createGroupHike(userName, groupHikeName, helper);
    }

    @GetMapping("{groupHikeName}")
    public List<String> listParticipants(
            @PathVariable String groupHikeName
    ) {
        return groupHikeParticipantsListService.listParticipants(groupHikeName);
    }

    @PutMapping("{groupHikeName}/{userName}/{isConnectedPage}")
    public boolean generalGroupHikeConnect(
            @PathVariable String groupHikeName,
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage,
            @RequestBody DateTime dateTime
    ) {
        return groupHikeGeneralConnectService.generalConnect(groupHikeName, userName, isConnectedPage, dateTime);
    }
}
