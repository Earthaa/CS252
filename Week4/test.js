var dict =
{
  me:dict,
  a:3,
  b:5,
  "test": function(){return a+b;},
  test2: function(){
    this.me = dict;
    console.log(dict);
  }
};

dict.test2()
console.log(dict["test"]())