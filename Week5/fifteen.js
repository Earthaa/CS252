var EventManager = {
    _subscriptions:{},
    subscribe:function(event_type,handler,self = EventManager){
        if(event_type in self._subscriptions){
            self._subscriptions[event_type].push(handler)
        }
        else{
            self._subscriptions[event_type] = [handler]
        }
    },
    publish:function(event,self = EventManager){  
        event_type = event[0]
        if (event_type in self._subscriptions){
            for(var h in self._subscriptions[event_type]){
                self._subscriptions[event_type][h](event)               
            }
        }
    }
}
var DataStorage={
    _data : '',
    _event_manager : null,
    _init:function(event_manager,self = DataStorage)
    {
        self._event_manager = event_manager
        self._event_manager.subscribe("load", self.load)
        self._event_manager.subscribe("start", self.produce_words)
    },
    load:function(event,self = DataStorage){
        path_to_file = event[1]
        var fs = require('fs');
        var buf = fs.readFileSync(path_to_file).toString("utf-8");
        self._data = buf.replace(/[\W_]+/g," ").toLowerCase();
    },
    produce_words:function(event,self = DataStorage){
        arr = self._data.split(" ");
        for(w in arr){
            self._event_manager.publish(['word',arr[w]])        
        }
        self._event_manager.publish(['eof',null])
    },
}
var StopWordFilter = {
    _stop_words :[],
    _event_manager : null,
    _init:function(event_manager,self = StopWordFilter)
    {
       self._event_manager = event_manager
       self._event_manager.subscribe("load", self.load)
        self._event_manager.subscribe("word", self.is_stop_word)
    },
    load:function(event,self = StopWordFilter){
        var fs = require('fs');
        self._stop_words = fs.readFileSync("../stop_words.txt").toString("utf-8").split(',');
        self._stop_words.push('s');
        self._stop_words.push('re');
        self._stop_words.push('t');
    },
    is_stop_word:function(event,self = StopWordFilter){
       word = event[1]
       if(!self._stop_words.includes(word)){
           self._event_manager.publish(["valid_word", word])
       }
    }
}
var WordFrequencyCounter = {
    _word_freqs:{},
    _event_manager : null,
    _init:function(event_manager,self = WordFrequencyCounter){
        self._event_manager = event_manager
        self._event_manager.subscribe("valid_word", self.increment_count)
        self._event_manager.subscribe("print", self.print_freqs)
    },
    increment_count:function(event,self = WordFrequencyCounter){
        word = event[1]
        if(word in self._word_freqs){
            self._word_freqs[word] += 1
        }
        else{
            self._word_freqs[word] = 1
        }
    },
    print_freqs:function(event,self = WordFrequencyCounter){
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

var WordFrequencyApplication = {
    event_manager:null,
    _init:function(event_manager,self = WordFrequencyApplication){
        self._event_manager = event_manager
        self._event_manager.subscribe("run", self.run)
        self._event_manager.subscribe("eof", self.stop)
    },
    run:function(event,self = WordFrequencyApplication){
        path_to_file = event[1]
        self._event_manager.publish(["load", path_to_file])
        self._event_manager.publish(["start", null])
    },
    stop:function(event,self = WordFrequencyApplication){
        self._event_manager.publish(["print", null])
    }
}
//Class to get the number of words with 'z'
var ZFrequency ={
    z_freq:0,//the number of words with letter 'z'
    _event_manager:null,
    _init:function(event_manager,self = ZFrequency){
        self._event_manager = event_manager
        self._event_manager.subscribe("valid_word",self.z_frequency_count)
        self._event_manager.subscribe("print",self.print_z_frequency)
    },
    z_frequency_count:function(event,self = ZFrequency){
        word = event[1]
        if(word.includes("z")){
            self.z_freq += 1
        }
    },
    print_z_frequency:function(event,self = ZFrequency){
        console.log("The count of words with 'z' is:"+self.z_freq)
    }
}
em = EventManager
DataStorage._init(em)
StopWordFilter._init(em)
WordFrequencyApplication._init(em)
WordFrequencyCounter._init(em)
ZFrequency._init(em)
em.publish(["run","../pride-and-prejudice.txt"])
//console.log(DataStorage._data)
