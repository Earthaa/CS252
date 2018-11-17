/*The code mirriors the method provided in the book
    Most of the methods are same, so no extra comment if it's not necessary
*/
var WordFrequencyFramework = {
    _load_event_handlers : [],
    _dowork_event_handlers : [],
    _end_event_handlers : [],
    register_for_load_event:function(handler,self = WordFrequencyFramework){
        self._load_event_handlers.push(handler)
    },
    register_for_dowork_event:function(handler,self = WordFrequencyFramework){
       self._dowork_event_handlers.push(handler) 
    },
    rigister_for_end_event:function(handler,self = WordFrequencyFramework){
        self._end_event_handlers.push(handler)
    },
    run:function(path_to_file,self = WordFrequencyFramework){  
        for(h in self._load_event_handlers){
            self._load_event_handlers[h](path_to_file);
        }
        for(h in self._dowork_event_handlers){
            self._dowork_event_handlers[h]();
        }
        for(h in self._end_event_handlers){
            self._end_event_handlers[h]();
        }
    }
}
var DataStorage={
    _data : '',
    _stop_word_filter:null,
    _word_event_handlers:[],
    _init:function(wfapp,stop_word_filter,self = DataStorage)
    {
        self._stop_word_filter = stop_word_filter
        wfapp.register_for_load_event(self.__load);
        wfapp.register_for_dowork_event(self.__produce_words);
    },
    __load:function(path_to_file,self = DataStorage){
        var fs = require('fs');
        var buf = fs.readFileSync(path_to_file).toString("utf-8");
        self._data = buf.replace(/[\W_]+/g," ").toLowerCase();
    },
    __produce_words:function(self = DataStorage){
        arr = self._data.split(" ");
        for(w in arr){
            if(!self._stop_word_filter.is_stop_word(arr[w])){
                for(h in self._word_event_handlers){
                   self._word_event_handlers[h](arr[w]);
                }
            }           
        }
    },
    register_for_word_event:function(handler,self = DataStorage){
        self._word_event_handlers.push(handler);
    }
}
var StopWordFilter = {
    _stop_words :[],
    _init:function(wfapp,self = StopWordFilter)
    {
        wfapp.register_for_load_event(self.__load);
    },
    __load:function(ignore,self = StopWordFilter){
        var fs = require('fs');
        self._stop_words = fs.readFileSync("../stop_words.txt").toString("utf-8").split(',');
        self._stop_words.push('s');
        self._stop_words.push('re');
        self._stop_words.push('t');
    },
    is_stop_word:function(word,self = StopWordFilter){
        return self._stop_words.includes(word);
    }
}
var WordFrequencyCounter = {
    _word_freqs:{},
    _init:function(wfapp,data_storage,self = WordFrequencyCounter){
        data_storage.register_for_word_event(self.__increment_count);
        wfapp.rigister_for_end_event(self.__print_freqs);
    },
    __increment_count:function(word,self = WordFrequencyCounter){
        if(word in self._word_freqs){
            self._word_freqs[word] += 1;
        }
        else{
            self._word_freqs[word] = 1;
        }
    },
    __print_freqs:function(self = WordFrequencyCounter){
        var items = Object.keys(self._word_freqs).map(
            function(key) {
            return [key, self._word_freqs[key]];
          }
          );
          items.sort(function(first, second) {
            return second[1] - first[1];
          });
          for(var i = 0; i < 25; i++)
          {
              console.log(items[i][0]+"  -  "+items[i][1]);
          }
    }
}
//Class to get the number of words with 'z'
var ZFrequency ={
    z_freq:0,//the number of words with letter 'z'
    //handle the method
    _init:function(wfapp,word_storage,self = ZFrequency){
        wfapp.rigister_for_end_event(self.print_z_frequency)
        word_storage.register_for_word_event(self.z_frequency_count)
    },
    z_frequency_count:function(word,self = ZFrequency){
        if(word.includes("z")){
            self.z_freq += 1
        }
    },
    print_z_frequency:function(self = ZFrequency){
        console.log("The count of words with 'z' is:"+self.z_freq)
    }
}

var wfapp = WordFrequencyFramework
var stop_word_filter = StopWordFilter;
var data_storage = DataStorage;
var word_freq_counter = WordFrequencyCounter;
var z_freq_counter = ZFrequency;
stop_word_filter._init(WordFrequencyFramework)
data_storage._init(wfapp,stop_word_filter)
word_freq_counter._init(wfapp,data_storage)
z_freq_counter._init(wfapp,data_storage)
wfapp.run(process.argv[2]);

// StopWordFilter._init(WordFrequencyFramework)
// DataStorage._init(WordFrequencyFramework,StopWordFilter)
// WordFrequencyCounter._init(WordFrequencyFramework,DataStorage)
// WordFrequencyFramework.run("../pride-and-prejudice.txt")