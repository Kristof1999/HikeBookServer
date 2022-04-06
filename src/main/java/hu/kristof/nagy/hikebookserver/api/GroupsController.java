package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.service.groups.GroupCreateService;
import hu.kristof.nagy.hikebookserver.service.groups.GroupsGeneralConnectService;
import hu.kristof.nagy.hikebookserver.service.groups.GroupsListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupsController {

    @Autowired
    private GroupsListService groupsListService;

    @Autowired
    private GroupCreateService groupCreateService;

    @Autowired
    private GroupsGeneralConnectService groupsGeneralConnectService;

    @GetMapping("groups/{userName}/{isConnectedPage}")
    public List<String> listGroups(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return groupsListService.listGroups(userName, isConnectedPage);
    }

    @PutMapping("groups/{groupName}/{userName}")
    public boolean createGroup(
            @PathVariable String groupName,
            @PathVariable String userName
    ) {
        return groupCreateService.createGroup(groupName, userName);
    }

    @PutMapping("groups/{groupName}/{userName}/{isConnectedPage}")
    public boolean generalConnect(
            @PathVariable String groupName,
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return groupsGeneralConnectService.generalConnect(groupName, userName, isConnectedPage);
    }
}
