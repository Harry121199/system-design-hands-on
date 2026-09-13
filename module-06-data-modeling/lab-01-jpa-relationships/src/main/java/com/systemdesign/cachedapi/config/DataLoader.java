package com.systemdesign.cachedapi.config;

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

    private static final String[] STATUSES = {"TODO", "IN_PROGRESS", "DONE"};
    private static final String[] PRIORITIES = {"LOW", "MEDIUM", "HIGH"};

    public DataLoader(UserRepository userRepository, ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

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

        // 50,000 tasks (500 per project)
        List<Task> batch = new ArrayList<>();
        for (Project project : projects) {
            for (int k = 1; k <= 500; k++) {
                Task task = new Task(
                        project.getName() + " - Task " + k,
                        "Description for task " + k,
                        PRIORITIES[random.nextInt(PRIORITIES.length)],
                        project
                );
                task.setStatus(STATUSES[random.nextInt(STATUSES.length)]);
                batch.add(task);

                if (batch.size() == 1000) {
                    taskRepository.saveAll(batch);
                    batch.clear();
                }
            }
        }
        System.out.println("Loaded 50,000 tasks (10 users × 10 projects × 500 tasks).");
    }
}