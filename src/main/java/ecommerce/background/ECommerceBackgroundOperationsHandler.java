package ecommerce.background;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

public class ECommerceBackgroundOperationsHandler implements
        RequestHandler<SQSEvent, Object> {

  @Override
  public Object handleRequest(SQSEvent event, Context context)
  {
    LambdaLogger logger = context.getLogger();
    return new Object();
  }
}
