# Markdown Parser

## 概述

这是一用于解析Markdown的Java程序，其主要目的还是为了服务于一个Web页面，所以这个仓库里的程序**能不能**完成，我也不知道。。。

顺便，这个`README.md`也用来做解析测试，所以在这个`README.md`里，我会*尽量多*的使用markdown的语法。

但是为了方便测试和Debug，我不会在这个文件里写太多内容。实现详情请参考源码。

## 程序说明

### 用法***(暂时提供)***

虽然这个程序是用于Web的，但是为了方便测试，这里还是会提供一个运行的方法：

```shell
java <class-name> <markdown-filename>
```

或者

```shell
java -jar <pkg> <markdown-filename>
```

具体用法，还是看怎么生成、打包程序。

### 程序功能

程序会按照一些~~编译原理~~完成其功能，主要完成如下工作：

1. 词法分析（Tokenizer）
2. 语法分析
2. 语义分析

> 上面的排序列表有序号错误，需要程序进行自动匹配
> 
> 这个引用里也有一些小陷阱，
> 程序也需要能够分辨
>

### 其他

这里想不到放什么了，放个表格吧：

| head one: left 1      | head two: left 2 | head three: right | head four: middle |
| ------ | :------ | ------: | :-----: | 
|value 1| value 2| value 3 |value 4 | 123123 | asdcasd|
|value 1 | value 2 |
| value 1


上面的表格里，包含了：

* 表格基本元素
* 不同的表格对齐方式
    * 靠左
    * 靠右
    * 居中
    

