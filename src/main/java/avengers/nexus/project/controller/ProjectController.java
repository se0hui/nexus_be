package avengers.nexus.project.controller;

import avengers.nexus.project.dto.CreateProjectDto;
import avengers.nexus.project.dto.SubmitApplicationDto;
import avengers.nexus.project.entity.Project;
import avengers.nexus.project.service.ProjectService;
import avengers.nexus.project.wanted.domain.Wanted;
import avengers.nexus.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Project", description = "프로젝트 관련 API")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if(user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        return user;
    }


    @GetMapping("/{id}")
    @Operation(summary = "프로젝트 조회", description = "해당 id의 프로젝트를 조회합니다.")
    @Parameter(name = "id", description = "프로젝트 ID", required = true)
    public Project getProject(@PathVariable String id) {
        return projectService.getProject(id);
    }


    @GetMapping("/")
    @Operation(summary = "모든 프로젝트 조회", description = "모든 프로젝트를 조회합니다.")
    public List<Project> getAllProject(){
        return projectService.getAllProject();
    }


    @GetMapping("/page/{page}")
    @Operation(summary = "페이지별 프로젝트 조회", description = "페이지별 프로젝트를 조회합니다.")
    @Parameter(name = "page", description = "페이지 번호", required = true)
    public List<Project> getProjectByPage(@PathVariable int page) {
        return projectService.getProjectsByPage(page);
    }


    @PostMapping("/")
    @Operation(summary = "프로젝트 생성", description = "프로젝트를 생성합니다.")
    @Parameter(name = "project", description = "CreateProjectDto", required = true)
    public ResponseEntity<?> createProject(@RequestBody CreateProjectDto project) {
        User user = getAuthenticatedUser();
        try {
            Project result = projectService.createProject(project,user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Project creation failed!");
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @Parameter(name = "id", description = "프로젝트 ID", required = true)
    public void deleteProject(@PathVariable String id) {
        User user = getAuthenticatedUser();
        projectService.deleteProject(id, user);
    }

    @PostMapping("/{projectId}/member")
    public ResponseEntity<?> addMember(@PathVariable String projectId, @RequestBody Long memberId) {
        User user = getAuthenticatedUser();
        Project project = projectService.getProject(projectId);
        if(!project.getOwner().equals(user.getId())) {
            return ResponseEntity.badRequest().body("Only owner can add member!");
        }
        if(project.getMembers().contains(memberId)) {
            return ResponseEntity.badRequest().body("Member already exists!");
        }
        if (user.getId().equals(memberId)) {
            return ResponseEntity.badRequest().body("Owner cannot be added as member!");
        }
        try{
            project.addMember(memberId);
            return ResponseEntity.ok("Member added successfully!");
        }catch (Exception e) {
            return ResponseEntity.badRequest().body("Member addition failed!");
        }
    }
}
