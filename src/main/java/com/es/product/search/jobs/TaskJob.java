package com.es.product.search.jobs;

import com.es.product.search.models.Task;
import com.es.product.search.respository.ProductRepository;
import com.es.product.search.respository.TaskRepository;
import com.es.product.search.service.IntegrationService;
import com.es.product.search.service.ProductIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskJob implements Job {

    private final ProductRepository productRepository;

    private final IntegrationService integrationService;

    private final TaskRepository taskRepository;


    @Override
    public void execute(JobExecutionContext context) {

        log.info("Starting TaskJob");

        String id = context.getMergedJobDataMap().getString("id");
        String name = context.getTrigger().getKey().getName();
        String group = context.getTrigger().getKey().getGroup();

        Task task = taskRepository.findById(UUID.fromString(id)).orElse(null);


        if(group.equals("product")) {
            assert task != null;
            if (task.isActive()) {
                integrationService.indexProducts();
            }
        }





        log.info("Finished TaskJob -> ID: {}, Name: {}, Group: {}", id, name, group);
    }
}


