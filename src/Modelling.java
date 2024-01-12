import com.wolfram.jlink.*;
import java.io.File;


public class Modelling {

    public static void kernelSetup(KernelLink kl, String[] args){

        try{
            kl = MathLinkFactory.createKernelLink(args);
        }catch (MathLinkException e){
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args){
        KernelLink kl = null;

        kernelSetup(kl,args);

        File ModData = new File("..\\Modified-Data");
        File[] processedDataFiles = ModData.listFiles();

        try{
            String command = "Import[" + processedDataFiles[0] + "]";
            kl.evaluate(command);
            kl.waitForAnswer();

            double[][] result = kl.getDoubleArray2();

            System.out.println(result[0]);
        }catch(MathLinkException e){
            System.out.println(e.getMessage());
        }


        if(kl != null) kl.close();
    }
}
