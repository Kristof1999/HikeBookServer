package hu.kristof.nagy.hikebookserver.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("groupHike")
public class GroupHikeController {

    @GetMapping("groups/{userName}/{isConnectedPage}")
    public List<String> listGroupHikes(
            @PathVariable String userName,
            @PathVariable boolean isConnectedPage
    ) {
        return groupsListService.listGroups(userName, isConnectedPage);
    }
}
