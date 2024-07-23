package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class ExtraMileLambdaFunctionHandler implements RequestHandler<DynamodbEvent, String> {

    private final AmazonS3 s3Client;
    private final HtmlUpdater htmlUpdater;

    public ExtraMileLambdaFunctionHandler() {
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
        this.htmlUpdater = new HtmlUpdater(s3Client, "website-hosting-mentoring", "index.html");
    }

    @Override
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        htmlUpdater.updateHtml(dynamodbEvent);
        return "OK";
    }
}
