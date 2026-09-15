package com.systemdesign.cachedapi.config;

import com.systemdesign.cachedapi.model.DenormalizedTask;
import com.systemdesign.cachedapi.repository.DenormalizedTaskRepository;
import com.systemdesign.cachedapi.model.Project;
import com.systemdesign.cachedapi.model.Task;
import com.systemdesign.cachedapi.model.User;
import com.systemdesign.cachedapi.repository.ProjectRepository;
import com.systemdesign.cachedapi.repository.TaskRepository;
import com.systemdesign.cachedapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final DenormalizedTaskRepository denormalizedTaskRepository;

    public DataLoader(UserRepository userRepository, ProjectRepository projectRepository,
                      TaskRepository taskRepository, DenormalizedTaskRepository denormalizedTaskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.denormalizedTaskRepository = denormalizedTaskRepository;
    }

    private static final String[] STATUSES = {"TODO", "IN_PROGRESS", "DONE"};
    private static final String[] PRIORITIES = {"LOW", "MEDIUM", "HIGH"};

    @Override
    public void run(String... args) throws Exception {
        if (taskRepository.count() > 0) return;

        Random random = new Random();

        // 10 users
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            users.add(new User("User " + i, "user" + i + "@example.com"));
        }
        userRepository.saveAll(users);
        System.out.println("Loaded 10 users.");

        // 100 projects (10 per user)
        List<Project> projects = new ArrayList<>();
        for (User user : users) {
            for (int j = 1; j <= 10; j++) {
                projects.add(new Project(
                        user.getName() + " - Project " + j,
                        "Description for project " + j,
                        user
                ));
            }
        }
        projectRepository.saveAll(projects);
        System.out.println("Loaded 100 projects.");

        // 50,000 denormalized tasks (same data, flat structure)
        List<DenormalizedTask> denBatch = new ArrayList<>();
        for (Project project : projects) {
            User owner = project.getUser();
            for (int k = 1; k <= 500; k++) {
                DenormalizedTask dt = new DenormalizedTask(
                        project.getName() + " - Task " + k,
                        "Description for task " + k,
                        STATUSES[random.nextInt(STATUSES.length)],
                        PRIORITIES[random.nextInt(PRIORITIES.length)],
                        project.getId(), project.getName(),
                        owner.getId(), owner.getName()
                );
                denBatch.add(dt);

                if (denBatch.size() == 1000) {
                    denormalizedTaskRepository.saveAll(denBatch);
                    denBatch.clear();
                }
            }
        }
        System.out.println("Loaded 50,000 denormalized tasks.");
    }
}