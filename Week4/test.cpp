#include <map>
#include <iostream>
#include <string>
#include <any>
#include <vector>
#include <typeinfo>
#include <functional>
using namespace std;
int main()
{
    map<string, any> Notebook;
    string name{ "Pluto" };
    int year = 2015;
      function<int(int,int)> add =[] (int a,int b)->int
    {
        return a+b;
    };
    vector<int> myvec = {1,2,3,4};
    Notebook = {
    {"PetName",name},
    {"Born",year},
    {"add", [](int a,int b)->int
    {
        return a+b;
    }
    }
    };
    string strS = any_cast<string>(Notebook["PetName"]); // = "Pluto"
    int intI = any_cast<int>(Notebook["Born"]); // = 2015
    auto mytest = any_cast<function<int(int,int)>>(Notebook["add"]);
    cout<<mytest(3,4)<<endl;
    cout<<strS<<endl;
    cout<<intI<<endl;
}