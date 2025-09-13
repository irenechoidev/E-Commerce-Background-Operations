package ecommerce.background;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.util.List;

public class ECommerceBackgroundOperationsHandler implements
        RequestHandler<SQSEvent, Object> {

  private static final String EVENT_NAME_KEY = "eventName";
  private static final String DYNAMO_DB_KEY = "dynamodb";
  private static final String OLD_IMAGE_KEY = "OldImage";
  private static final String IMAGE_FIELD_KEY = "imageKey";
  private static final String STRING_FIELD_KEY = "S";

  private static final String BUCKET_NAME = "e-commerce-images-bucket-v1";
  private static final String REMOVE_OPERATION = "REMOVE";

  private static final SdkHttpClient httpClient = UrlConnectionHttpClient.builder().build();
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final S3Client s3Client = S3Client.builder()
          .httpClient(httpClient)
          .build();

  @Override
  public Object handleRequest(SQSEvent event, Context context)
  {
    LambdaLogger logger = context.getLogger();
    logger.log(event.toString());

    List<SQSMessage> messages = event.getRecords();
    for (SQSMessage message : messages) {
        try {
            String body = message.getBody();
            JsonNode root = objectMapper.readTree(body);

            String eventName = root.get(EVENT_NAME_KEY).asText();
            String imageKey = root.get(DYNAMO_DB_KEY)
                                  .get(OLD_IMAGE_KEY)
                                  .get(IMAGE_FIELD_KEY)
                                  .get(STRING_FIELD_KEY)
                                  .asText();

            if (eventName.equals(REMOVE_OPERATION)) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                                .bucket(BUCKET_NAME)
                                .key(imageKey + ".jpg")
                        .build());
            }
        } catch (JsonProcessingException e) {
            return Response.builder()
                    .statusCode(502)
                    .build();
        }
    }

    return Response.builder()
            .statusCode(200)
            .build();
  }
}
