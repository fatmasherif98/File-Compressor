import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Compress {

	ArrayList<Integer> compressedBytes = new ArrayList<Integer>();
	HashMap<Character, String > codes = new HashMap<Character, String>();
	TreeNode root;
	boolean lastByte = false;
	boolean addedEOF = false;
	int lastByteIndex = 0;
	
	public Compress(HashMap<Character, String > codes, TreeNode root ) {
		this.codes = codes;
		this.root = root;
	}
	
	public String preOrderTarversal(  TreeNode root, StringBuilder str )
	{
		if( root == null )
			return str.toString();
		if( !root.isLeafNode() )
		{
			str.append(0);
			String tmp;
			tmp = preOrderTarversal( root.left, str);
			tmp = preOrderTarversal( root.right, new StringBuilder(tmp));
			return tmp;
		} else
		{
			str.append(1);
			str.append(root.c);
			return str.toString();
		}
	}
	
	public void createHeaderBytes( String header )
	{
		int currentB = 0;
        int len = header.length(), currentIndex = 0;
        
        for(int i=0; i<len; i++) //loops on every character in header
        {
        	if( header.charAt(i) == '1')
        	{
        		//set the bit with index currentIndex to 1 in currentB
        		currentB = currentB | (1<< currentIndex);
        		currentIndex++;
        	}else if( header.charAt(i) == '0')
        	{
        		//do nothing just increment the index
        		currentIndex++;
        	}
        	else 
        	{
        		//we have an 8 bit character which could overflow our byte buffer
        		int tempChar = header.charAt(i);
        		if( currentIndex != 0)
        		{
        			tempChar = tempChar << currentIndex; //example: if current index was 3, that means that buffer has 3 bits already used
            		currentB = currentB | tempChar; //therefore shift character by 3 bits to the left, five bits will be added to buffer
            		compressedBytes.add(currentB);  //or the buffer& shifter character
            		currentB = 0; //add old buffer to arrayList and empty buffer
            		
            		tempChar = header.charAt(i);  //to get the rest of the bits
            		tempChar = tempChar >> (8-currentIndex); //example: shift the five bits that we already added, out of the character (8-3) = 5
            		currentB = currentB | tempChar;  // or the buffer and character, current Index should  still be 3 but for the new buffer
        		}else
        		{//the buffer is empty and can take the whole character
        			currentB = currentB | tempChar;
            		compressedBytes.add(currentB);
            		currentB = 0; //currentIndex is still zero
        		}
        	
        	}
        	
        	if( currentIndex == 8) //if our buffer is full, add it to the array
        	{
        		compressedBytes.add(currentB);
        		currentB = 0;
        		currentIndex = 0;
        	}
        	
        }
        
        if( currentIndex != 0)
        {
        	lastByte = true;
        	lastByteIndex = currentIndex;
        }
	}
	
	public void createCompressedFile(String filePath) {
		
		FileInputStream in = null;
       

        try {
            in = new FileInputStream(filePath);
            int tempChar, currentIndex = 0, currentB = 0;
            while ((tempChar = in.read()) != -1 || !addedEOF ) { //not sure of this condition though, what happens when in.read is called twice when 
            	//file is already completely read 
                
            	if( lastByte ) //check only the first time entering the loop
            	{
            		currentIndex = this.lastByteIndex;
            		currentB = compressedBytes.get(compressedBytes.size()-1);
            		lastByte = false;
            	}
            	
            	String characCode;
            	
            	if( tempChar != -1)
            		 characCode = codes.get( (char)tempChar);
            	else {
            		characCode = codes.get( '-'); //whatever end of file charac would be
            		addedEOF = true;
            	}
            	
            	int len = characCode.length(); //loop on the character's code
            	
            	for(int i=0; i<len; i++)
            	{
            		if( characCode.charAt(i) == '1')
                	{
                		//set the bit with index currentIndex to 1 in currentB
                		currentB = currentB | (1<< currentIndex);
                		currentIndex++;
                	}else if( characCode.charAt(i) == '0')
                	{
                		//do nothing just increment the index
                		currentIndex++;
                	}
            		
            		if( currentIndex == 8 )
            		{
            			compressedBytes.add(currentB);
                		currentB = 0;
                		currentIndex = 0;
            		}
            	}
            	
           }
            
            in.close();
        } catch (IOException e) {
			
			e.printStackTrace();
		} 
	}

	public void writeToFile() {
		
	
        FileOutputStream out = null;

        try {
        	
        	File outputFile = new File("compressedFile.txt");
        	outputFile.createNewFile(); // if file already exists will do nothing 
            out = new FileOutputStream( outputFile, false);
            
            
            for( int character : compressedBytes)
            {
            	out.write(character);
            }
            
            if (out != null) {
                out.close();
            }
        } catch(IOException e) {
        	e.printStackTrace();
        	System.out.println("HELLO THERE");
        }
	}
}
