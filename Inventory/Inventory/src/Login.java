import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Login {
    private Connection con;
    private Class c;
    private PreparedStatement ps;
    Scanner sc=new Scanner(System.in);
    public Login(){
        try {
            c=Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "praveenlaehss1", "praveenlaehss1");

        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public String loginType() throws Exception{
        System.out.println("Username : ");
        String username=sc.nextLine();
        System.out.println("Password : ");
        String password=sc.nextLine();
        ps=con.prepareStatement("select * from login where username = ? and password = ?");
        ps.setString(1,username);
        ps.setString(2,password);
        ResultSet rs =ps.executeQuery();
        if(rs.next()){
            System.out.println("\n Welcome back "+rs.getString(1)+" as "+rs.getString(3));
            return rs.getString(3);
        }
        return "";
    }
}
