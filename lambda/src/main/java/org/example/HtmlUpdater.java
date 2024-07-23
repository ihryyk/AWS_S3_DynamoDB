package org.example;

import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HtmlUpdater {
    private final AmazonS3 s3Client;
    private final String bucketName;
    private final String objectKey;
    private final ProductActionHandler productActionHandler;

    public HtmlUpdater(AmazonS3 s3Client, String bucketName, String objectKey) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.productActionHandler = new ProductActionHandler();
    }

    public void updateHtml(DynamodbEvent dynamodbEvent) {
        Document doc = downloadHtml();
        Element productTable = doc.getElementById("productTable");
        for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
            Map<String, AttributeValue> itemValues = record.getDynamodb().getNewImage();
            System.out.println("itemValues: " + itemValues);
            if (itemValues == null) {
                continue;
            }
            String id = itemValues.get("Id").getS();
            String name = itemValues.get("name").getS();
            switch (record.getEventName()) {
                case "INSERT":
                    productActionHandler.handleInsert(productTable, id, name);
                    break;
                case "MODIFY":
                    productActionHandler.handleModify(doc, id, name);
                    break;
                default:
                    break;
            }
        }
        uploadHtml(doc);
    }

    private Document downloadHtml() {
        S3Object s3Object = s3Client.getObject(bucketName, objectKey);
        try (InputStream objectData = s3Object.getObjectContent()) {
            return Jsoup.parse(objectData, null, "");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load existing HTML page from bucket: " + bucketName, e);
        }
    }

    private void uploadHtml(Document doc) {
        InputStream updatedHtml = new ByteArrayInputStream(doc.html().getBytes());
        s3Client.putObject(bucketName, objectKey, updatedHtml, null);
    }
}
