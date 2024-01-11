import com.wolfram.jlink.*;
import java.io.File;


public class Modelling {

    public void kernelSetup(KernelLink kl, String[] args){

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




        if(kl != null) kl.close();
    }
}
