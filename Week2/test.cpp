#include<iostream>
using namespace std;
function<string (int)> retFun(int y) {

    return [=](int x) { 
        int s = 10;
        return "x+y"; };
}
int main()
{
    cout<<retFun(4)(3)<<endl;
}