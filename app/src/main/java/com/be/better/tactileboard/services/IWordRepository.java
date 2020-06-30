package com.be.better.tactileboard.services;

import java.util.Collection;

public interface IWordRepository {
    public boolean addWord(String word, String pattern);
    public String getWord(String pattern);
    public String getPattern(String word);
    public boolean wordExists(String word);
    public boolean patternExists(String pattern);
    public Collection<String> getAllWords();
    public void clear();
}
