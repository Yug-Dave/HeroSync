package de.fhdo.HeroSync.controller;

import de.fhdo.HeroSync.dto.HabitDto;
import de.fhdo.HeroSync.entity.User;
import de.fhdo.HeroSync.service.HabitService;
import de.fhdo.HeroSync.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/habits")
@RequiredArgsConstructor
public class HabitRestController {
    private final HabitService habitService;
    private final DashboardService dashboardService;

    @GetMapping("")
    public List<HabitDto> list(Principal p) {
        User user = dashboardService.findUserByEmail(p.getName());
        return habitService.listAllDtos(user);
    }

    @PostMapping("")
    public HabitDto create(Principal p, @RequestBody HabitDto d) {
        User user = dashboardService.findUserByEmail(p.getName());
        return habitService.createDto(user, d.getName(), d.getDescription(), d.getXpValue());
    }

    @PutMapping("/{id}")
    public HabitDto update(Principal p, @PathVariable Long id, @RequestBody HabitDto d) {
        User user = dashboardService.findUserByEmail(p.getName());
        return habitService.updateDto(user, id, d.getName(), d.getDescription(), d.isArchived(), d.getXpValue());
    }

    @DeleteMapping("/{id}")
    public void delete(Principal p, @PathVariable Long id) {
        User user = dashboardService.findUserByEmail(p.getName());
        habitService.delete(user, id);
    }
}
