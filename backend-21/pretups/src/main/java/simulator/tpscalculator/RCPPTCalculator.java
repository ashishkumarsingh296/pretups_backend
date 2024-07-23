package simulator.tpscalculator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
public class RCPPTCalculator {
        public static void main(String[] args){
                BufferedReader inFile = null;
                try{
                        long count =0;
                        long successcount=0;
                        long tttotal=0;
                        long invaltttotal=0;
                        long intoptttotal=0;
                        long othertttotal=0;
						long maxinval=0;
						long maxintop=0;
						long maxtt=0;
						long maxothertt=0;
						long mininval=99999;
						long minintop=99999;
						long mintt=99999;
						long minothertt=99999;
						long ttGreaterThan200=0;
						long ttLessThan200=0;
						

                        String line = null;
						System.out.println("Syntax: javac -classpath . TT.java");
						System.out.println("Syntax: java -classpath . TT LogPath FileNamePrefix");
                        String folderName = args[0];
                        if(!new File(folderName).exists()){
                                System.out.println("Path not found...");
                                System.exit(0);
                        }
                        String[] child = new File(folderName).list();
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
                                                String key="STV:RC";
                                                int kwstartindex = line.indexOf(key);

                                                //Status
                                                String key1="[TS:";
                                                int statusstartindex = line.indexOf(key1);
                                                int statusendindex = line.indexOf("]", statusstartindex);
                                                String status = line.substring(statusstartindex+key1.length(), statusendindex).trim();

                                                //TT
                                                String key2="[RTT:";
                                                int ttstartindex = line.indexOf(key2);
                                                int ttendindex = line.indexOf("]", ttstartindex);
                                                String tt = line.substring(ttstartindex+key2.length(), ttendindex).trim();

                                                //INVALTT
                                                String key3="[VAL:";
                                                int invalttstartindex = line.indexOf(key3);
                                                int invalttendindex = line.indexOf("]", invalttstartindex);
                                                String invaltt = line.substring(invalttstartindex+key3.length(), invalttendindex).trim();

                                                //INTOPTT
                                                String key4="[TOP:";
                                                int intopttstartindex = line.indexOf(key4);
                                                int intopttendindex = line.indexOf("]", intopttstartindex);
                                                String intoptt = line.substring(intopttstartindex+key4.length(), intopttendindex).trim();

                                                //OTHERTT
                                                String key5="[PPT:";
                                                int otherttstartindex = line.indexOf(key5);
                                                int otherttendindex = line.indexOf("]", otherttstartindex);
                                                String othertt = line.substring(otherttstartindex+key5.length(), otherttendindex).trim();

                                                if(kwstartindex != -1 && status.equals("200") && Long.parseLong(tt) < 240000 ){
                                                        tttotal += Long.parseLong(tt);
                                                        invaltttotal += Long.parseLong(invaltt);
                                                        intoptttotal += Long.parseLong(intoptt);
                                                        othertttotal += Long.parseLong(othertt);
                                                        successcount++;
														if(maxinval < Long.parseLong(invaltt)) maxinval=Long.parseLong(invaltt);
														if(maxintop < Long.parseLong(intoptt)) maxintop=Long.parseLong(intoptt);
														if(maxtt < Long.parseLong(tt)) maxtt=Long.parseLong(tt);
														if(maxothertt < Long.parseLong(othertt)) maxothertt=Long.parseLong(othertt);

														if(mininval > Long.parseLong(invaltt)) mininval=Long.parseLong(invaltt);
														if(minintop > Long.parseLong(intoptt)) minintop=Long.parseLong(intoptt);
														if(mintt > Long.parseLong(tt)) mintt=Long.parseLong(tt);
														if(minothertt > Long.parseLong(othertt)) minothertt=Long.parseLong(othertt);
														//added by Ashish
														if(Long.parseLong(tt) < 200){
															ttLessThan200++;
														}else{
															ttGreaterThan200++;
														}
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
                        System.out.println("Total Row Count: "+ count);
                        System.out.println("Success Count: "+ successcount);
                        
			System.out.println("Avg TotalTime: "+ tttotal/successcount);
                        System.out.println("Avg INVALTT: "+ invaltttotal/successcount);
                        System.out.println("Avg INTOPTT: "+ intoptttotal/successcount);
                        System.out.println("Avg PretupsTime: "+ othertttotal/successcount);
						
						//System.out.println("Max TT: "+ maxtt);
                      //  System.out.println("Max INVALTT: "+ maxinval);
                       // System.out.println("Max INTOPTT: "+ maxintop);
			//System.out.println("Max OTHERTT: "+ maxothertt);
						
			//System.out.println("Min TT: "+ mintt);
                        //System.out.println("Min INVALTT: "+ mininval);
                        //System.out.println("Min INTOPTT: "+ minintop);
			//System.out.println("Min OTHERTT: "+ minothertt);
			//System.out.println("TT Less Than 200: "+ ttLessThan200);
		       //System.out.println("TT Greater Than 200: "+ ttGreaterThan200);
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

