package simulator.tpscalculator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
public class OtherServicePPTCalculator {
        public static void main(String[] args){
                BufferedReader inFile = null;
                try{
                	long count =0;
                	long successcount=0;
                	long othertttotal=0;
                	long maxothertt=0;
                	long minothertt=99999;


                	String line = null;
                	System.out.println("Syntax: javac -classpath . TT1.java");
                	System.out.println("Syntax: java -classpath . TT LogPath FileNamePrefix");
                	String serviceType = null;
                	String serviceList[] = args[2].split(":");
                	String folderName = args[0];
                	if(!new File(folderName).exists()){
                		System.out.println("Path not found...");
                		System.exit(0);
                	}
                	String[] child = new File(folderName).list();
                	for (int m=0; m<serviceList.length; m++)
                	{
                		serviceType=serviceList[m];
            			successcount=0;
                    	othertttotal=0;
                    	maxothertt=0;
                		for (int i=0; i<child.length; i++)
                		{
                			String filePath = folderName +"/"+ child[i];

                			if(new File(filePath).isFile() && (child[i].startsWith(args[1])))
                			{
                				System.out.println(filePath);
                				File srcFile = new File(filePath);
                				inFile = new BufferedReader(new FileReader(srcFile));
                				while(inFile.ready())
                				{
                					try{
                						line = inFile.readLine();

                						//Keyword
                						String key="STV:"+serviceType;
                						int kwstartindex = line.indexOf(key);

                						//TT
                						String key5="[TT:";
                						int otherttstartindex = line.indexOf(key5);
                						int otherttendindex = line.indexOf(" ms]", otherttstartindex);
                						String othertt = line.substring(otherttstartindex+key5.length(), otherttendindex).trim();

                						if(kwstartindex != -1 ){
                							othertttotal += Long.parseLong(othertt);
                							successcount++;
                							if(maxothertt < Long.parseLong(othertt)) maxothertt=Long.parseLong(othertt);
                							if(minothertt > Long.parseLong(othertt)) minothertt=Long.parseLong(othertt);
                						}
                						count++;
                					}
                					catch(Exception e){
                						//System.out.println(count+": line: "+line);
                					}
                					continue;
                				}//end of while - single file
                				if(inFile!=null){
                					inFile.close();
                					inFile=null;
                				}
                			}//end of if - file name start
                		}//end of for - all files
                		if(successcount >0){
                			System.out.println(serviceType + " Total Request Count : "+ successcount);
                			System.out.println(serviceType + " Success Count : "+ successcount);
                			System.out.println(serviceType + " Avg PretupsTime : "+ othertttotal/successcount);
                		}

                	}
                }
                catch(Exception e){
                        System.out.println("exception: "+e);
                        e.printStackTrace();
                }
                finally{
                        if(inFile!=null){
                                try{inFile.close();}catch(Exception e){}
                                inFile=null;
                        }
                }
        }
}

