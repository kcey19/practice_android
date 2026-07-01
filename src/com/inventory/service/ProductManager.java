package com.inventory.service;

import com.inventory.model.Product;
import java.util.ArrayList;

public class ProductManager {
    private final ArrayList<Product> stockList;

    public ProductManager() {
        this.stockList = new ArrayList<>();
    }
    public ProductManager(ArrayList<Product> stockList) {
        this.stockList = stockList;
    }
    public void addProduct(Product product) {
        stockList.add(product);
        System.out.println("✅ Success: " + product.getName() + " added to stock.");
    }
    public void viewInventory() {
        if (stockList.isEmpty()) {
            System.out.println("⚠️ Notice: The inventory is currently empty.");
            return;
        }

        System.out.println("\nCURRENT INVENTORY");
        System.out.printf("%-10s %-25s %-10s%n", "ID", "PRODUCT NAME", "QUANTITY");
        System.out.println("---------------------------------------------------");

        for (Product p : stockList) {
            System.out.printf("%-10d %-25s %-10d%n", p.getId(), p.getName(), p.getQuantity());
        }
        System.out.println(" \n");
    }

    public boolean deleteProduct(int id) {
        for (int i = 0; i < stockList.size(); i++) {
            if (stockList.get(i).getId() == id) {
                stockList.remove(i);
                System.out.println(" 🚮 Product with ID " + id + " has been successfully deleted.");
                return true;
            }
        }
        System.out.println(" ❌ Error: Product with ID " + id + " not found.");
        return false;
    }
}
