package com.systemdesign.grpc;

import com.systemdesign.grpc.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TaskServiceImpl extends TaskServiceGrpc.TaskServiceImplBase {

    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public void createTask(CreateTaskRequest request, StreamObserver<Task> responseObserver) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        String now = LocalDateTime.now().toString();

        Task task = Task.newBuilder()
                .setId(id)
                .setTitle(request.getTitle())
                .setDescription(request.getDescription())
                .setPriority(request.getPriority())
                .setStatus("TODO")
                .setCreatedAt(now)
                .setUpdatedAt(now)
                .build();

        tasks.put(id, task);

        responseObserver.onNext(task);
        responseObserver.onCompleted();
    }

    @Override
    public void getTask(GetTaskRequest request, StreamObserver<Task> responseObserver) {
        Task task = tasks.get(request.getId());

        if (task == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Task not found: " + request.getId())
                            .asRuntimeException()
            );
            return;
        }

        responseObserver.onNext(task);
        responseObserver.onCompleted();
    }

    @Override
    public void listTasks(ListTasksRequest request, StreamObserver<TaskList> responseObserver) {
        TaskList.Builder listBuilder = TaskList.newBuilder();

        tasks.values().stream()
                .filter(t -> request.getStatus().isEmpty()
                        || t.getStatus().equalsIgnoreCase(request.getStatus()))
                .filter(t -> request.getPriority().isEmpty()
                        || t.getPriority().equalsIgnoreCase(request.getPriority()))
                .forEach(listBuilder::addTasks);

        responseObserver.onNext(listBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateTask(UpdateTaskRequest request, StreamObserver<Task> responseObserver) {
        Task existing = tasks.get(request.getId());

        if (existing == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Task not found: " + request.getId())
                            .asRuntimeException()
            );
            return;
        }

        Task.Builder updated = existing.toBuilder();
        if (!request.getTitle().isEmpty()) updated.setTitle(request.getTitle());
        if (!request.getDescription().isEmpty()) updated.setDescription(request.getDescription());
        if (!request.getStatus().isEmpty()) updated.setStatus(request.getStatus());
        if (!request.getPriority().isEmpty()) updated.setPriority(request.getPriority());
        updated.setUpdatedAt(LocalDateTime.now().toString());

        Task result = updated.build();
        tasks.put(result.getId(), result);

        responseObserver.onNext(result);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTask(DeleteTaskRequest request, StreamObserver<DeleteTaskResponse> responseObserver) {
        boolean removed = tasks.remove(request.getId()) != null;

        responseObserver.onNext(
                DeleteTaskResponse.newBuilder()
                        .setSuccess(removed)
                        .build()
        );
        responseObserver.onCompleted();
    }
}