package com.systemdesign.sql.config;

import com.systemdesign.sql.model.Task;
import com.systemdesign.sql.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Component
public class DataLoader implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private static final String[] STATUSES = {"TODO","IN_PROGRESS","DONE"};
    private static final String[] PRIORITIES = {"LOW","MEDIUM","HIGH"};

    public DataLoader(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(taskRepository.count()>0) return ;
        Random random = new Random();
        List<Task> tasks = new ArrayList<>();

        for(int i=0;i<50_000;i++) {
            Task task = new Task(
                    "Task "+i,
                    "Description for task number "+i,
                    PRIORITIES[random.nextInt(PRIORITIES.length)]
            );
            task.setStatus(STATUSES[random.nextInt(STATUSES.length)]);
            tasks.add(task);

            if(tasks.size() == 1000) {
                taskRepository.saveAll(tasks);
                tasks.clear();
            }
        }

        System.out.println("Loaded 50,000 tasks into the database.");
    }
}
