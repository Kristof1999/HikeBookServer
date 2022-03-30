package hu.kristof.nagy.hikebookserver.api;

import hu.kristof.nagy.hikebookserver.service.hike.UpdateAvgSpeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HikeController {
    @Autowired
    private UpdateAvgSpeedService updateAvgSpeedService;

    @PutMapping("avgSpeed/{userName}")
    public void updateAvgSpeed(
            @PathVariable String userName,
            @RequestBody Double avgSpeed
    )  {
        updateAvgSpeedService.updateAvgSpeed(userName, avgSpeed);
    }
}
