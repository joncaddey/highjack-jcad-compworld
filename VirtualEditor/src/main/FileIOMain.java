package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import phyObj.PhyCircle;
import phyObj.PhyObject;
import phyObj.PhyPolygon;

public class FileIOMain {

	
	public static void main(String args[]) {
		PhyObject v = PhyPolygon.getEqTriangle(20);
		FileOutputStream fos= null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream("data.txt");
			out = new ObjectOutputStream(fos);
			out.writeObject(v);
			out.close();
			
			FileInputStream fis = new FileInputStream("data.txt");
			ObjectInputStream in = new ObjectInputStream(fis);
			PhyPolygon u = (PhyPolygon) in.readObject();
			System.out.println(Arrays.toString(((Triangle)u.getRenderable()).vertices));
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException f) {
			System.err.println(f);
		}
	}
}
