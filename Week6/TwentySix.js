//Here are some methods which will be used in the columns
var is_stop_word = (w) => {
    if(!stop_words[0].includes(w))
        return w;
    else
        return "";
}
var put_to_set = () =>{
    var mySet = new Set()
    for(var i = 0; i < non_stop_words[0].length; i++){
        if(non_stop_words[0][i] != ""){
            mySet.add(non_stop_words[0][i])
        }
    }
    return Array.from(mySet)
} 
var do_count = ()=>{
    var count = new Array(unique_words[0].length)
    for(var i = 0; i < non_stop_words[0].length; i++){
        if(non_stop_words[0][i] != ""){
            var index = unique_words[0].indexOf(non_stop_words[0][i])
            if(count[index] == undefined){
                count[index] = 1
            }
            else{count[index]++}
        }
    }
        return count;
}
var do_sort = ()=>{
    function do_zip(first,i){
        return [first,counts[0][i]]
    }
    var zipped = unique_words[0].map(do_zip)
    zipped.sort((a,b)=>{return -a[1]+b[1]})
    return zipped
}
//Here are the columns, each consists of data and method
var all_words = [[],null]
var stop_words = [[],null]
var non_stop_words = [[],()=>{return all_words[0].map(is_stop_word)}]
var unique_words = [[], put_to_set]
var counts = [[],do_count]
var sorted_data = [[],do_sort]
var all_columns = [all_words,stop_words,non_stop_words,unique_words,counts,sorted_data]
//update function, when a column has been changed then such function should be run
function update(){
    for(var i = 0; i < all_columns.length; i++){
        if(all_columns[i][1] != null){
            all_columns[i][0] = all_columns[i][1]()
        }
    }
}
var fs = require('fs');
all_words[0] = fs.readFileSync(process.argv[2]).toString("utf-8").replace(/[\W_]+/g," ").toLowerCase().split(" ");
stop_words[0] = fs.readFileSync("../stop_words.txt").toString("utf-8").split(",")
//add some letters in order not to be counted
stop_words[0].push("re")
stop_words[0].push("s")
stop_words[0].push("t")

update()
for(var i = 0; i < 25; i++){
    console.log(sorted_data[0][i][0] + "  -  " + sorted_data[0][i][1])
}