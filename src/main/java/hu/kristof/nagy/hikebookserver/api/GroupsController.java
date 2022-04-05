package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.service.groups.GroupCreateService;
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

    @GetMapping("groups/{userName}/{isConnectedPage}")
    public List<String> listGroups(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return groupsListService.listGroups(userName, isConnectedPage);
    }

    @PutMapping("groups/{groupName}")
    public boolean createGroup(
            @PathVariable String groupName,
            @RequestBody String userName
    ) {
        return groupCreateService.createGroup(groupName, userName);
    }
}
