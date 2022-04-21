package hu.kristof.nagy.hikebookserver.api;

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
    private GroupsListService groupsListService;

    @Autowired
    private GroupCreateService groupCreateService;

    @Autowired
    private GroupsGeneralConnectService groupsGeneralConnectService;

    @Autowired
    private GroupsMembersListService groupsMembersListService;

    @GetMapping("{userName}/{isConnectedPage}")
    public ResponseResult<List<String>> listGroups(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return groupsListService.listGroups(userName, isConnectedPage);
    }

    @PutMapping("{groupName}/{userName}")
    public ResponseResult<Boolean> createGroup(
            @PathVariable String groupName,
            @PathVariable String userName
    ) {
        return groupCreateService.createGroup(groupName, userName);
    }

    @PutMapping("{groupName}/{userName}/{isConnectedPage}")
    public ResponseResult<Boolean> generalConnect(
            @PathVariable String groupName,
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return groupsGeneralConnectService.generalConnect(groupName, userName, isConnectedPage);
    }

    @GetMapping("{groupName}")
    public ResponseResult<List<String>> listMembers(
            @PathVariable String groupName
    ) {
        return groupsMembersListService.listMembers(groupName);
    }
}
