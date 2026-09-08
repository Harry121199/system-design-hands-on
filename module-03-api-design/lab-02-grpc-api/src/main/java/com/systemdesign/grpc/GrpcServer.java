package com.systemdesign.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder
                .forPort(9090)
                .addService(new TaskServiceImpl())
                .build()
                .start();

        System.out.println("gRPC server running on port 9090...");

        server.awaitTermination();
    }
}