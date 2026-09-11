package com.systemdesign.cachedapi.config;

import com.systemdesign.cachedapi.model.Task;
import com.systemdesign.cachedapi.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataLoader implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private static final String[] STATUS = {"TODO", "IN_PROGRESS", "DONE"};
    private static final String[] PRIORITIES = {"LOW", "MEDIUM", "HIGH"};

    public DataLoader(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        if (taskRepository.count() > 0) return;
        Random random = new Random();
        List<Task> tasks = new ArrayList<>();
        for(int i = 1; i <= 50_000; i++) {
            Task task = new Task("Task "+i,"Description "+i,PRIORITIES[random.nextInt(PRIORITIES.length)]);
            task.setStatus(STATUS[random.nextInt(STATUS.length)]);
            tasks.add(task);
            if(tasks.size() == 1000) {
                taskRepository.saveAll(tasks);
                tasks.clear();
            }
        }
        System.out.println("Loaded 50,000 tasks.");
    }
}
