var TFQuarantine ={
    funcs:[],
    _init:function(func,self = TFQuarantine){
        self.funcs = [func]
    },
    bind:function(func,self = TFQuarantine){
        self.funcs.push(func)
    },
    execute:function(self = TFQuarantine){
         function guard_callable(v){  
             if(typeof v === "function"){
                 return v()
             }
             else{
                 return v
             }
        }
        value = () => {}
        for(i in self.funcs){
            func = self.funcs[i]
            value = func(guard_callable(value))
        }
    }
}
function get_input(arg){
    function _f(){
        return "../pride-and-prejudice.txt"
    }
    return _f
}
function extract_words(path_to_file){
   function _f(){
        var fs = require('fs');
        var buf = fs.readFileSync(path_to_file).toString("utf-8");
        data = buf.replace(/[\W_]+/g," ").toLowerCase().split(" ");
        return data
   } 
   return _f
}
function remove_stop_words(word_list){
    function _f(){
        var fs = require('fs');
        var stop_words = fs.readFileSync("../stop_words.txt").toString("utf-8").split(",");
        stop_words.push("re")
        stop_words.push("s")
        stop_words.push("t")
        words = []
        for(i in word_list){
            w = word_list[i]
            if(!stop_words.includes(w)){
                words.push(w)
            }
        }
        return words
    }
    return _f
}
function frequencies(word_list){
    word_freqs = {}
    for(i in word_list){
        w = word_list[i]
        if(w in word_freqs){
            word_freqs[w] += 1 
        }
        else{
            word_freqs[w] = 1
        }
    }
    return word_freqs
}
function sort(word_freq){
    var items = Object.keys(word_freq).map(
        function(key) {
        return [key, word_freq[key]];
      }
      );
      items.sort(function(first, second) {
        return second[1] - first[1];
      });
      return items;
}
//This function has been changed acrroding to the 24.3
//The style is still obeyed after the change
function top25_freqs(word_freqs){
    for(var i = 0; i < 25; i++)
    {
        console.log(word_freqs[i][0]+"  -  "+word_freqs[i][1]);
    }
}
TFQuarantine._init(get_input)
TFQuarantine.bind(extract_words)
TFQuarantine.bind(remove_stop_words)
TFQuarantine.bind(frequencies)
TFQuarantine.bind(sort)
TFQuarantine.bind(top25_freqs)
TFQuarantine.execute()