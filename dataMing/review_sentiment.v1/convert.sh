#!/bin/bash



# use $? get the function return value
function conv()
{
    local file;
    echo $1
:<<eof
    fileList=$1
#    for file in $fileList
    do
#        echo $oldPath'/'$file
#        cat $oldPath'/'$file
        iconv -f gbk -t utf8 $oldPath'/'$file -o $newPath'/'$file
    done
eof
    #  $# Number of params, $@ Params
    echo $#
    while [ $# -gt 0 ]
    do
        file=$1
        iconv -f gbk -t utf8 $oldPath'/'$file -o $newPath'/'$file
        shift
    done
}

oldPath=train2
newPath=train2utf8

trainFileList=`ls train2`
#echo $trainFileList
conv $trainFileList

oldPath=test2
newPath=test2utf8

testFileList=`ls test2`

conv $testFileList

