package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.service.grouphike.GroupHikeListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
