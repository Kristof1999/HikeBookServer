package hu.kristof.nagy.hikebookserver.api;

import com.google.cloud.firestore.Firestore;
import hu.kristof.nagy.hikebookserver.model.ResponseResult;
import hu.kristof.nagy.hikebookserver.service.groups.GroupCreateService;
import hu.kristof.nagy.hikebookserver.service.groups.GroupsGeneralConnectService;
import hu.kristof.nagy.hikebookserver.service.groups.GroupsListService;
import hu.kristof.nagy.hikebookserver.service.groups.GroupsMembersListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("groups/")
public class GroupsController {

    @Autowired
    private Firestore db;

    @GetMapping("{userName}/{isConnectedPage}")
    public ResponseResult<List<String>> listGroups(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return GroupsListService.listGroups(db, userName, isConnectedPage);
    }

    @PutMapping("{groupName}/{userName}")
    public ResponseResult<Boolean> createGroup(
            @PathVariable String groupName,
            @PathVariable String userName
    ) {
        return GroupCreateService.createGroup(db, groupName, userName);
    }

    @PutMapping("{groupName}/{userName}/{isConnectedPage}")
    public ResponseResult<Boolean> generalConnect(
            @PathVariable String groupName,
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return GroupsGeneralConnectService.generalConnect(db, groupName, userName, isConnectedPage);
    }

    @GetMapping("{groupName}")
    public ResponseResult<List<String>> listMembers(
            @PathVariable String groupName
    ) {
        return GroupsMembersListService.listMembers(db, groupName);
    }
}
