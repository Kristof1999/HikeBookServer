package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.service.groups.GroupsListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GroupsController {

    @Autowired
    private GroupsListService service;

    @GetMapping("groups/{userName}/{isConnectedPage}")
    public List<String> listGroups(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return service.listGroups(userName, isConnectedPage);
    }
}
