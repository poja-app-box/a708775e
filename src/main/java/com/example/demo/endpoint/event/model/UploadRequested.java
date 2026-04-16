package com.example.demo.endpoint.event.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor(onConstructor_ = @JsonCreator)
@EqualsAndHashCode(callSuper = false)
@Data
@ToString
public class UploadRequested extends PojaEvent {
  @JsonProperty("id")
  private final String id;

  @Override
  public Duration maxConsumerDuration() {
    return Duration.ofSeconds(10);
  }

  @Override
  public Duration maxConsumerBackoffBetweenRetries() {
    return Duration.ofMinutes(1);
  }
}
