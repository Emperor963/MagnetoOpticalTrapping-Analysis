import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

public class Analysis{
    
    public static void main(String[] args){
        String file1 = "..\\Observed-Data\\Rb-95mA-3.5A-AbsData.csv";
        String file2 = "..\\Observed-Data\\Rb-95mA-3.5A-AbsData2.csv";
        String file3 = "..\\Observed-Data\\Rb-95mA-3.5A-AbsData4.csv";
        String file4 = "..\\Observed-Data\\Rb-95mA-3.5A-AbsData5.csv";

        File run1 = new File(file1);
        File run2 = new File(file2);
        File run3 = new File(file3);
        File run4 = new File(file4);
        Thread t1 = new Thread(new FReader(run1, 1));
        Thread t2 = new Thread(new FReader(run2, 2));
        Thread t3 = new Thread(new FReader(run3, 3));
        Thread t4 = new Thread(new FReader(run4, 4));

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

}


class FReader implements Runnable{
    File file;
    double[] timeStamp = new double[3522];
    double[] mWPower = new double[3522];
    int threadNumber;

    public FReader(File file,int threadNumber){
        this.file = file;
        this.threadNumber = threadNumber;
    }

    //Helper Method
    private double timeProcess(String time){
        //System.out.println(time);
        String[] stamps = time.split(":");
        int mins = Integer.parseInt(stamps[1]);
        double secs = Double.parseDouble(stamps[2]);

        double timeCalc = mins*60.0 + secs;

        return timeCalc;
    }
    
    //Helper Method
    private double dot(double[] arr1, double[] arr2){
        double pdt = 0;
        double[] arr2Fixed = new double[arr1.length];

        for(int i = 0; i < arr1.length; i++){
            if(i <= arr2.length - 1) arr2Fixed[i] = arr2[i];
            pdt += arr1[i] * arr2Fixed[i];
        }

        return pdt;
    }

    public ArrayList<Double[]> gaussianFilter(double[] x, double[] y, int width){
        ArrayList<Double[]> result = new ArrayList<Double[]>(); //stores the (x,y) pair in each element of the result[] array
        Double[] firstElement = {x[0],y[0]};
        result.add(firstElement);

        double[] kernel = new double[2*width - 1];

        double sigma = 0.4*width;  
        int center = width-1;
        double normalization = 0;
        for(int i = 0; i < kernel.length; i++){
            kernel[i] = Math.exp((-1) * Math.pow(((center-i) / sigma),2));
            normalization += kernel[i];
            //if(threadNumber == 1) continue;
            //System.out.println(kernel[i] + "   " + i);
        }

        int lengthY = y.length;
        int count = 1;
        for(int index = center; index < lengthY; index += 2*width-1){

            if(index + center > lengthY){
                System.out.println("entered");
                double[] filter = Arrays.copyOfRange(y, index - center, lengthY-1);
            }
            double[] filter = Arrays.copyOfRange(y, index-center, index+center);
            double filterY = dot(kernel, filter)/normalization;
            //if(threadNumber == 1) System.out.println(count + "  " + x[index] + "     " + filterY);
            Double[] toAdd = {x[index], filterY};
            result.add(toAdd);
            count++;
        }

        return result;
    }



    public ArrayList<Double[]> analyze(){
        Scanner scnr = null;
        try{
            scnr = new Scanner(file);
        }catch(FileNotFoundException e){
            System.out.println(e.getMessage());
        }
        
        //Skip header lines
        for(int i = 0; i < 15; i++){
            scnr.nextLine();
        }

        String[] firstReading = scnr.nextLine().split(",");
        double firstTime = timeProcess(firstReading[2].trim());
        timeStamp[0] = 0.0;
        mWPower[0] = Double.parseDouble(firstReading[3].trim()) * 1000;
        int i = 1;
        while(scnr.hasNextLine()){
            String[] reading = scnr.nextLine().split(",");
            //System.out.println(i+ "  " + reading[2] + "    " + reading[3]);
            timeStamp[i] = timeProcess(reading[2].trim()) - firstTime;
            mWPower[i] = Double.parseDouble(reading[3].trim()) * 1000;
            i++;
        }
        //System.out.println(timeStamp[3521]);
        //System.out.println(mWPower[3521]);
        return gaussianFilter(timeStamp, mWPower, 12);
    }

    @Override
    public void run(){
        ArrayList<Double[]> toWrite = analyze();
        String filename = "..\\Modified-Data\\GaussianData-ObservationNo" + String.valueOf(threadNumber);
        File file = new File(filename +".txt");
        FileWriter writer = null;
        try{
            writer = new FileWriter(file);
            for(int i = 0; i < toWrite.size(); i++){
                //System.out.println(Double.toString(toWrite[i][0]));
                writer.write(Math.floor(toWrite.get(i)[0]*1000)/1000 + "," + Math.floor(toWrite.get(i)[1]*1000)/1000 + "\n");

            }

            writer.close();
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        
   }
}
