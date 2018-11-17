function test(str){
    function f(){
        console.log(str)
    }
    return f
}
test("abc")()