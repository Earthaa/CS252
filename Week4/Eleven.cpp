#include<iostream>
#include<vector>
#include<unordered_map>
#include<map>
#include<algorithm>
#include<fstream>
#include<string>
using namespace std;
class DataStorageManager
{   public:
        auto dispatch(vector<string> message)
        {
            /*In order to avoid return void, if this function does not need a return value
              it will return an empty vector. So do the other dispatch functions in following
              classes.
            */
            vector<string> words;
            if(message[0] ==  "init")
                this->init(message[1]);
            else if(message[0] == "words")
                return this->words();
            else
                throw ("Message not understood");
            return words;
        }
    private:
        vector<string> data;
        void init(string path_to_file)
        {
            //Read file and then lowerize each word
            ifstream myFile(path_to_file);
            string line;
            if(myFile.is_open())
                while(getline(myFile,line))
                    data.push_back(line);
            myFile.close();
            for(auto iter = data.begin(); iter != data.end(); iter++)
                for(int i = 0; i < (*iter).length(); i++)
                    if(((*iter)[i] >= 'A' && (*iter)[i]<= 'Z'))
                        (*iter)[i] += 32;
                    else if(!(((*iter)[i] >= 'A' && (*iter)[i]<= 'Z')||((*iter)[i] >= 'a' && (*iter)[i]<= 'z')))
                        (*iter)[i] = ' ';
        }
        vector<string> words()
        {
            //Extract each word from the originial 'words' which are acturally string lines
            vector<string> words;
            for(auto iter = data.begin(); iter != data.end(); iter++)
                for(int i = 0; i < (*iter).length(); i++)
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
            return words;
        }
};

class StopWordsManager
{   public:
        auto dispatch(vector<string> message)
        {
            if(message[0] == "init")
                this->init();
            else if(message[0] == "is_stop_word")
                return this->isStopWord(message[1]);
            else
                throw ("Message not understood");
            return true;
        }
    private:
        unordered_map<string, int> stopWords;
        void init()
        {   
            string stopWordsFile = "../stop_words.txt";
            ifstream myFile(stopWordsFile);
            string line;
            if (myFile.is_open())
            {
                getline(myFile,line); 
                myFile.close();
                for(int i = 0; i < line.size(); i++)
                {
                    int j = 0;
                    while (line[i + j] != ',' && i + j < line.size())
                        j++;
                    stopWords.insert(make_pair<string,int>(line.substr(i,j),1));
                    i = i + j;
                }
            }
        }
        bool isStopWord(string word)
        {
            return stopWords.find(word) != stopWords.end();
        }
};
class WordFrequencyManager
{
    public:
        auto dispatch(vector<string> message)
        {
            vector<pair<string,int> > test;
            if(message[0] == "increment_count")
                this->incrementCount(message[1]);
            else if(message[0] == "sorted")
                return this->sorted();
            else
                throw ("Message not understood");
            return test;
        }
    private:
        map<string,int> wordFrequency;
        vector<pair<string,int> > frequence;
        void incrementCount(string word)
        {
            if(word == "")
                return;
            if(wordFrequency.find(word)!=wordFrequency.end())
                wordFrequency[word] ++;
            else
                wordFrequency[word] = 1;
        }
        vector<pair<string,int> > sorted()
        {
             for(auto it = wordFrequency.begin(); it != wordFrequency.end(); it++)
                frequence.push_back(*it);
             sort(frequence.begin(), frequence.end(),[](pair<string,int> a, pair<string,int> b) -> bool { return a.second > b.second;});
             return frequence;
        }
};

class WordFrequencyController
{   
    public:
        void dispatch(vector<string> message)
        {
            if(message[0] == "init")
                this->init(message[1]);
            else if(message[0] == "run")
                this->run();
            else
                throw ("Message not understood");
        }
    private:
        DataStorageManager* _storage_manager;
        StopWordsManager* _stop_word_manager;
        WordFrequencyManager* _word_freq_manager;
        void init(string path_to_file)
        {
            _storage_manager = new DataStorageManager();
            _stop_word_manager = new StopWordsManager();
            _word_freq_manager = new WordFrequencyManager();
            this->_storage_manager->dispatch({"init",path_to_file});
            this->_stop_word_manager->dispatch({"init"});
        }
        void run()
        {
            vector<string> words = this->_storage_manager->dispatch({"words"});
            for(int i = 0; i < words.size(); i++)
                if(!this->_stop_word_manager->dispatch({"is_stop_word",words[i]}))
                    this->_word_freq_manager->dispatch({"increment_count",words[i]});
            vector<pair<string,int> > word_freqs = this->_word_freq_manager->dispatch({"sorted"});
            for(int i = 0; i < 25; i++)
                cout<<word_freqs[i].first<<"  -  "<<word_freqs[i].second<<endl;
        }
};
int main(int argc, const char * argv[])
{
    WordFrequencyController *wfcontroller = new WordFrequencyController();
    wfcontroller->dispatch({"init",argv[1]});
    wfcontroller->dispatch({"run"});
    return 0;
}