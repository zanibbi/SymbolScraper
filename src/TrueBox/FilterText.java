/******************************************************************************
* FilterText.java
*
* Copyright (c) 2018, 2019
* Ritvik Joshi, Parag Mali, Puneeth Kukkadapu, Mahshad Mahdavi, and 
* Richard Zanibbi
*
* Document and Pattern Recognition Laboratory
* Rochester Institute of Technology, USA
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/

package TrueBox;



import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.zip.CheckedOutputStream;

class FilterText {

    ArrayList<PageStructure> allPages;

    FilterText(ArrayList<PageStructure> allPages){

        this.allPages=allPages;
    }

    HashMap<String, Integer> wordDictionary;

    FilterText(HashMap<String, Integer> wordDictionary){
        this.wordDictionary=wordDictionary;
    }

    public void isMath() throws IOException, InterruptedException {

        HashMap<String,ArrayList<Words>> wordList = createWordMap();
        System.out.println(wordList.size());
        StringBuilder pythonCommand = new StringBuilder();

        pythonCommand.append("python");
        pythonCommand.append(" C:\\Users\\ritvi\\PycharmProjects\\CapstoneProject\\MathScrapper\\NLProcessing.py");

        Iterator iter = wordList.entrySet().iterator();
        int counter=0;
        while(iter.hasNext()){
            Map.Entry pair = (Map.Entry) iter.next();

            String word = (String) pair.getKey();
            pythonCommand.append(" "+word);
            if(counter>200){
                break;
            }
            counter++;

        }


        String cmdString = pythonCommand.toString();
        System.out.println(cmdString);
        String cmd[] = cmdString.split("\\s+");
        Process p = Runtime.getRuntime().exec(cmd);

        while(p.isAlive()){
            continue;
        }
        System.out.println("Word filtering completed");
        //assignFlag(wordList);
        //System.out.println(p.waitFor());
        //BufferedReader readInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        //System.out.println(readInput.readLine());
        //while ((thisLine = readInput.readLine()) != null) {
        //    System.out.println(thisLine);
        //}
        //Thread.sleep(10000);

        BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\ritvi\\PycharmProjects\\CapstoneProject\\MathScrapper\\WordFilter.txt"));

        String st;
        while ((st = br.readLine()) != null) {
            //System.out.println("Reading file");
            String res[] = st.split("\\s+");
            System.out.println(res[0]+" "+res[1]);
            String wordString = "";
            ArrayList<Words> words = null;
            switch (res[1]) {

                case "True":
                    wordString = res[0];
                   // System.out.println(wordString + " " + res[1]);

                    if (wordList.containsKey(wordString)) {
                        words = wordList.get(wordString);
                        for (Words wrd : words) {
                            wrd.NonMath = true;
                        }
                    }
                    break;
                case "False":
                    wordString = res[0];
                   // System.out.println(wordString + " " + res[1]);
                    if (wordList.containsKey(wordString)) {
                        words = wordList.get(wordString);
                        for (Words wrd : words) {
                            wrd.NonMath = false;
                        }
                    }
                    break;
            }
        }

        //System.out.println(st);


    }


    public void assignFlag(HashMap<String, ArrayList<Words>> wordList) throws IOException {
        System.out.println("Reading flag file");
        boolean flag=true;
        while(flag) {
            Scanner br = new Scanner(new File("C:\\Users\\ritvi\\PycharmProjects\\CapstoneProject\\MathScrapper\\WordFilter.txt"));

            while (br.hasNext()) {
                flag=false;
                //System.out.println("Reading file");
                String res[] = br.nextLine().split("\\s+");
                System.out.println(res[0]+" "+res[1]);
                String wordString = "";
                ArrayList<Words> words = null;
                switch (res[1]) {

                    case "True":
                        wordString = res[0];
                        // System.out.println(wordString + " " + res[1]);

                        if (wordList.containsKey(wordString)) {
                            words = wordList.get(wordString);
                            for (Words wrd : words) {
                                wrd.NonMath = true;
                            }
                        }
                        break;
                    case "False":
                        wordString = res[0];
                        //System.out.println(wordString + " " + res[1]);
                        if (wordList.containsKey(wordString)) {
                            words = wordList.get(wordString);
                            for (Words wrd : words) {
                                wrd.NonMath = false;
                            }
                        }
                        break;
                }
            }
            br.close();
        }
    }



    public HashMap<String, ArrayList<Words>> createWordMap(){
        HashMap<String,ArrayList<Words>> wordList= new HashMap<>();

        for(int i=0;i<allPages.size();i++) {
            PageStructure page = allPages.get(i);
            for (int lineIter = 0; lineIter < page.Lines.size(); lineIter++) {
                Line line = page.Lines.get(lineIter);
                for (int wordIter = 0; wordIter < line.words.size(); wordIter++) {
                    Words word = line.words.get(wordIter);
                    String wordString = word.wordString.toLowerCase();
                    //System.out.println(word.wordString);
                    if(wordList.containsKey(wordString)){
                        ArrayList<Words> tempList = wordList.get(wordString);
                        tempList.add(word);
                    }else{
                        ArrayList<Words> tempList = new ArrayList<>();
                        tempList.add(word);
                        wordList.put(wordString,tempList);
                    }
                }
            }
        }

        return wordList;
    }

    public HashMap<Integer,Words> filter(PageStructure page){

        HashMap<Integer, Words> filteredMap = new HashMap<Integer, Words>();

        ArrayList<Line> allLines = page.Lines;
        int matched=0;
        for(int lineIter=0;lineIter<allLines.size();lineIter++){
            ArrayList<Words> allWords = allLines.get(lineIter).words;
            //System.out.println("TW"+allWords.size());
            for(int wordIter=0;wordIter<allWords.size();wordIter++){
                ArrayList<characterInfo> characters = allWords.get(wordIter).characters;
                String word="";
                for(int charIter=0;charIter<characters.size();charIter++){
                    if(characters.get(charIter).value.equals(",") || characters.get(charIter).value.equals(".") )
                        continue;
                    word+=characters.get(charIter).value;
                }
                //System.out.println(word+" "+matchWord(word));
                if(matchWord(word.trim())){
                    //System.out.println(matched++);
                    matched++;
                    filteredMap.put(matched, allWords.get(wordIter));
                }
            }

        }
        //System.out.println("S:"+filteredMap.size());


        return filteredMap;

    }

    public boolean matchWord(String word){
        return wordDictionary.containsKey(word.toLowerCase());
    }

    public HashMap<Integer,characterInfo> getCharacterList(HashMap<Integer,Words> filtered){
        //System.out.println("FT:"+filtered.size());
        Iterator iter = filtered.entrySet().iterator();
        HashMap<Integer,characterInfo> filteredCharacter = new HashMap<Integer, characterInfo>();
        int counter=0;
        while(iter.hasNext()){
            Map.Entry pair = (Map.Entry) iter.next();
            Words word =(Words) pair.getValue();
            for(characterInfo ch: word.characters){
                counter++;
                //System.out.println(ch.value);
                filteredCharacter.put(counter,ch);
            }
        }

        //System.out.println("FC:"+filteredCharacter.size());
        return filteredCharacter;

    }


}
