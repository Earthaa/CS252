var extract_words = function(path_to_file){
    var fs = require('fs');
    var all_words = fs.readFileSync(path_to_file).toString("utf-8").replace(/[\W_]+/g," ").toLowerCase().split(" ");
    var stop_words = fs.readFileSync("../stop_words.txt").toString("utf-8").split(",");
    stop_words.push("re");
    stop_words.push("s");
    stop_words.push("t");
    var words = [];
    for(var i = 0; i < all_words.length; i++){
        if(all_words[i]!="" && !stop_words.includes(all_words[i]))
            words.push(all_words[i]);
    }
    return words
}
var frequencies = function(word_list){
    word_freqs = {};
    for(i in word_list){
        w = word_list[i]
        if(w in word_freqs){
            word_freqs[w] += 1; 
        }
        else{
            word_freqs[w] = 1;
        }
    }
    return word_freqs;
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
//Aspect orienting programming 
var profile = function(f){
    return function (){
        console.time("Function" + f.name + "takes");
        res = f.apply(this,arguments);
        console.timeEnd("Function" + f.name + "takes")
        return res;
    }
}

//Weave without change the originial code

extract_words = profile(extract_words);
frequencies = profile(frequencies);
sort = profile(sort);

var word_freqs = sort(frequencies(extract_words("../pride-and-prejudice.txt")))
for(var i = 0; i < 25; i++) {
        console.log(word_freqs[i][0]+"  -  "+word_freqs[i][1]);
}