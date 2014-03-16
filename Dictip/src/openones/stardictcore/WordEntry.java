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

/**
 * This class is used to store entries in .idx file.
 * @author kien
 */
public class WordEntry {

    /** lower case of str_word. */
    private String strLwrWord;

    /** Word. */
    private String strWord;

    /** position of meaning of this word in ".dict" file. */
    private long longOffset;

    /** length of the meaning of this word in ".dict" file. */
    private long longSize;

    /**
     * Set the value for longSize.
     * @param longSize the longSize to set
     */
    public void setLongSize(long longSize) {
        this.longSize = longSize;
    }

    /**
     * Get value of longSize.
     * @return the longSize
     */
    public long getLongSize() {
        return longSize;
    }

    /**
     * Set the value for longOffset.
     * @param longOffset the longOffset to set
     */
    public void setLongOffset(long longOffset) {
        this.longOffset = longOffset;
    }

    /**
     * Get value of longOffset.
     * @return the longOffset
     */
    public long getLongOffset() {
        return longOffset;
    }

    /**
     * Set the value for strWord.
     * @param strWord the strWord to set
     */
    public void setStrWord(String strWord) {
        this.strWord = strWord;
    }

    /**
     * Get value of strWord.
     * @return the strWord
     */
    public String getStrWord() {
        return strWord;
    }

    /**
     * Set the value for strLwrWord.
     * @param strLwrWord the strLwrWord to set
     */
    public void setStrLwrWord(String strLwrWord) {
        this.strLwrWord = strLwrWord;
    }

    /**
     * Get value of strLwrWord.
     * @return the strLwrWord
     */
    public String getStrLwrWord() {
        return strLwrWord;
    }
}
