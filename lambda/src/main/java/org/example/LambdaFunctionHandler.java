package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.ByteArrayInputStream;
import java.util.List;

public class LambdaFunctionHandler implements RequestHandler<DynamodbEvent, String> {

    private final ProductService productService;
    private final AmazonS3 s3Client;

    public LambdaFunctionHandler() {
        this.productService = new ProductService();
        this.s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    private String generateProductTable(List<Product> productList) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Product List</title><style>" +
                "table {width: 100%; border-collapse: collapse;}" +
                "table, th, td {border: 1px solid black; padding: 5px;}" +
                "</style></head><body><table id='productTable'><tr><th>ID</th><th>Name</th></tr>");
        for (Product product : productList) {
            sb.append("<tr id='product").append(product.getId()).append("'><td>")
                    .append(product.getId()).append("</td><td>").append(product.getName())
                    .append("</td></tr>");
        }
        sb.append("</table></body></html>");
        return sb.toString();
    }

    @Override
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {
        String htmlPage = generateProductTable(productService.getProducts());
        s3Client.putObject("website-hosting-mentoring", "index.html",
                new ByteArrayInputStream(htmlPage.getBytes()), null);
        return "OK";
    }

}
