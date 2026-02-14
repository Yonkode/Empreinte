public class Test {
    public static void main(String[] args){
        for (int i=0; i<10; i++){
            for(int j=0; j<10; j++){
                double r=Math.sqrt(i*i + j*j);
                double theta = Math.atan2(j,i);
                theta = theta*180/Math.PI;
                System.out.print("Pour " + i + "," + j + " " + "r = "  + r +" "+ "thetha = "+ theta +" ");
            }
            System.out.println();
        }
    }
}
