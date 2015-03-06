#!/bin/python 

f1=open("test2.list",'r')
f2=open("result",'r');
f3=open("newcon",'w')

line1 = f1.readline();
line2 = f2.readline();
while line1:
    print line1;

    if(line2[0:1]=='0'):
        target = '+1';
    else:
        target = '-1';
    f3.write( line1.strip()+","+target+"\n")
    
    line1 = f1.readline();
    line2 = f2.readline();


f1.close();
f2.close();
f3.close();

