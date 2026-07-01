package com.inventory.main;

import com.inventory.model.Product;
import com.inventory.service.ProductManager;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ProductManager manager = new ProductManager();
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        System.out.println("=== Welcome to the Professional Inventory System ===");

        while (isRunning) {
            System.out.println("1. View Current Inventory");
            System.out.println("2. Add New Product");
            System.out.println("3. Exit Application");
            System.out.print("Please select an option (1-3): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    manager.viewInventory();
                    break;

                case 2:
                    System.out.print("Enter Product ID (Integer): ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Product Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter Initial Stock Quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();

                    Product newProduct = new Product(id, name, quantity);
                    manager.addProduct(newProduct);
                    break;

                case 3:
                    isRunning = false;
                    System.out.println("🔌 Shutting down system. Goodbye!");
                    break;

                default:
                    System.out.println("❌ Invalid choice! Please select a number between 1 and 3.");
            }
        }

        scanner.close();
    }
}
