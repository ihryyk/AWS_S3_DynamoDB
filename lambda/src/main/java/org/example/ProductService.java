package org.example;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductService {

    private AmazonDynamoDB amazonDynamoDB;
    private final String DYNAMODB_TABLE_NAME = "Product";
    private final Regions REGION = Regions.EU_CENTRAL_1;

    private AmazonDynamoDB getAmazonDynamoDB() {
        if (amazonDynamoDB == null) {
            this.amazonDynamoDB = AmazonDynamoDBClientBuilder.standard()
                    .withRegion(REGION)
                    .build();
        }
        return amazonDynamoDB;
    }

    public List<Product> getProducts() {
        ScanRequest scanRequest = new ScanRequest().withTableName(DYNAMODB_TABLE_NAME);
        ScanResult result = getAmazonDynamoDB().scan(scanRequest);
        List<Product> productList = new ArrayList<>();
        for (Map<String, AttributeValue> item : result.getItems()) {
            Product product = new Product();
            product.setId(item.get("Id").getS());
            product.setName(item.get("name").getS());
            productList.add(product);
        }
        return productList;
    }

}
