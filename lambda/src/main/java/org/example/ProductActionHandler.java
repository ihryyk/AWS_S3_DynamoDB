package org.example;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ProductActionHandler {

    void handleInsert(Element productTable, String id, String name) {
        productTable.append("<tr id='product" + id + "'><td>" + id + "</td><td>" + name + "</td></tr>");
    }

    void handleModify(Document doc, String id, String name) {
        Element row = doc.getElementById("product" + id);
        if(row != null) {
            row.getElementsByTag("td").last().text(name);
        }
    }

}
