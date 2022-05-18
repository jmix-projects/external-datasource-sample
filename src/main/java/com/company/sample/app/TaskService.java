package com.company.sample.app;

import com.company.sample.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Arrays;
import java.util.List;

@Component
public class TaskService {

    public static final String TASKS_BASE_URL = "http://localhost:18080/tasks";

    @Autowired
    private RestTemplate restTemplate;

    public List<Task> loadTasks() {
        Task[] tasks = restTemplate.getForObject(TASKS_BASE_URL, Task[].class);
        return Arrays.asList(tasks);
    }

    public Task saveTask(Task task) {
        String url = task.getId() != null ?
                TASKS_BASE_URL + "/"  + task.getId() :
                TASKS_BASE_URL;
        ResponseEntity<Task> response = restTemplate.postForEntity(url, task, Task.class);
        return response.getBody();
    }

    public void deleteTask(Task task) {
        restTemplate.delete(TASKS_BASE_URL + "/" + task.getId());
    }
}