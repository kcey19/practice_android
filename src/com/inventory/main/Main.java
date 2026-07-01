package com.inventory.main;

import com.inventory.model.Product;
import com.inventory.service.ProductManager;

import java.util.InputMismatchException;
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
            System.out.println("3. Delete Product");
            System.out.println("4. Exit Application");
            System.out.print("Please select an option (1-4): ");

            int choice ;
            try{
                System.out.println("Choose an option :");
                choice = scanner.nextInt();
                scanner.nextLine();
            }
            catch(InputMismatchException e){
                System.out.println(" ❌Invalid Input. Please enter a valid number.");
                scanner.nextLine();
                continue;
            }

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
                    System.out.print("Enter Product ID to delete: ");
                    int idToDelete = scanner.nextInt();
                    manager.deleteProduct(idToDelete);
                    break;

                case 4:
                    isRunning = false;
                    System.out.println("🔌 Shutting down system. Goodbye!");
                    break;


                default:
                    System.out.println("❌ Invalid choice! Please select a number between 1 and 4");
            }
        }

        scanner.close();
    }
}
