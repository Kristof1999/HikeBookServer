package hu.kristof.nagy.hikebookserver.api.routes;

import hu.kristof.nagy.hikebookserver.model.BrowseListItem;
import hu.kristof.nagy.hikebookserver.model.routes.EditedUserRoute;
import hu.kristof.nagy.hikebookserver.model.routes.UserRoute;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users/routes/")
public class UserRouteController {

    @PutMapping("{userName}/{routeName}")
    public boolean createUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName,
            @RequestBody UserRoute userRoute
    ) {

    }

    @GetMapping("{userName}")
    public List<UserRoute> loadUserRoutes(
            @PathVariable String userName
    ) {

    }

    @GetMapping("{userName}/{routeName}")
    public UserRoute loadUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {

    }

    @DeleteMapping("{userName}/{routeName}")
    public boolean deleteUserRoute(
            @PathVariable String userName,
            @PathVariable String routeName
    ) {

    }

    @PutMapping("edit/{userName}/{routeName}")
    public boolean editUserRoute(
        @PathVariable String userName,
        @PathVariable String oldRouteName,
        @RequestBody EditedUserRoute editedUserRoute
    ) {

    }

    @GetMapping("")
    public List<BrowseListItem> listUserRoutes() {

    }

}
