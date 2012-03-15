package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import phyObj.PhyComposite;
import phyObj.PhyObject;

public class FileIOMain {

	
	public static void main(String args[]) {
		List<PhyObject> list = new ArrayList<PhyObject>(10);
		for (int i = 0; i < 10; i++) {
			PhyObject v = PhyComposite.getRocket(i + 1);
			list.add(v);
		}
		
		FileOutputStream fos= null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream("data.txt");
			out = new ObjectOutputStream(fos);
			out.writeObject(list);
			out.close();
			
			FileInputStream fis = new FileInputStream("data.txt");
			ObjectInputStream in = new ObjectInputStream(fis);
			List<PhyObject> u = (List) in.readObject();
			for (PhyObject i : u) {
				System.out.println(i.getMass());
			}
		} catch (IOException e) {
			System.err.println(e);
		} catch (ClassNotFoundException f) {
			System.err.println(f);
		}
	}
}
