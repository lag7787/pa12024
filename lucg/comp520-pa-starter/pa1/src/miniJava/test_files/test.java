// what about at the beginning 
package miniJava.test_files;

/*
 * testing commewnt seperators
 */
class test {

    public static void main(String[] args) {
        int num = 312;
        int test_____identifier = 12;
        System.out.println(2 == 2);
        //System.out.println(2 == (1 + 1));
        System.out.println(); // do inline work as well
        System.out.println(1321412412);
        boolean a = true;
        boolean b = false;
        orMethod(a,b);
        orMethod(a,b);
    }

    public static boolean orMethod(boolean a, boolean b) {

        return a || b;

    }

    public static boolean andMethod(boolean a, boolean b) {

        return a && b;

    }
    
}
