/**
 * Licensed to Open-Ones Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Open-Ones Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package openones.stardictcore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * This class is used to read idx, ifo,dict files get dictionary information, word and meaning.
 * @author kien
 * @author Thach Le
 * 
 * Small modifications to make it work on Android
 * @author Anran Yang
 */
public class StarDict {
    /** number of the nearest word that is displayed. */
    private final int nearest = 10;

    /** Dict directory.(path to the .dict file). */
    private String strURL = null;

    /** decide if object has loaded the entries. */
    private boolean boolAvailable = false;

    /** ifo file. */
    private IfoFile ifoFile = null;

    /** idx file. */
    private IdxFile idxFile = null;

    /** dict file. */
    private DictFile dictFile = null;

    /**
     * Constructor to load dictionary with given path.
     * @param url Path of one of stardict file or Path of folder contains stardict files
     */
    public StarDict(String url) {
        File file = new File(url);

        if (!file.isDirectory()) {
            strURL = getFileNameWithoutExtension(url);
            ifoFile = new IfoFile(strURL + ".ifo");
            idxFile = new IdxFile(strURL + ".idx", ifoFile.getLongWordCount(), ifoFile.getLongIdxFileSize());
            dictFile = new DictFile(strURL + ".dict");
        } else {
            String[] list = file.list();

            strURL = url;
            for (int i = list.length - 1; i >= 0; i--) {
                String extension = getExtension(list[i]);
                String path = url + File.separator + list[i];
                if (extension.equals("ifo")) {
                    ifoFile = new IfoFile(path);
                } else if (extension.equals("idx")) {
                    idxFile = new IdxFile(path, ifoFile.getLongWordCount(), ifoFile.getLongIdxFileSize());
                } else if (extension.equals("dict")) {
                    dictFile = new DictFile(path);
                } else {
                    continue;
                }
            }
        }

        if (ifoFile.isBoolIsLoaded() && idxFile.isLoaded()) {
            boolAvailable = true;
        }
    }

    /**
     * get book name of dictionary.
     * @return Book name
     */
    public String getDictName() {
        return ifoFile.getStrBookname().replace("\r", "").trim();
    }

    /**
     * get book version.
     * @return version of a dictionary
     */
    public String getDictVersion() {
        return ifoFile.getStrVersion();
    }

    /**
     * get amount of words in a StarDict dictionary (within 3 files).
     * @return a long totalWord.
     * @author LongNX
     */
    public int getTotalWords() {
        return getWordEntry().size();
    }

    /**
     * get word content from an idx. let say the stardict-dictd-easton-2.4.2, we give this method the idx 1000 and it
     * return us the "diana".
     * @param idx
     * @return word
     * @author LongNX
     */
    public String getWordByIndex(int idx) {
        String word = getWordEntry().get(idx).getStrLwrWord();
        return word;
    }

    /**
     * lookup a word by its index.
     * @param idx index of a word
     * @return word data
     */
    public String lookupWord(int idx) {
        if (idx < 0 || idx >= idxFile.getLongWordCount()) {
            return "not found";
        }
        WordEntry tempEntry = idxFile.getEntryList().get((int) idx);

        return dictFile.getWordData(tempEntry.getLongOffset(), tempEntry.getLongSize());
    }

    /**
     * lookup a word.
     * @param word that is looked up in database.
     * @return word data
     */
    public String lookupWord(String word) {
        if (!boolAvailable) {
            return "the dictionary is not available";
        }
        int idx = (int) idxFile.findIndexForWord(word);

        return lookupWord(idx);
    }

    /**
     * get a list of word entry.
     * @return list of word entry
     */
    public List<WordEntry> getWordEntry() {
        return idxFile.getEntryList();
    }

    /**
     * load index file and info file.
     */
    public void reLoad() {
        boolAvailable = false;
        ifoFile.reload();
        idxFile.reload();

        if (ifoFile.isBoolIsLoaded() && idxFile.isLoaded()) {
            boolAvailable = true;
        }
    }

    /**
     * get the nearest of the chosen word.
     * @param word that is looked up in database
     * @return a list of nearest word.
     */
    public List<Word> getNearestWords(String word) {
        if (boolAvailable) {
            int idx = (int) idxFile.findIndexForWord(word);
            int nMax = nearest + idx;
            if (nMax > idxFile.getLongWordCount()) {
                nMax = (int) idxFile.getLongWordCount();
            }
            List<Word> wordList = new ArrayList<Word>();
            for (int i = idx; i < nMax; i++) {
                if (i != 0) {
                    Word tempWord = new Word();
                    tempWord.setStrWord(idxFile.getEntryList().get(i).getStrWord());
                    tempWord.setIndex(i);
                    wordList.add(tempWord);
                }
            }
            return wordList;
        }
        return null;
    }

    /**
     * check if a word is in dictionary.
     * @param word that is looked up in database
     * @return true if exists, false otherwise
     */
    public boolean existWord(String word) {
        int wordIndex = (int) idxFile.findIndexForWord(word);

        if (wordIndex >= idxFile.getLongWordCount()) {
            return false;
        }

        String lwrWord = word.toLowerCase();
        if (lwrWord.equals(idxFile.getEntryList().get(wordIndex).getStrLwrWord())) {
            return true;
        }

        return false;
    }
    /**
     * Add list of word to idx, dict file, modify size .ifo file.
     * @param pWord word that is added
     * @param pMean word mean
     * @return true if success
     */
    public boolean addListOfWords(String[] pWord, String[] pMean) {
        if (pWord.length != pMean.length || pWord.length == 0) {
            return false;
        }
        try {
            for (int i = 0; i < pWord.length; i++) {
                String strLwrWord = pWord[i].toLowerCase();
                int pos = (int) idxFile.findIndexForWord(strLwrWord);
                boolean bExist = false;
                if (pos < (int) idxFile.getLongWordCount()) {
                    if (strLwrWord.compareTo(((WordEntry) idxFile.getEntryList().get(pos)).getStrLwrWord()) == 0) {
                        bExist = true;
                    }
                }
                long nextOffset = dictFile.addData(pMean[i]);
                if (nextOffset >= 0) {
                    if (!bExist) {
                        idxFile.addEntry(pWord[i], nextOffset, pMean[i].length(), pos);
                    } else {
                        WordEntry tempEntry = idxFile.getEntryList().get(pos);
                        tempEntry.setLongOffset(nextOffset);
                        tempEntry.setLongSize(pMean[i].length());
                    }
                }
            }
            idxFile.write();
            ifoFile.setLongIdxFileSize(idxFile.getLongIdxFileSize());
            ifoFile.setLongWordCount(idxFile.getLongWordCount());
            ifoFile.write();
        } catch (Exception ex) {
        	Log.e("Stardict", "add words error", ex);
            return false;
        }
        return true;
    }

    /**
     * Add a word to .dict file and .idx file, modify the size of ifo file.
     * @param word word that is needed to add.
     * @param mean word meaning.
     * @return true if add complete.
     */
    public boolean addOneWord(String word, String mean) {
        String[] pWord = new String[1];
        String[] pMean = new String[1];
        pWord[0] = word;
        pMean[0] = mean;

        return addListOfWords(pWord, pMean);
    }

    /**
     * Get file name without extension. For example: input: a:\b.a - output: a:\b
     * @param url path of a file
     * @return original file name
     */
    public String getFileNameWithoutExtension(String url) {
        int dot = url.lastIndexOf(".");
        return url.substring(0, dot);
    }

    /**
     * get extension of file.
     * @param url path to file
     * @return extension of file
     */
    public String getExtension(String url) {
        int dot = url.lastIndexOf(".");
        return url.substring(dot + 1);
    }
}
