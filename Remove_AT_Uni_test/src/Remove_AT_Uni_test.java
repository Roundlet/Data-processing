import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.html.parser.Entity;

public class Remove_AT_Uni_test {
	public static void main(String[] args) throws Exception {
		try {
			/*Read compare data*/
			FileInputStream process_cmp = new FileInputStream("C:\\Users\\compare_data.txt");
			InputStreamReader isr_cmp = new InputStreamReader(process_cmp,"utf-8");
			BufferedReader br_cmp = new BufferedReader(isr_cmp);
			String readline_cmp = ""; 
			/*Set up the cmp_map*/
			/*<Test Id, Readline>*/
			Map<Integer,String> cmp_map = new HashMap<Integer,String>();
			while((readline_cmp = br_cmp.readLine())!=null) {
				String[] array = readline_cmp.split(",");
				int id = Integer.parseInt(array[0]);
				if(!cmp_map.containsKey(id)) {
					cmp_map.put(id, readline_cmp);
				}
			}
			
			br_cmp.close();
			isr_cmp.close();
			process_cmp.close();
			
			/*Write the contents to file*/
	       		File file_w = new File("C:\\Users\\write_data.txt");
			FileOutputStream fos  = new FileOutputStream(file_w);
			OutputStreamWriter os = new OutputStreamWriter(fos,"utf-8");
			BufferedWriter writer = new BufferedWriter(os);
			
			/*Open a write file*/
			FileInputStream process_file = new FileInputStream(new File("C:\\Users\\master_data.txt"));
			InputStreamReader isr = new InputStreamReader(process_file,"utf-8");
			BufferedReader br = new BufferedReader(isr);
			String readline_master = "";
			br.readLine();//skip the attributes description
			readline_master = br.readLine();//first line

			
			/*Read master file*/
			while(readline_master!=null) {
				
				String pre_key = "";
				
				
				//System.out.println(readline_master);
				/*Read the first element in one group*/
				String[] attr = readline_master.split(",");
				//System.out.println(attr[2]);
				
				
				/*Get the group key*/
				for(int i=0;i<18;i++) {
					pre_key = pre_key + attr[i]+",";
				}
				//System.out.println(pre_key);
				
				/*Skip all TestProcess files*/
				if(attr[2].contains("TestProcess")) {
					readline_master = br.readLine();
					continue;
				}
				
				
				/*Find the first row of the data we need to modified*/
				else if(attr[2].contains("UnitTest")||attr[2].substring(0, 2).equals("AT")) {
					/*Find the directory in master file for this group*/
					int index = attr[2].lastIndexOf("\\");
					String dir_master = attr[2].substring(0, index);
					//System.out.println("This is the check for match map dir_master "+dir_master);
					/*Take the whole line as the key
					 *For each group we create a Map */
					/*<readline_master, Test id>*/
					Map<String,Integer> master_map = new HashMap<String,Integer>();
					
					
					
					/*attr[22] store the code we need to match in compare file.
					 * Put the first element in map*/
					master_map.put(readline_master, Integer.parseInt(attr[22]));//store the first line in group
					//System.out.println(attr[22]);
					
					/*The bound condition is that there are only one member in the group*/
					/*Loop in one group.
					 *Loop will stop at the first element in the next group*/
					while((readline_master = br.readLine())!=null) {
						String cur_key = "";
						attr = readline_master.split(",");
						/*Get the current key*/
						for(int i=0;i<18;i++) {
							cur_key = cur_key + attr[i]+",";
						}
						/*Using keys to identify whether they belong to the
						 * same group*/
						//System.out.println("pre_key: "+pre_key);
						//System.out.println("cur_key: "+cur_key);
						if(!cur_key.equals(pre_key)) {
							break;
						}
						master_map.put(readline_master, Integer.parseInt(attr[22]));	
					}
					
					
					/*Set up the map that contains all the compare code (Test ID) that match the
					 * dir name in master file use the parameter dir_master*/
					Map<String,Integer> match_map = new HashMap<String,Integer>();
					for(Map.Entry<Integer, String> entry :cmp_map.entrySet()) {
						attr = entry.getValue().split(",");
						String dir_cmp = attr[6];
						//System.out.println("This is the check for match map dir_master "+dir_master+" dir_cmp "+dir_cmp);
						if(dir_master.equals(dir_cmp)) {
							//System.out.println("This is the check for match map dir_master "+dir_master+" dir_cmp "+dir_cmp);
							String cmp_line = pre_key+attr[1]+","+","+","+","+entry.getValue();
							//System.out.println("This is the check for match map line "+cmp_line);
							System.out.println(entry.getValue());
							match_map.put(cmp_line, Integer.parseInt(attr[0]));
						}
						
					}
					
					/*Record the code that existing in both master and match map*/
					Map<String,Integer> record_map = new HashMap<String,Integer>();
					for(Map.Entry<String,Integer> entry_master : master_map.entrySet()) {
						//System.out.println("entry_master: "+entry_master.getValue());
						for(Map.Entry<String,Integer> entry_match : match_map.entrySet()) {
							//System.out.println("entry_match: "+entry_match.getValue());
							if(entry_master.getValue().equals(entry_match.getValue() )) {
								System.out.println("= "+entry_master.getValue());
								record_map.put(entry_master.getKey(),entry_master.getValue());
								writer.write(entry_master.getKey());
								writer.newLine();
							}
						}
					}
					
					/*Find the element we need to insert*/
					if(record_map.isEmpty()) {
						for(Map.Entry<String,Integer> entry_match : match_map.entrySet()) {
							//System.out.println(entry_match.getValue());
							writer.write(entry_match.getKey());
							writer.newLine();
						}
					}
					else {
						for(Map.Entry<String,Integer> entry_match : match_map.entrySet()) {
							for(Map.Entry<String,Integer> entry_record : record_map.entrySet()) {
								if(!entry_match.getValue().equals(entry_record.getValue()) ) {
									System.out.println(entry_match.getValue());
									writer.write(entry_match.getKey());
									writer.newLine();
								}
							}
						}
					}
					
				}
				else {
					writer.write(readline_master);
					writer.newLine();
					readline_master = br.readLine();
				}
				
			}
			writer.close();
			os.close();
			fos.close();
			br.close();
			isr.close();
			process_file.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
