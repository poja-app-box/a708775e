package com.example.demo.endpoint.rest.controller;

import static software.amazon.awssdk.regions.Region.EU_WEST_3;

import com.example.demo.endpoint.event.EventProducer;
import com.example.demo.endpoint.event.model.UploadRequested;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.services.cloudformation.model.StackResourceSummary;

@Slf4j
@AllArgsConstructor
@RestController
public class CloudController {
  private final CloudFormationClient cfClient;
  private final ObjectMapper om;
  private final EventProducer<UploadRequested> eventProducer;

  @SneakyThrows
  @GetMapping("/cf/list_stacks")
  public List<ListStackResultItem> ListStacks() {
    log.info("LOG: list stacks should fail, cannot access cloudformation");
    return cfClient
        .listStackResourcesPaginator(req -> req.stackName("preprod-compute-a708775e-00ca28d3"))
        .stackResourceSummaries()
        .stream()
        .map(ListStackResultItem::from)
        .toList();
  }

  @PostMapping("/s3/upload")
  public String UploadWithOwnedAndExternalBucket(@RequestBody UploadBody body) {
    var eventId = body.eventId();
    eventProducer.accept(List.of(new UploadRequested(eventId)));
    return "Event(id=" + eventId + ") sent";
  }

  public record UploadBody(String eventId) {}

  public record ListStackResultItem(
      String physicalResourceId, String logicalResourceId, String resourceType) {
    static ListStackResultItem from(StackResourceSummary that) {
      return new ListStackResultItem(
          that.physicalResourceId(), that.logicalResourceId(), that.resourceType());
    }
  }

  @Configuration
  public static class CloudConf {
    @Bean
    public CloudFormationClient cfClient() {
      return CloudFormationClient.builder().region(EU_WEST_3).build();
    }
  }
}
