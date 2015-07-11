import com.tum.assd.ASSDMain;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Main {
  public static void main(String[] args){
	ASSDMain assd = new ASSDMain();
	assd.loadASSD();
	if(assd.checkASSD()){
        	System.out.println("Decrypt: " + assd.decrypt("81896ecbb9afb39894c7144b5e962b08f132e9fd228539521aba75d4abbc18fe"));		
		System.out.println("Encrypt: " + assd.encrypt("GAURAV"));		
	}else {
		System.out.println("No ASSD Module Loaded !!");
	}
  }
}

