package org.example.client.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

        int port=5000;

        executorService.shutdown();
    }
}