import java.sql.*;
import java.util.*;

public class Customer {
    private Connection con;
    private Class c;
    private PreparedStatement ps;
    private Statement st;
    private Scanner sc=new Scanner(System.in);
    private double totalCost;
    List<CustomerBill>list =new ArrayList<>();
    public Customer() {
        try {
            c=Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "praveenlaehss1", "praveenlaehss1");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void buyItem(int productId) throws Exception{
        System.out.println("Product Quantity: ");
        int quantity=sc.nextInt();
        ps=con.prepareStatement("select * from products where productId = ?");
        ps.setInt(1,productId);
        ResultSet rs=ps.executeQuery();
        if(rs.next()) {
            list.add(new CustomerBill(rs.getInt(1), rs.getString(2), rs.getDouble(3), quantity));
            System.out.println("Item added");
        }
        else{
            throw new ProductNotFoundException();
        }
        ps=con.prepareStatement("select quantity from products where productId = ?");
        ps.setInt(1,productId);
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            int availableQuantity = resultSet.getInt(1);
            if (availableQuantity >= quantity) {
                int updatedQuantity = availableQuantity - quantity;
                ps = con.prepareStatement("UPDATE products SET quantity = ? WHERE productId = ?");
                ps.setInt(1, updatedQuantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }
            else{
                throw new MinimumStockException();
            }
        }
        else{
            throw new ProductNotFoundException();
        }
    }
    public void showBuyItems() throws Exception{
        totalCost=0;
        for(CustomerBill c:list){
            System.out.println(c);
            totalCost+=(c.getPrice()*c.getQuantity());
        }
        System.out.println("\n\t\tTotal cost : "+totalCost);
    }
    public void removeItem() throws Exception{
        System.out.println("Product Id: ");
        int productId=sc.nextInt();
        int quantity=0;
        double price=0;
        for(CustomerBill c:list){
            if(c.getProductId()==productId){
                quantity=c.getQuantity();
                price=c.getPrice();
                list.remove(c);
            }
        }
        totalCost-=(quantity*price);
        System.out.println("Item removed");
        ps=con.prepareStatement("select quantity from products where productId = ?");
        ps.setInt(1,productId);
        ResultSet rs= ps.executeQuery();
        if(rs.next()){
            int availableQuantity=rs.getInt(1);
            int updatedQuantity=availableQuantity+quantity;
            ps=con.prepareStatement("update products set quantity = ? where productId = ?");
            ps.setInt(1,updatedQuantity);
            ps.setInt(2,productId);
            ps.executeQuery();
        }
    }
    public void showAllItems() throws Exception{
        System.out.println("\nShowing all products: ");
        ps=con.prepareStatement("select productId,productName,price from products order by productId");
        System.out.format("%-30s%-30s%-30s\n","ProductId","ProductName","Price");
        ResultSet rs=ps.executeQuery();
        while(rs.next()){
            System.out.format("%-30s%-30s%-30s\n",rs.getInt(1),rs.getString(2),rs.getDouble(3));
        }
    }
    public void buyItem1() {
        try {
            System.out.println("To terminate buying enter 100 as Product Id");
            System.out.print("Enter Product Id to buy : ");
            int productId = sc.nextInt();

            while (productId != 100) {
                ps = con.prepareStatement("select * from products where productId = ?");
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();

                if (rs.next() && productId == rs.getInt(1) ) {
                    System.out.print("Product Quantity : ");
                    int quantity = sc.nextInt();
                    if(rs.getInt(4) <= quantity) {
                        throw new MinimumStockException();
                    }
                    else{
                        int finalProductId = productId;
                        Optional<CustomerBill> obj = list.parallelStream().filter(c -> c.getProductId() == finalProductId).findFirst();
                        //int prevQuantity = 0;
                        if(obj.isPresent())
                        {
                            //obj.get().getOrderedQuantity += quantity;
                            obj.get().setQuantity(obj.get().getQuantity() + quantity);
                        }
                        else {
                            list.add(new CustomerBill(rs.getInt(1), rs.getString(2), rs.getInt(3), quantity));
                        }
                        PreparedStatement ps2 = con.prepareStatement("update products set quantity = ? where ProductId = ?");
                        ps2.setInt(1,rs.getInt(4) - quantity );
                        ps2.setInt(2,productId);
                        ps2.executeUpdate();
                    }

                }
                else{
                    throw new ProductNotFoundException();
                    //System.out.println("Item not found\nPlease....Try again");
                }
                System.out.print("Enter Product Id to buy : ");
                productId = sc.nextInt();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    public void updateCart1(){
        try {
            System.out.print("Enter Product id to update your order with new quantity: ");
            int productId = sc.nextInt();
            System.out.print("Enter the quantity you have to remove : ");
            int updateQuantity = sc.nextInt();

            Optional<CustomerBill> option = list.stream().filter(x -> x.getProductId() == productId).findFirst();
            CustomerBill obj = option.get();
            if (option.isPresent()) {
                PreparedStatement pst = con.prepareStatement("select * from products where productId = ?");
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    pst = con.prepareStatement("update products set quantity = ? where productId = ?");
                    if (rs.getInt(4) + obj.getQuantity() >= updateQuantity) {
                        if (updateQuantity < obj.getQuantity()) {
                            pst.setInt(1, rs.getInt(4) + obj.getQuantity() - updateQuantity);
                        } else {
                            pst.setInt(1, rs.getInt(4) - updateQuantity + obj.getQuantity());
                        }
                        pst.setInt(2,productId);
                        pst.executeUpdate();
                        obj.setQuantity(updateQuantity);
                        //obj.setCostOfGivenQuantity(obj.getPrice() * updateQuantity);
                        System.out.println("Cart updated successfully");
                    } else {
                        throw new MinimumStockException();
                    }
                }
            }else {
                System.out.println("Item not found exception");
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
