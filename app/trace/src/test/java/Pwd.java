public class Pwd {

    public static void main(String[] args) {
        String userName="rockey09";
       String pwd=MD5.encrypt32("123456");
        System.out.println(MD5.encrypt32(userName + pwd));
    }

}
