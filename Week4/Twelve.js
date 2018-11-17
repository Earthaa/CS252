function extract_words(obj,path_to_file)
{
    var fs = require('fs');
    var buf = fs.readFileSync(path_to_file).toString("utf-8");
    arr = buf.replace(/[\W_]+/g," ").toLowerCase().split(" ");
    obj['data'] = arr;
}
function load_stop_words(obj)
{
    var fs = require('fs');
    obj['stop_words'] = fs.readFileSync("../stop_words.txt").toString("utf-8").split(',');
    obj['stop_words'].push('s');
    obj['stop_words'].push('re');
    obj['stop_words'].push('t');
}
function increment_count(obj,w)
{
    if(!(w in obj['freqs']))
    {
        obj['freqs'][w] = 1;
    }
    else
    {  
        obj['freqs'][w] += 1;
    }
}
var data_storage_obj = {
        me: data_storage_obj,
        "data": [],
        "init": function(path_to_file){this.me = data_storage_obj;extract_words(this.me,path_to_file);},
        "words": function(){return this.me['data']}
};
var stop_words_obj ={
        me:stop_words_obj,
        "stop_words":[],
        "init":function(){this.me = stop_words_obj, load_stop_words(this.me);},
        "is_stop_word":function(word){return this.me["stop_words"].includes(word);}
}
stop_words_obj.me = stop_words_obj;
var word_freqs_obj = {
    me:word_freqs_obj,
    "freqs":{},
    "increment_count": function(w){this.me = word_freqs_obj;increment_count(this.me,w);},
    "sorted": function(){
        this.me = word_freqs_obj;
        var items = Object.keys(word_freqs_obj["freqs"]).map(
            function(key) {
            return [key, word_freqs_obj["freqs"][key]];
          }
          );
          items.sort(function(first, second) {
            return second[1] - first[1];
          });
          return items;
    }
}
data_storage_obj['init'](process.argv[2]);
stop_words_obj['init']();
for(var i = 0; i < data_storage_obj['words']().length; i++)
{
    w = data_storage_obj['words']()[i];
    if(!(stop_words_obj['is_stop_word'](w)))
    {
        word_freqs_obj['increment_count'](w);
    }
}
word_freqs_obj['top25'] = function(){
    var word_freqs = this.me['sorted']();
    for(var i = 0; i < 25; i++)
    {
        console.log(word_freqs[i][0]+"  -  "+word_freqs[i][1]);
    }
}
word_freqs_obj["top25"]();