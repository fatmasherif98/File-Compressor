import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Decompressor {

	TreeNode root =null;
	static BitReader reader = null;
	BufferedWriter out = null;
	
	public Decompressor(String pathName) throws IOException
	{
		reader = new BitReader(pathName);
		String substring = (String) pathName.subSequence(0, pathName.lastIndexOf("compressed.txt"));
	    out = new BufferedWriter(new FileWriter(substring + "decompressed.txt"));
	
	}
	
	public  TreeNode reconstructHuffmanTree() throws IOException {
		 if (reader.readBit() == 1)
		    {
			 	TreeNode t = new TreeNode();
			 	t.c=(char)reader.readByte();
			 	t.right=null;
			 	t.left=null;
			 	return t;
		    }
		    else
		    {
		        TreeNode leftChild = reconstructHuffmanTree();
		        TreeNode rightChild = reconstructHuffmanTree();
		    	TreeNode t = new TreeNode();
			 	t.c='-';
			 	t.right=null;
			 	t.left=null;
			 	return t;
		    }
	}

	
}
