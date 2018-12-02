//to partitialzie file
function partition(dataStr, startLine,nlines){
    var str = "";
    for(var i = 0; i < nlines; i++){
        if(i + startLine >= dataStr.length){
            break;
        }
        str = str + " " + dataStr[i + startLine];
    }
    return str;
}
function splitWords(dataStr){
    function scan(str_data){
        return str_data.replace(/[\W_]+/g," ").toLowerCase().split(" "); 
    }
    function removeStopWords(wordList){
        var fs = require("fs");
        var stopWords = fs.readFileSync("../stop_words.txt").toString("utf-8").split(",");
        stopWords.push("s");
        var nonStopWords = [];
        for(var i = 0; i <= wordList.length; i++){
            if(wordList[i] != undefined && wordList[i] != "" && !stopWords.includes(wordList[i])){
                nonStopWords.push(wordList[i]);
            }
        }
        return nonStopWords;
    }
    var result = [];
    var words = removeStopWords(scan(dataStr));
    for(var i = 0; i < words.length; i++){
        result.push([words[i],1]);
    }
    return result;
}
//regroup words alphabettically according to 31.3
function regroup(pairsList){
    var mapping = {
        "a-e":[],
        "f-j":[],
        "k-o":[],
        "p-t":[],
        "u-z":[]
    };
    for(var listNum = 0; listNum < pairsList.length; listNum++){
        for(var pairNum = 0; pairNum < pairsList[listNum].length;pairNum++){
            if(pairsList[listNum][pairNum][0][0] <="e")
                mapping["a-e"].push(pairsList[listNum][pairNum])
            else if(pairsList[listNum][pairNum][0][0] <="j")
                mapping["f-j"].push(pairsList[listNum][pairNum])
            else if(pairsList[listNum][pairNum][0][0] <="o")
                mapping["k-o"].push(pairsList[listNum][pairNum])
            else if(pairsList[listNum][pairNum][0][0] <="t")
                mapping["p-t"].push(pairsList[listNum][pairNum])
            else
                mapping["u-z"].push(pairsList[listNum][pairNum])
            }
    }
    return mapping;
}
function countWords(mapping){
    var freqsMap = {};
    for(var i = 0; i < mapping.length; i++){
       if(!(mapping[i][0] in freqsMap)){
           var freq = mapping
                    .filter(each => each[0] == mapping[i][0])
                    .reduce((sum,each)=>sum+each[1],0);
           freqsMap[mapping[i][0]] = freq;
       }
    }
    return freqsMap;
}
function combineFreqs(tmpFreq){
    for(word in tmpFreq){
        if(!(word in wordFreqs))
            wordFreqs[word] = tmpFreq[word]
    }
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
var fs = require("fs");
orignialData = fs.readFileSync("../pride-and-prejudice.txt").toString("utf-8").split('\n');
var dataStrs = [];
//partialize data
for(var startLine = 0; startLine < orignialData.length; startLine+=200){
    dataStrs.push(partition(orignialData, startLine,200));
}
var splits = dataStrs.map(splitWords);
splitAlphabetically = regroup(splits);
//extract the wordList in the dictionary
var wordList = [];
for(each in splitAlphabetically){
    wordList.push(splitAlphabetically[each]);
}
// //an array contains frequencies of each word accroding to the alphabet
var tmpFreqs = wordList.map(countWords);
//Combine each dictionary in tmpFreqs
var wordFreqs = {};
tmpFreqs.map(combineFreqs);
var sorted = sort(wordFreqs);
for(var i = 0; i < 25; i++) {
    console.log(sorted[i][0]+"  -  "+sorted[i][1]);
}