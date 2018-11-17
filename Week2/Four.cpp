#include <iostream>
#include <vector>
#include <map>
#include <algorithm>
#include <fstream>
#include <string>
#include <unordered_map>
using namespace std;
void readFile(string pathToFile,vector<string>& data)
{
    ifstream myFile(pathToFile);
    string line;
    if(myFile.is_open())
    {
        while(getline(myFile,line))
        {
            data.push_back(line);
        }
    }
}
void NormalizeAndFilterChars(vector<string>& data)
{
    for(auto iter = data.begin(); iter != data.end(); iter++)
    {
        for(int i = 0; i < (*iter).length(); i++)
        {
            if(((*iter)[i] >= 'A' && (*iter)[i]<= 'Z'))
            {
                (*iter)[i] += 32;
            }
            else if(!(((*iter)[i] >= 'A' && (*iter)[i]<= 'Z')||((*iter)[i] >= 'a' && (*iter)[i]<= 'z')))
            {
                (*iter)[i] = ' ';
            }
        }
    }
}

void scan(vector<string>& data, vector<string>& words)
{
    for(auto iter = data.begin(); iter != data.end(); iter++)
    {
         for(int i = 0; i < (*iter).length(); i++)
         {
             if((*iter)[i] >= 'a' && (*iter)[i] <= 'z')
             {
                 int j = 0;
                 while(j < (*iter).length() && (*iter)[i+j] >= 'a' && (*iter)[i+j] <= 'z')
                        j++;
                 string newWord = (*iter).substr(i,j);
                 if(newWord == "s" || newWord == "re")
                    {
                        i = i + j;
                        continue;
                    }
                words.push_back(newWord);
                i = i + j;
             }
         }
    }
}
void remove_stop_words(vector<string>&words)
{
    unordered_map<string,int> stopWords;
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
        for(int i = 0; i < words.size(); i++)
            if(stopWords[words[i]] == 1)
                words[i] = "";
}

void getFrequency(const vector<string>& words, map<string,int>& wordFrequency,vector<pair<string,int> >& frequence)
{
    for(int i = 0; i < words.size(); i++)
    {
        if(words[i] == "")
            continue;
        if(wordFrequency.find(words[i])!=wordFrequency.end())
            wordFrequency[words[i]] ++;
        else
            wordFrequency[words[i]] = 1;
    }
    for(auto it = wordFrequency.begin(); it != wordFrequency.end(); it++)
        frequence.push_back(*it);
}

void sortWords(vector<pair<string, int> > &frequence)
{
    sort(frequence.begin(), frequence.end(),[](pair<string,int> a, pair<string,int> b) -> bool { return a.second > b.second;});
}
int main(int argc, const char * argv[]) {
    vector<string> data;
    vector<string> words;
    map<string,int> wordFrequency;
    vector<pair<string,int> > frequency;
    readFile(argv[1], data);
    NormalizeAndFilterChars(data);
    scan(data,words);
    remove_stop_words(words);
    getFrequency(words,wordFrequency,frequency);
    sortWords(frequency);
    for(int i = 0; i < 25; i++)
    {
        cout<<frequency[i].first<<"  -  "<<frequency[i].second<<endl;
    }
    return 0;
}
