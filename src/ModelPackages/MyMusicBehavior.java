/*
 * Class that handles the music behavior of Clippy
 */
package ModelPackages;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.result.Result;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Marzipan
 */
 public class MyMusicBehavior extends MyBehavior {

        private List<String> songList = new ArrayList<String>();
        private Collection files;
        private File selectedFile;

        /**
         * Creates a music behavior
         */
        public MyMusicBehavior() throws IOException
        {
            
            songList.add("pause");
            songList.add("play next song");
            songList.add("volume up");
            songList.add("volume down");
            File playList = new File("./src/PersistantData/playlist.txt");
            FileWriter write = new FileWriter(playList);
            System.out.println("C:\\Users\\" + System.getProperty("user.name") + "\\Music\\");
            File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Music\\");
            String[] extensions =
            {
                "mp3", "wma", "aac"
            };
            files = FileUtils.listFiles(file, extensions, true);
            for (Iterator iterator = files.iterator(); iterator.hasNext();)
            {
                File file1 = (File) iterator.next();
                String fileName = file1.getName().substring(0, file1.getName().indexOf('.'));
                fileName = fileName.replaceAll("[^A-Za-z&&[^']]", " ");
                write.write(fileName);  
                songList.add(fileName);
                System.out.println("File = " + file1.getName().substring(0, file1.getName().indexOf('.')));
            }
            write.close();
        }

        /**
         * Executed when we enter this node. Displays the active grammar
         *
         * @throws JSGFGrammarException
         * @throws JSGFGrammarParseException
         */
        public void onEntry() throws IOException, JSGFGrammarParseException, JSGFGrammarException
        {
            super.onEntry();

            BaseRecognizer recognizer = new BaseRecognizer(getGrammar().getGrammarManager());
            try
            {
                recognizer.allocate();
            }
            catch (Exception e)
            {
            }

            RuleGrammar ruleGrammar = new BaseRuleGrammar(recognizer, getGrammar().getRuleGrammar());

            // now lets add a rule for each song in the play list

            String ruleName = "song";
            int count = 1;
            try
            {
                for (String song : songList)
                {
                    String newRuleName = ruleName + count;
                    Rule newRule = null;
                    if (song.equalsIgnoreCase("pause") || song.equalsIgnoreCase("volume up") || song.equalsIgnoreCase("volume down"))
                    {
                        newRule = ruleGrammar.ruleForJSGF(song
                                + " { " + newRuleName + " }");
                    }
                    else
                    {
                        if (song.equalsIgnoreCase("play next song"))
                        {
                            newRule = ruleGrammar.ruleForJSGF(song
                                    + " { " + newRuleName + " }");
                        }
                        else
                        {
                            newRule = ruleGrammar.ruleForJSGF("play " + song
                                    + " { " + newRuleName + " }");
                        }
                    }
                    ruleGrammar.setRule(newRuleName, newRule, true);
                    ruleGrammar.setEnabled(newRuleName, true);
                    count++;
                }
            }
            catch (GrammarException ge)
            {
                System.out.println("Trouble with the grammar " + ge);
                throw new IOException("Can't add rules for playlist " + ge);
            }
            // now lets commit the changes
            getGrammar().commitChanges();
            grammarChanged();
            help();
        }

        @Override
        public String onRecognize(Result result) throws GrammarException
        {
            String next = super.onRecognize(result);;
            trace("Recognize result: " + result.getBestFinalResultNoFiller());
            String listen = result.getBestFinalResultNoFiller();
            System.out.println(listen);
            if (listen.equalsIgnoreCase("main menu") || listen.equalsIgnoreCase("menu"))
            {
                next = "menu";
            }
            else
            {
                if (listen.startsWith("play") && !listen.equalsIgnoreCase("play next song"))
                {
                    String substring = listen.substring(5);
                    System.out.println(substring);
                    selectedFile = null;
                    for (Iterator iterator = files.iterator(); iterator.hasNext();)
                    {
                        File file1 = (File) iterator.next();
                        String fileName = file1.getName().substring(0, file1.getName().indexOf('.')).replaceAll("[^A-Za-z&&[^']]", " ").replaceAll("\\s+", " ").trim();
                        if (fileName.equalsIgnoreCase(substring))
                        {
                            System.out.println("Name of found file: " + file1.getName());
                            selectedFile = file1;
                            
                        }
                    }
                    try
                    {
                        System.out.println("File Path : " + selectedFile.getAbsoluteFile() + "\\");
                        Desktop.getDesktop().open(new File(selectedFile.getAbsoluteFile() + "\\"));
                    }
                    catch (Exception ex)
                    {
                        System.out.println(ex.getMessage() + "\n Will now try to open using windows media player executeable in program files x86");
                        try
                        {
                            Process process = new ProcessBuilder("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
                        }
                        catch (IOException ex1)
                        {
                            System.out.println("Didn't work, Trying one last option");
                            try
                            {
                                Process process = new ProcessBuilder("C:\\Program Files\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
                            }
                            catch (IOException ex2)
                            {
                                System.out.println("Sorry cannot open music files");
                            }
                        }

                    }
                    next = "";
                }
                else
                {
                    if (listen.equalsIgnoreCase("pause"))
                    {
                        try
                        {
                            Process process = new ProcessBuilder("./Windows Control/UniversalMediaRemote.exe", "Play").start();
                        }
                        catch (IOException ex)
                        {
                            System.out.println("Couldn't find Media Control Program in root/windows control/");
                        }
                    }
                    else
                    {
                        if (listen.equalsIgnoreCase("volume up"))
                        {
                            try
                            {
                                for (int i = 0; i < 10; i++)
                                {
                                    Process process = new ProcessBuilder("./Windows Control/UniversalMediaRemote.exe", "VUp").start();
                                }

                            }
                            catch (IOException ex)
                            {
                            }
                        }
                        else
                        {
                            if (listen.equalsIgnoreCase("volume down"))
                            {
                                try
                                {
                                    for (int i = 0; i < 10; i++)
                                    {
                                        Process process = new ProcessBuilder("./Windows Control/UniversalMediaRemote.exe", "VDown").start();
                                    }

                                }
                                catch (IOException ex)
                                {
                                }
                            }
                            else if (listen.equalsIgnoreCase("play next song"))
                                {
                                    System.out.println("playing next song");
                                    ArrayList<File> list = new ArrayList(files);
                                    System.out.println(list.size());
                                    selectedFile = list.get((int)(Math.random()*list.size()));                      
                                        try
                                        {
                                            try
                                            {
                                                System.out.println("File Path : " + selectedFile.getAbsoluteFile() + "\\");
                                                Desktop.getDesktop().open(new File(selectedFile.getAbsoluteFile() + "\\"));
                                            }
                                            catch (Exception ex)
                                            {
                                                System.out.println(ex.getMessage() + "\n Will now try to open using windows media player executeable in program files x86");
                                                try
                                                {

                                                    Process process = new ProcessBuilder("C:\\Program Files (x86)\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
                                                }
                                                catch (IOException ex1)
                                                {
                                                    System.out.println("Didn't work :( Trying one last option");
                                                    try
                                                    {
                                                        Process process = new ProcessBuilder("C:\\Program Files\\Windows Media Player\\wmplayer.exe", selectedFile.getAbsolutePath()).start();
                                                    }
                                                    catch (IOException ex2)
                                                    {
                                                        System.out.println("Sorry cannot open music files");
                                                    }
                                                }

                                            }
                                        }
                                        catch (Exception ex)
                                        {
                                        }

                                    }
                                }
                            }
                        }
                    }
                
            
            return next;
        }
 }
