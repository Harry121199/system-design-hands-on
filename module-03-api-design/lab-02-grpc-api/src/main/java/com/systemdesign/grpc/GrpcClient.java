package com.systemdesign.grpc;

import com.systemdesign.grpc.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class GrpcClient {

    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        TaskServiceGrpc.TaskServiceBlockingStub stub = TaskServiceGrpc.newBlockingStub(channel);

        // Create tasks
        System.out.println("=== Creating tasks ===\n");

        Task task1 = stub.createTask(CreateTaskRequest.newBuilder()
                .setTitle("Learn REST")
                .setDescription("Build a REST API")
                .setPriority("HIGH")
                .build());
        System.out.println("Created: " + task1.getId() + " - " + task1.getTitle());

        Task task2 = stub.createTask(CreateTaskRequest.newBuilder()
                .setTitle("Learn gRPC")
                .setDescription("Build a gRPC API")
                .setPriority("MEDIUM")
                .build());
        System.out.println("Created: " + task2.getId() + " - " + task2.getTitle());

        // List all
        System.out.println("\n=== Listing all tasks ===\n");
        TaskList allTasks = stub.listTasks(ListTasksRequest.newBuilder().build());
        allTasks.getTasksList().forEach(t ->
                System.out.println("  " + t.getId() + " | " + t.getTitle() + " | " + t.getStatus() + " | " + t.getPriority())
        );

        // Get one
        System.out.println("\n=== Get task by ID ===\n");
        Task fetched = stub.getTask(GetTaskRequest.newBuilder()
                .setId(task1.getId())
                .build());
        System.out.println("Fetched: " + fetched.getTitle() + " - " + fetched.getStatus());

        // Update
        System.out.println("\n=== Update task ===\n");
        Task updated = stub.updateTask(UpdateTaskRequest.newBuilder()
                .setId(task1.getId())
                .setStatus("IN_PROGRESS")
                .build());
        System.out.println("Updated: " + updated.getTitle() + " - " + updated.getStatus());

        // Delete
        System.out.println("\n=== Delete task ===\n");
        DeleteTaskResponse deleted = stub.deleteTask(DeleteTaskRequest.newBuilder()
                .setId(task2.getId())
                .build());
        System.out.println("Deleted: " + deleted.getSuccess());

        // Try to get deleted task
        System.out.println("\n=== Get deleted task (should fail) ===\n");
        try {
            stub.getTask(GetTaskRequest.newBuilder()
                    .setId(task2.getId())
                    .build());
        } catch (StatusRuntimeException e) {
            System.out.println("Error: " + e.getStatus().getCode() + " - " + e.getStatus().getDescription());
        }

        // Final list
        System.out.println("\n=== Final task list ===\n");
        TaskList finalList = stub.listTasks(ListTasksRequest.newBuilder().build());
        finalList.getTasksList().forEach(t ->
                System.out.println("  " + t.getId() + " | " + t.getTitle() + " | " + t.getStatus())
        );

        channel.shutdown();
    }
}