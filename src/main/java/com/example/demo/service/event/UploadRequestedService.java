package com.example.demo.service.event;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import com.example.demo.endpoint.event.model.UploadRequested;
import com.example.demo.file.bucket.BucketComponent;
import com.example.demo.file.bucket.BucketConf;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UploadRequestedService implements Consumer<UploadRequested> {

  private final BucketComponent ownedBucket;

  private static final String NON_OWNED_BUCKET_NAME =
      "prod-bucket-poja-idp-api-bucket-cxboigrytphi\n";

  @Override
  public void accept(UploadRequested event) {
    ownedBucket.upload(createFile("owned"), newInstant());

    var notOwnedBucket = new BucketComponent(new BucketConf("eu-west-3", NON_OWNED_BUCKET_NAME));
    notOwnedBucket.upload(createFile("not-owned"), newInstant());
  }

  private static String newInstant() {
    return now().truncatedTo(MILLIS).toString();
  }

  @SneakyThrows
  private static File createFile(String content) {
    Path tempFile = Files.createTempFile("newFile", null);
    Files.writeString(tempFile, content);
    return tempFile.toFile();
  }
}
