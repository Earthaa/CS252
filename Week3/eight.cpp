#include <iostream>
#include <vector>
#include <map>
#include <algorithm>
#include <fstream>
#include <string>
#include <functional>
#include <unordered_map>
using namespace std;
/* 
In this assignment, I use lambda caluculus provided by c++14 standard to realize
all the requirments. 
All "auto xxx "is a lambda function.
Remember, in order to compile the code, you should use the compilers based on
c++14 standard or later.
*/
//no_op return function
auto no_op = [](auto func)->auto
{
    return;
};
//print top 25 elements in given style
auto printAll = [](vector<pair<string, int> >& frequence,auto&& func)
{
    for(int i = 0; i < 25; i++)
    {
        cout<<frequence[i].first<<"  -  "<<frequence[i].second<<endl;
    }
    func(nullptr);
};
//sort words in frequence vector according to their frequencies
auto sortWords =[] (vector<pair<string, int> >& frequence,auto&& func)
{
    sort(frequence.begin(), frequence.end(),
    [](pair<string,int> a, pair<string,int> b) -> bool { return a.second > b.second;});
    func(frequence,no_op);
};
//get frequences of each word in words
auto getFrequency = [](vector<string> &words,auto&& func)
{
     map<string,int> wordFrequency;
     vector<pair<string,int> > frequence;
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
    func(frequence,printAll);
};
//remove stop words from the words
auto remove_stop_words = [](vector<string> &words,auto&& func)
{   
    vector<string> tmp = words;
    string path = "../stop_words.txt";
    unordered_map<string,int> stopWords;
    ifstream myFile(path);
    string line;
    if (myFile.is_open())
        {
            while (getline(myFile,line))
            {   //split by comma
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
            //If a words first appears, then set its count = 1
            for(int i = 0; i < words.size(); i++)
                if(stopWords[words[i]] == 1)
                    words[i] = "";
    }        
        func(words,sortWords);
};
//from data vector scan each word and save them in a vector
auto scan = [](vector<string> &data,auto&& func)
{
    vector<string> words;
    for(auto iter = data.begin(); iter != data.end(); iter++)
    {
         for(int i = 0; i < (*iter).length(); i++)
         {
             //scan each word,if it's not a world(begin with a letter) continue
             if((*iter)[i] >= 'a' && (*iter)[i] <= 'z')
             {                
                 int j = 0;
                 while(j < (*iter).length() && (*iter)[i+j] >= 'a' && (*iter)[i+j] <= 'z')
                        j++;
                 string newWord = (*iter).substr(i,j);
                 //delete some meaningless words
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
    func(words,getFrequency);  
};
//Normalize the original data and then filter chars,change all upper-cases to lower-cases
auto NormalizeAndFilterChars = [](vector<string> &data,auto&& func)
{
    for(auto iter = data.begin(); iter != data.end(); iter++)
    {
        for(int i = 0; i < (*iter).length(); i++)
        {
            //change all upper-cases to lower-cases
            if(((*iter)[i] >= 'A' && (*iter)[i]<= 'Z'))
            {
                (*iter)[i] += 32;
            }
            else if(!(((*iter)[i] >= 'A' && (*iter)[i]<= 'Z')||((*iter)[i] >= 'a' && (*iter)[i]<= 'z')))
            {
                //Normalize
                (*iter)[i] = ' ';
            }
        }
    }
    func(data,remove_stop_words);
};
//read file from the given path, and then save the original data to data
auto readFile = [](string pathToFile, auto&& func)
{
    vector<string> data;
    ifstream myFile(pathToFile);
    string line;
    if(myFile.is_open())
    {
        while(getline(myFile,line))
        {
            data.push_back(line);
        }
    }
    func(data,scan);
};
int main(int argc, const char * argv[]) 
{
    readFile(argv[1],NormalizeAndFilterChars);
}
