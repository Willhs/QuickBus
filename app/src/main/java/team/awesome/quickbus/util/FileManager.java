package team.awesome.quickbus.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

	/**
	 * Checks if file specified actually exists.
	 * If not, file is created.
	 * @return
	 */
	public static void validateFileExists(File file){
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads a file and returns read file as a list.
	 *
	 * @param filepath
	 * @return
	 */
	public static ArrayList<String> readFile(String filepath){
		File file = new File(filepath);

		validateFileExists(file);

		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;


			while((line = br.readLine())!=null){
				list.add(line);
			}

			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * Reads a file and returns read file as a list.
	 *
	 * @param file
	 * @return
	 */
	public static ArrayList<String> readFile(File file){
		validateFileExists(file);

		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;


			while((line = br.readLine())!=null){
				list.add(line);
			}

			br.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void appendToFile(ArrayList<String> list, File file){
		ArrayList<String> original = readFile(file); // file exists

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			for(String s: original){
				bw.write(s+"\n");
			}

			for(String s: list){
				bw.write(s+"\n");
			}

			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Append to "+file.getName()+" complete");
	}

	public static void appendToFile(ArrayList<String> list, String filepath){
		File file = new File(filepath);

		ArrayList<String> original = readFile(file); // file exists

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			for(String s: original){
				bw.write(s+"\n");
			}

			for(String s: list){
				bw.write(s+"\n");
			}

			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Append to "+file.getName()+" complete");
	}
}
