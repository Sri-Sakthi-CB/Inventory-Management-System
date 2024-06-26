import java.sql.*;
import java.util.*;

public class Main {
    Scanner sc=new Scanner(System.in);
    public void owners() throws Exception{
        Owner admin = new Owner();
        ownerOptions();
        int i = sc.nextInt();
        while(i <= 6){
            switch(i){
                case 1: admin.addProduct();break;
                case 2: admin.removeProduct();break;
                case 3: admin.updateProduct();break;
                case 4: admin.showAllProduct(); break;
                default:
                    System.err.println("\t\tAdmin operations are terminated");
                    System.exit(0);
            }
            System.out.println("\nAgain operation:");
            ownerOptions();
            i = sc.nextInt();
        }
    }
    public void ownerOptions(){
        System.out.println("Enter 1 : Add product");
        System.out.println("Enter 2 : Remove product");
        System.out.println("Enter 3 : Update quantity of a product");
        System.out.println("Enter 4 : Display all items");
        System.out.println("Enter 6 : exit");
    }
    public void customers() throws Exception{
        Customer customer = new Customer();
        customer.showAllItems();
        customerOptions();
        int i = sc.nextInt();
        while(i<4){
            switch(i){
                case 1: customer.buyItem1(); break;
                case 2: customer.updateCart1(); break;
                case 3: customer.showBuyItems(); break;
                default:
                    customerOptions();
            }
            System.out.println("\nAgain operation:");
            customerOptions();
            i = sc.nextInt();
        }
    }
    public void customerOptions(){
        System.out.println("Enter 1 : Order Items");
        System.out.println("Enter 2 : update orders if any change in quantity");
        System.out.println("Enter 3 : To print bill");
        System.out.println("Enter 4 : To terminate all processes");
    }



    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        try {
            Main m=new Main();
            Class c=Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "srisakthi", "srisakthi");
            if(con==null){
                System.out.println("Server not connected");
            }
            else{
                System.out.println("Server connected");
                //System.out.println("Enter User type: C-Customer,O-Owner");
                System.out.println("Login Credential:");
                Login l=new Login();
                String userType=l.loginType();
                if(userType.equals("customer")){
                    m.customers();
                }
                else if(userType.equals("owner")){
                    m.owners();
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }


    }
}
