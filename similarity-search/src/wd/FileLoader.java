package wd;

import java.io.BufferedReader;
import java.io.FileReader;


/**
 *
 * @author Sergio
 */
public class FileLoader {

     
    
public String load(String file, int start, int end) throws Exception{
BufferedReader br = new BufferedReader(new FileReader(file));
StringBuilder sb = new StringBuilder();

try {
    String line = br.readLine();
    
    int index = 0;
    while (line != null) {
        //System.out.println(line);
        if (index> start)
            sb.append(line);
            
        line = br.readLine();
        if (end<index) break;
        index++;
    }
    

    
} finally {
    br.close();
}

return sb.toString();    
}


}
