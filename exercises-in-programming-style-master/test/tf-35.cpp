//
//  wordfrequency.cpp
//  wordfrequency
//
//  Created by Yi Zhou on 10/2/18.
//  Copyright © 2018 Yi Zhou. All rights reserved.
//

#include <iostream>
#include <vector>
#include <map>
#include <algorithm>
#include <fstream>
#include <string>
#include <unordered_map>
using namespace std;
void getStopWords(unordered_map<string, int>& stopWords)
{
    string stopWordsFile = "../stop_words.txt";
    ifstream myFile(stopWordsFile);
    string line;
    if (myFile.is_open())
    {
        while (getline(myFile,line))
        {
            for(int i = 0; i < line.size(); i++)
            {
                int j = 0;
                while (line[i + j] != ',' && i + j < line.size()) {
                    j++;
                }
                stopWords[line.substr(i,j)] = 1;
                i = i + j;
            }
        }
        myFile.close();
    }
    else
    {
        cout<<"Cannot find the stop_words.txt!"<<endl;
    }
}
void getFrequences(string fileName,map<string,int>& wordFrequence,const unordered_map<string,int>& stopWords)
{
    ifstream myFile(fileName);
    string word;
    if(myFile.is_open())
    {
        while(myFile>>word)
        {
            for(int i = 0; i < word.size(); i++)
            {
                if((word[i] >= 'a' && word[i] <= 'z') || (word[i] >= 'A' && word[i] <= 'Z'))
                {
                    int j = 0;
                    while (j<word.size() && ((word[i+j] >= 'a' && word[i+j] <= 'z') || (word[i+j] >= 'A' && word[i+j] <= 'Z')))
                    {
                        j++;
                    }
                    string newWord = word.substr(i,j);
                    transform(newWord.begin(), newWord.end(), newWord.begin(), ::tolower);
                    if(newWord == "s" || newWord == "re")
                    {
                        i = i + j;
                        continue;
                    }
                    if(stopWords.find(newWord)==stopWords.end())
                    {
                        wordFrequence[newWord] += 1;
                    }
                    i = i + j;
                }
            }
        }
    }
    myFile.close();
}
bool sortByFrequence(const pair<string,int> &lhs, const pair<string, int> &rhs)
{
    return lhs.second > rhs.second;
}
int main(int argc, const char * argv[]) {
//    if(argc < 2)
//    {
//        cout<<"A textfile is needed,please try again"<<endl;
//        return -1;
//    }
    unordered_map<string, int> stopWords;
    map<string,int> wordFrequence;
    vector<pair<string, int> > frequence;
    getStopWords(stopWords);
    getFrequences(argv[1], wordFrequence, stopWords);
    for(auto it = wordFrequence.begin(); it != wordFrequence.end(); it++)
    {
        frequence.push_back(*it);
    }
    sort(frequence.begin(), frequence.end(),sortByFrequence);
    int count = 1;
    for(auto it = frequence.begin(); it != frequence.end(); it++)
    {
        if (count > 25) {
            break;
        }
        cout<<it->first<<"  -"<<it->second<<endl;
        count++;
    }
    return 0;
}
