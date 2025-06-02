package com.es.product.search.controller;


import com.es.product.search.models.Task;
import com.es.product.search.respository.TaskRepository;
import com.es.product.search.service.JobService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    private final JobService jobService;

    private final Scheduler scheduler;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody final Task request) {
        Task task = taskRepository.save(request);
        try {
            jobService.scheduleTaskJob(task);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(taskRepository.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        Task task = taskRepository.findById(UUID.fromString(id)).orElse(null);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        boolean isUnScheduledJob = jobService.unScheduleTaskJob(task);
        if (isUnScheduledJob) {
            taskRepository.delete(task);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/jobs")
    public List<Map<String, String>> listAllJobs() throws SchedulerException {
        List<Map<String, String>> jobList = new ArrayList<>();

        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

                for (Trigger trigger : triggers) {
                    Map<String, String> jobInfo = new HashMap<>();
                    jobInfo.put("id", jobKey.getName());
                    jobInfo.put("name", trigger.getKey().getName());
                    jobInfo.put("group", jobKey.getGroup());
                    jobInfo.put("nextFireTime", String.valueOf(trigger.getNextFireTime()));
                    jobInfo.put("previousFireTime", String.valueOf(trigger.getPreviousFireTime()));
                    jobInfo.put("triggerState", scheduler.getTriggerState(trigger.getKey()).name());

                    // ✅ Add Cron Expression if it is a CronTrigger
                    if (trigger instanceof CronTrigger cronTrigger) {
                        jobInfo.put("cronExpression", cronTrigger.getCronExpression());
                    } else {
                        jobInfo.put("cronExpression", "N/A");
                    }


                    jobList.add(jobInfo);
                }
            }
        }

        return jobList;
    }


}
