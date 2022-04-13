package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.model.GroupHikeCreateHelper;
import hu.kristof.nagy.hikebookserver.model.routes.Route;
import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping("groupHike/")
public class GroupHikeController {

    @Autowired
    private GroupHikeListService groupHikeListService;

    @GetMapping("{userName}/{isConnectedPage}")
    public List<String> listGroupHikes(
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
        return true;
    }
}
